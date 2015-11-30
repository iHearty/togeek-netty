package cn.togeek.netty;

import io.netty.bootstrap.ServerBootstrap;
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
import cn.togeek.netty.handler.TranspondServerHandler;
import cn.togeek.netty.message.Transport;

public class ServerBootstrapUtil {
   private static ServerBootstrap bootstrap;

   private static EventLoopGroup bGrp = new NioEventLoopGroup(1);

   private static EventLoopGroup wGrp = new NioEventLoopGroup();

   public static void startService() throws Exception {
      try {
         bootstrap = new ServerBootstrap();
         bootstrap.group(bGrp, wGrp).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
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

         bootstrap.bind(8007).sync().channel().closeFuture().sync();
      }
      finally {
         bGrp.shutdownGracefully();
         wGrp.shutdownGracefully();
      }
   }

   public static void main(String[] args) throws Exception {
      startService();
   }
}
