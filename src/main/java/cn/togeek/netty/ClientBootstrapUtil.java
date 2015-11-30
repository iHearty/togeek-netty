package cn.togeek.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import cn.togeek.netty.handler.TranspondHandler;
import cn.togeek.netty.message.Transport;

public class ClientBootstrapUtil {
   public static void startService() throws Exception {
      EventLoopGroup grp = null;

      try {
         grp = new NioEventLoopGroup();
         Bootstrap bootstrap = new Bootstrap();
         bootstrap.group(grp).channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<SocketChannel>() {
               protected void initChannel(SocketChannel ch) throws Exception {
                  ChannelPipeline p = ch.pipeline();
                  p.addLast(new ProtobufVarint32FrameDecoder());
                  p.addLast(new ProtobufDecoder(Transport.Transportor
                     .getDefaultInstance()));
                  p.addLast(new ProtobufVarint32LengthFieldPrepender());
                  p.addLast(new ProtobufEncoder());
//                  p.addLast(new IdleStateHandler(3, 0, 0));
//                  p.addLast(new HeartbeatRequestHandler());
//                  p.addLast(new UptimeClientHandler());
                  p.addLast(new TranspondHandler());
               };
            });

         bootstrap.connect("127.0.0.1", 8007).sync()
            .channel().closeFuture().sync();
      }
      finally {
         grp.shutdownGracefully();
         startService();
      }
   }

   public static void main(String[] args) throws Exception {
      startService();
   }
}
