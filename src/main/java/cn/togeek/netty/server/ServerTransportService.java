package cn.togeek.netty.server;

import java.net.InetSocketAddress;

import cn.togeek.netty.AbstractTransportService;
import cn.togeek.netty.Settings;
import cn.togeek.netty.SettingsException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerTransportService extends
   AbstractTransportService<ServerBootstrap> {

   public static final ServerTransportService INSTANCE =
      new ServerTransportService();

   private NioEventLoopGroup boosGroup;

   private NioEventLoopGroup workGroup;

   private ServerTransportService() {
      super();
   }

   @Override
   protected void init(Settings settings) throws SettingsException {
      this.bootstrap = new ServerBootstrap();
      boosGroup = new NioEventLoopGroup(1);
      workGroup = new NioEventLoopGroup();
      options(settings).group(boosGroup, workGroup)
         .channel(NioServerSocketChannel.class)
         .handler(new ServerInitializer())
         .childHandler(new ServerChildInitializer(transport));
   }

   @Override
   public void start(Settings settings) throws SettingsException {
      init(settings);

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
}