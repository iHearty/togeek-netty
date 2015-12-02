package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Heartbeat;

public class HeartbeatRequestHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);

      ctx.writeAndFlush(TransportorHelper.getTransportor(Heartbeat.REQ));
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if((msg instanceof Heartbeat)
         && ((Heartbeat) msg).getType() == Heartbeat.HEARTBEAT_RES)
      {
         ctx.executor().schedule(new HeartbeatTask(ctx), 3, TimeUnit.SECONDS);
         return;
      }

      super.channelRead(ctx, msg);
   }

   private class HeartbeatTask implements Runnable {
      private ChannelHandlerContext ctx = null;

      public HeartbeatTask(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      @Override
      public void run() {
         try {
            ctx.writeAndFlush(TransportorHelper.getTransportor(Heartbeat.REQ));
         }
         catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
}
