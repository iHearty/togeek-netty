package cn.togeek.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import cn.togeek.netty.NettyTransport;
import cn.togeek.netty.codec.Protobuf2ObjectDecoder;
import cn.togeek.netty.handler.CheckinServerHandler;
import cn.togeek.netty.handler.HeartbeatRequestHandler;
import cn.togeek.netty.handler.TransportMessageHandler;
import cn.togeek.netty.handler.UptimeClientHandler;
import cn.togeek.netty.message.Transport;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
   private NettyTransport transport;

   ClientInitializer(NettyTransport transport) {
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
      pipeline.addLast(new CheckinServerHandler());
      pipeline.addLast(new TransportMessageHandler(transport));
   }
}