package cn.togeek.netty;

import java.net.InetSocketAddress;

import org.restlet.engine.util.StringUtils;

import cn.togeek.netty.handler.HeartbeatResponseHandler;
import cn.togeek.netty.handler.TranspondServerHandler;
import cn.togeek.netty.message.Transport;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServerTransport {
   public static void start(Settings settings) throws SettingsException {
      NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
      NioEventLoopGroup workGroup = new NioEventLoopGroup();

      ServerBootstrap bootstrap = new ServerBootstrap().group(boosGroup,
         workGroup).channel(NioServerSocketChannel.class);

      boolean tcpNoDelay = settings.getAsBoolean("TCP_NODELAY", false);
      boolean tcpKeepAlive = settings.getAsBoolean("SO_KEEPALIVE", false);

      if(tcpNoDelay) {
         bootstrap.option(ChannelOption.TCP_NODELAY, tcpNoDelay);
      }

      if(tcpKeepAlive) {
         bootstrap.option(ChannelOption.SO_KEEPALIVE, tcpKeepAlive);
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_SNDBUF"))) {
         bootstrap.option(ChannelOption.SO_SNDBUF, settings.getAsInt(
            "SO_SNDBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_RCVBUF"))) {
         bootstrap.option(ChannelOption.SO_RCVBUF, settings.getAsInt(
            "SO_RCVBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_BACKLOG"))) {
         bootstrap.option(ChannelOption.SO_BACKLOG, settings.getAsInt(
            "SO_BACKLOG", 50));
      }

      bootstrap.handler(new ParentChannelInitializer()).childHandler(
         new ChildChannelInitializer());

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

   private static class ParentChannelInitializer extends
      ChannelInitializer<ServerSocketChannel> {
      @Override
      protected void initChannel(ServerSocketChannel channel) throws Exception {
         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast(new LoggingHandler(LogLevel.INFO));
      }
   }

   private static class ChildChannelInitializer extends
      ChannelInitializer<SocketChannel> {
      @Override
      protected void initChannel(SocketChannel channel) throws Exception {
         ChannelPipeline pipeline = channel.pipeline();
         pipeline.addLast(new ProtobufVarint32FrameDecoder());
         pipeline.addLast(new ProtobufDecoder(Transport.Transportor
            .getDefaultInstance()));
         pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
         pipeline.addLast(new ProtobufEncoder());
         pipeline.addLast(new HeartbeatResponseHandler());
         pipeline.addLast(new TranspondServerHandler());
      }
   }
}