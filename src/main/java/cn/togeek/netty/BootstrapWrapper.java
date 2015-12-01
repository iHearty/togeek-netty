package cn.togeek.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
import cn.togeek.netty.helper.ClientWriteHelper;
import cn.togeek.netty.message.Transport;

public class BootstrapWrapper {
   private static Channel sChannel = null;

   private static EventLoopGroup grp = null;

   private BootstrapWrapper() {
   }

   public static void startService() throws Exception {
      Bootstrap bootstrap = getBootstrap(false);

      try {
         sChannel = doConnect(bootstrap).sync().channel();
         sChannel.closeFuture().sync();
      }
      finally {
         grp.shutdownGracefully();
         sChannel = null;
      }
   }

   public static void stopService() {
      sChannel.close();
   }

   private static Bootstrap getBootstrap(final boolean forWrite) {
      Bootstrap bootstrap = new Bootstrap();

      if(grp == null) {
         grp = new NioEventLoopGroup();
      }

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
               // p.addLast(new IdleStateHandler(3, 0, 0));
               // p.addLast(new HeartbeatRequestHandler());
               // p.addLast(new UptimeClientHandler());

               if(!forWrite) {
                  p.addLast(new TranspondHandler());
               }
            };
         });

      return bootstrap;
   }

   private static ChannelFuture doConnect(Bootstrap bootstrap) {
      return bootstrap.connect("127.0.0.1", 8007);
   }

   public static Channel getWriteChannel() throws Exception {
      Bootstrap bootstrap = getBootstrap(true);
      final Channel channel = doConnect(bootstrap).sync().channel();

      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               channel.closeFuture().sync();
            }
            catch(InterruptedException e) {
               ClientWriteHelper.freeChannel();
               e.printStackTrace();
            }
         }
      }).start();

      return channel;
   }

   public static void main(String[] args) throws Exception {
      startService();
   }
}
