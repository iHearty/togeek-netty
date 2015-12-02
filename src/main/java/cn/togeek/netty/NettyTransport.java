package cn.togeek.netty;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.engine.util.StringUtils;

import cn.garden.util.UUIDUtil;
import cn.togeek.netty.codec.Protobuf2ObjectDecoder;
import cn.togeek.netty.handler.HeartbeatRequestHandler;
import cn.togeek.netty.handler.HeartbeatResponseHandler;
import cn.togeek.netty.handler.TransportMessageHandler;
import cn.togeek.netty.handler.UptimeClientHandler;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Listener;
import cn.togeek.netty.message.Transport;
import cn.togeek.netty.message.Transport.Transportor;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;

public class NettyTransport {
   public static void main(String[] args) throws Exception {
      Settings settings = Settings.builder().put("comm.server.host", "0.0.0.0")
         .put("comm.server.port", 52400).put("comm.this.plantid", 1)
         .put("TCP_NODELAY", true).put("SO_KEEPALIVE", true)
         .put("SO_BACKLOG", 100).build();
      new NettyTransport().startClient(settings);
   }

   private final ChildChannelGroup channels = new ChildChannelGroup(
      GlobalEventExecutor.INSTANCE);

   private final ConcurrentMap<String, RequestHolder<Response>> holders = PlatformDependent
      .newConcurrentHashMap();

   public void addChildChannel(int plantId, Channel channel) {
      channels.add(plantId, channel);
   }

   public void removeChildChannel(Channel channel) {
      channels.remove(channel);
   }

   public void sendRequest(Request request, Listener<Response> listener)
      throws Exception
   {
      // 1. 生成request id
      String requestId = UUIDUtil.getUUID();

      // 2. 将requestId放入map
      holders.put(requestId, new RequestHolder<>(listener));

      // 3. 获得电厂ID
      // User user = request.getClientInfo().getUser();
      // int plantId = ((PlantUser) user).getPlantId();
      int plantId = 1;

      Transportor transportor = TransportorHelper
         .getRequestTransportor(requestId, request);
      channels.find(plantId).writeAndFlush(transportor);
   }

   public void processResponse(String requestId, String entity) {
      RequestHolder<Response> holder = holders.remove(requestId);
      holder.listener().onResponse(entity);
   }

   public void startServer(Settings settings) throws SettingsException {
      NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
      NioEventLoopGroup workGroup = new NioEventLoopGroup();

      ServerBootstrap bootstrap = new ServerBootstrap()
         .group(boosGroup, workGroup).channel(NioServerSocketChannel.class);

      boolean tcpNoDelay = settings.getAsBoolean("TCP_NODELAY", false);
      boolean tcpKeepAlive = settings.getAsBoolean("SO_KEEPALIVE", false);

      if(tcpNoDelay) {
         bootstrap.option(ChannelOption.TCP_NODELAY, tcpNoDelay);
      }

      if(tcpKeepAlive) {
         bootstrap.option(ChannelOption.SO_KEEPALIVE, tcpKeepAlive);
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_SNDBUF"))) {
         bootstrap.option(ChannelOption.SO_SNDBUF,
            settings.getAsInt("SO_SNDBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_RCVBUF"))) {
         bootstrap.option(ChannelOption.SO_RCVBUF,
            settings.getAsInt("SO_RCVBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_BACKLOG"))) {
         bootstrap.option(ChannelOption.SO_BACKLOG,
            settings.getAsInt("SO_BACKLOG", 50));
      }

      bootstrap.handler(new ServerParentChannelInitializer())
         .childHandler(new ServerChildChannelInitializer(this));

      String host = settings.get("comm.server.host");
      int port = settings.getAsInt("comm.server.port", 52400);

      try {
         bootstrap.bind(new InetSocketAddress(host, port)).sync().channel()
            .closeFuture().sync();
      }
      catch(Exception e) {
         throw new RuntimeException("Failed to bind to [" + port + "]", e);
      }
      finally {
         boosGroup.shutdownGracefully();
         workGroup.shutdownGracefully();
      }
   }

