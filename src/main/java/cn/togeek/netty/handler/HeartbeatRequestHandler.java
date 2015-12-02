package cn.togeek.netty.handler;

import java.util.concurrent.TimeUnit;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class HeartbeatRequestHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);

      ctx.writeAndFlush(TransportorHelper.getTransportor(
         TransportType.HEARTBEAT_REQ));
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
      if((msg instanceof Transportor) && ((Transportor) msg)
         .getType() == TransportType.HEARTBEAT_RES) {
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
         ctx.writeAndFlush(TransportorHelper.getTransportor(
            TransportType.HEARTBEAT_REQ));
      }
   }
}
