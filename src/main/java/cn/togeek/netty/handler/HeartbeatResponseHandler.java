package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

import cn.togeek.netty.message.Heartbeat;

public class HeartbeatResponseHandler extends ChannelHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      System.out.println("RES " + msg + " " + System.identityHashCode(ctx.channel()));
      if((msg instanceof Heartbeat)
         && ((Heartbeat) msg).getType() == Heartbeat.REQ)
      {
         ctx.writeAndFlush(Heartbeat.getHeartbeat(Heartbeat.RES));
      }
      
      super.channelRead(ctx, msg);
   }
}
