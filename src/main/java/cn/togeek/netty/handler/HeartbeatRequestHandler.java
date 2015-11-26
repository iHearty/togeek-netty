package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

import cn.togeek.netty.message.Heartbeat;

public class HeartbeatRequestHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(Heartbeat.getHeartbeat(Heartbeat.REQ));
      super.channelActive(ctx);
   }
   
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if((msg instanceof Heartbeat)
         && ((Heartbeat) msg).getType() == Heartbeat.RES)
      {
         System.out.println(System.identityHashCode(ctx.channel()) + " HeartbeatRequestHandler.channelRead ");
         ctx.executor().schedule(new HeartbeatTask(ctx), 20000,
            TimeUnit.MILLISECONDS);
      }
      
      super.channelRead(ctx, msg);
   }
   
   private class HeartbeatTask implements Runnable {
      private ChannelHandlerContext ctx = null;

      public HeartbeatTask(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      public void run() {
         ctx.writeAndFlush(Heartbeat.getHeartbeat(Heartbeat.REQ));
      }
   }
}
