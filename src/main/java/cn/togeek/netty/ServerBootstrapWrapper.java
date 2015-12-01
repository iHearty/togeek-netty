package cn.togeek.netty;

import cn.togeek.netty.handler.TranspondServerHandler;
import cn.togeek.netty.message.Transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ServerBootstrapWrapper {
   private static Channel ssChannel = null;

   private static ServerBootstrap bootstrap = null;

   private static EventLoopGroup bGrp = null;

   private static EventLoopGroup wGrp = null;

   private ServerBootstrapWrapper() {
   }

   private static void init() {
      bGrp = new NioEventLoopGroup();
      wGrp = new NioEventLoopGroup();

      bootstrap = new ServerBootstrap();
      bootstrap.group(bGrp, wGrp).channel(NioServerSocketChannel.class).option(
         ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(
            LogLevel.INFO)).childHandler(
               new ChannelInitializer<SocketChannel>() {
                  @Override
                  public void initChannel(SocketChannel ch) {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new ProtobufVarint32FrameDecoder());
                     p.addLast(new ProtobufDecoder(Transport.Transportor
                        .getDefaultInstance()));
                     p.addLast(new ProtobufVarint32LengthFieldPrepender());
                     p.addLast(new ProtobufEncoder());
                     // p.addLast(new HeartbeatResponseHandler());
                     p.addLast(new TranspondServerHandler());
                  }
               });
   }

   public static void startService() throws Exception {
      if(bootstrap == null) {
         init();
      }

      try {
         ssChannel = bootstrap.bind(8007).sync().channel();
         ssChannel.closeFuture().sync();
      }
      finally {
         bGrp.shutdownGracefully();
         wGrp.shutdownGracefully();
         ssChannel = null;
      }
   }

   public static void stopService() {
      ssChannel.close();
   }

   public static void main(String[] args) throws Exception {
      startService();
   }
}
