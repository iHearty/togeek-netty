package cn.togeek.netty.handler;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Heartbeat;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartbeatResponseHandler extends ChannelHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      System.out.println("HeartbeatResponseHandler -> " + msg);
      if(Heartbeat.REQ.equals(msg)) {
         ctx.writeAndFlush(TransportorHelper.getTransportor(Heartbeat.RES));
         return;
      }

      super.channelRead(ctx, msg);
   }
}