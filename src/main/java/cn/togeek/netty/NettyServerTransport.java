package cn.togeek.netty;

import java.net.InetSocketAddress;

import cn.togeek.netty.handler.HeartbeatResponseHandler;
import cn.togeek.netty.handler.TranspondServerHandler;
import cn.togeek.netty.message.Transport;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServerTransport {
   public static void start(Settings settings) {
      NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
      NioEventLoopGroup workGroup = new NioEventLoopGroup();

      ServerBootstrap bootstrap = new ServerBootstrap().group(boosGroup,
         workGroup).channel(NioServerSocketChannel.class);

      String tcpNoDelay = settings.get("tcp_no_delay");
      String tcpKeepAlive = settings.get("tcp_keep_alive");

      if(!"default".equals(tcpNoDelay)) {
         bootstrap.option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(
            tcpNoDelay));
      }

      if(!"default".equals(tcpKeepAlive)) {
         bootstrap.option(ChannelOption.SO_KEEPALIVE, Boolean.parseBoolean(
            tcpNoDelay));
      }

      bootstrap.option(ChannelOption.SO_BACKLOG, 100).handler(
         new LoggingHandler(LogLevel.INFO)).childHandler(
            new ChannelInitializer<SocketChannel>() {
               @Override
               public void initChannel(SocketChannel ch) {
                  ChannelPipeline p = ch.pipeline();
                  p.addLast(new ProtobufVarint32FrameDecoder());
                  p.addLast(new ProtobufDecoder(Transport.Transportor
                     .getDefaultInstance()));
                  p.addLast(new ProtobufVarint32LengthFieldPrepender());
                  p.addLast(new ProtobufEncoder());
                  p.addLast(new HeartbeatResponseHandler());
                  p.addLast(new TranspondServerHandler());
               }
            });

      String host = settings.get("comm.server.host");
      int port = Integer.parseInt(settings.get("comm.server.port"));

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