package cn.togeek.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import cn.togeek.netty.handler.HeartbeatRequestHandler;
import cn.togeek.netty.handler.TranspondHandler;

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
                  p.addLast(new ObjectDecoder(ClassResolvers
                     .cacheDisabled(null)));
                  p.addLast(new ObjectEncoder());
                  p.addLast(new StringDecoder());
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
