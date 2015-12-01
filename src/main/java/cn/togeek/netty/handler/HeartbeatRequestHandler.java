package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class HeartbeatRequestHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(TransportorHelper
         .getTransportor(TransportType.HEARTBEAT_REQ));
      super.channelActive(ctx);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if((msg instanceof Transportor)
         && ((Transportor) msg).getType() == TransportType.HEARTBEAT_RES)
      {
         ctx.executor().schedule(new HeartbeatTask(ctx), 20000,
            TimeUnit.MILLISECONDS);
         return;
      }

      super.channelRead(ctx, msg);
   }

   private class HeartbeatTask implements Runnable {
      private ChannelHandlerContext ctx = null;

      public HeartbeatTask(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      public void run() {
         ctx.writeAndFlush(TransportorHelper
            .getTransportor(TransportType.HEARTBEAT_REQ));
      }
   }
}
