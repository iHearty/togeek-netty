package cn.togeek.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import cn.togeek.netty.codec.Protobuf2ObjectDecoder;
import cn.togeek.netty.handler.HeartbeatResponseHandler;
import cn.togeek.netty.message.Transport;

public class ServerChildInitializer extends ChannelInitializer<SocketChannel> {
   @Override
   protected void initChannel(SocketChannel channel) throws Exception {
      ChannelPipeline pipeline = channel.pipeline();
      pipeline.addLast(new ProtobufVarint32FrameDecoder());
      pipeline.addLast(new Protobuf2ObjectDecoder(Transport.Transportor
         .getDefaultInstance()));
      pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
      pipeline.addLast(new ProtobufEncoder());
      pipeline.addLast(new HeartbeatResponseHandler());
//      pipeline.addLast(new TransportMessageHandler(transport));
   }

}