   public void startClient(final Settings settings) throws SettingsException {
      String host = settings.get("comm.server.host");
      int port = settings.getAsInt("comm.server.port", 52400);

      NioEventLoopGroup workGroup = new NioEventLoopGroup();

      try {
         clientBootstrap(settings, workGroup)
            .connect(new InetSocketAddress(host, port)).sync().channel()
            .closeFuture().sync();
      }
      catch(Exception e) {
         throw new RuntimeException(
            "Failed to connect to [" + host + ", " + port + "]", e);
      }
      finally {
         workGroup.shutdownGracefully();

         workGroup.schedule(new Runnable() {
            @Override
            public void run() {
               try {
                  startClient(settings);
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }, 60, TimeUnit.SECONDS);
      }
   }

   private Bootstrap clientBootstrap(Settings settings,
      NioEventLoopGroup workGroup) throws SettingsException
   {
      Bootstrap bootstrap = new Bootstrap().group(workGroup)
         .channel(NioSocketChannel.class);

      boolean tcpNoDelay = settings.getAsBoolean("TCP_NODELAY", false);
      boolean tcpKeepAlive = settings.getAsBoolean("SO_KEEPALIVE", false);

      if(tcpNoDelay) {
         bootstrap.option(ChannelOption.TCP_NODELAY, tcpNoDelay);
      }

      if(tcpKeepAlive) {
         bootstrap.option(ChannelOption.SO_KEEPALIVE, tcpKeepAlive);
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_SNDBUF"))) {
         bootstrap.option(ChannelOption.SO_SNDBUF,
            settings.getAsInt("SO_SNDBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_RCVBUF"))) {
         bootstrap.option(ChannelOption.SO_RCVBUF,
            settings.getAsInt("SO_RCVBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_BACKLOG"))) {
         bootstrap.option(ChannelOption.SO_BACKLOG,
            settings.getAsInt("SO_BACKLOG", 50));
      }

      bootstrap.handler(new ClientChildChannelInitializer(this));

      return bootstrap;
   }

   private class ServerParentChannelInitializer
      extends ChannelInitializer<ServerSocketChannel> {
      @Override
      protected void initChannel(ServerSocketChannel channel) throws Exception {
         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast(new LoggingHandler(LogLevel.INFO));
      }
   }

   private class ServerChildChannelInitializer
      extends ChannelInitializer<SocketChannel> {
      private NettyTransport transport;

      ServerChildChannelInitializer(NettyTransport transport) {
         this.transport = transport;
      }

      @Override
      protected void initChannel(SocketChannel channel) throws Exception {
         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast(new ProtobufVarint32FrameDecoder());
         pipeline.addLast(new Protobuf2ObjectDecoder(
            Transport.Transportor.getDefaultInstance()));
         pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
         pipeline.addLast(new ProtobufEncoder());
         pipeline.addLast(new HeartbeatResponseHandler());
         pipeline.addLast(new TransportMessageHandler(transport));
      }
   }

   private class ChildChannelGroup extends DefaultChannelGroup {
      public ChildChannelGroup(EventExecutor executor) {
         super(executor);
      }

      private final ConcurrentMap<Integer, ChannelId> mapping = PlatformDependent
         .newConcurrentHashMap();

      @Override
      @Deprecated
      public boolean add(Channel channel) {
         throw new UnsupportedOperationException(
            "use add(plantId, channel) instead");
      }

      public boolean add(int plantId, Channel channel) {
         boolean added = super.add(channel);

         if(added) {
            mapping.put(plantId, channel.id());
         }

         return added;
      }

      @Override
      public boolean remove(Object obj) {
         Set<Integer> keys = mapping.keySet();

         if(obj instanceof ChannelId) {
            for(Integer key : keys) {
               if(((ChannelId) obj).equals(mapping.get(key))) {
                  mapping.remove(key);
               }
            }
         }
         else if(obj instanceof Channel) {
            for(Integer key : keys) {
               if(((Channel) obj).id().equals(mapping.get(key))) {
                  mapping.remove(key);
               }
            }
         }

         return super.remove(obj);
      }

      public Channel find(int plantId) {
         ChannelId channelId = mapping.get(plantId);
         return super.find(channelId);
      }
   }

   private class RequestHolder<T extends Response> {
      private Listener<T> listener;

      RequestHolder(Listener<T> listener) {
         this.listener = listener;
      }

      public Listener<T> listener() {
         return listener;
      }
   }

   private class ClientChildChannelInitializer
      extends ChannelInitializer<SocketChannel> {
      private NettyTransport transport;

      ClientChildChannelInitializer(NettyTransport transport) {
         this.transport = transport;
      }

      @Override
      protected void initChannel(SocketChannel channel) throws Exception {
         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast(new ProtobufVarint32FrameDecoder());
         pipeline.addLast(new Protobuf2ObjectDecoder(
            Transport.Transportor.getDefaultInstance()));
         pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
         pipeline.addLast(new ProtobufEncoder());
         pipeline.addLast(new IdleStateHandler(60, 0, 0));
         pipeline.addLast(new HeartbeatRequestHandler());
         pipeline.addLast(new UptimeClientHandler());
         pipeline.addLast(new TransportMessageHandler(transport));
      };
   }
}