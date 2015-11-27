package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import cn.togeek.netty.message.Carrier;
import cn.togeek.netty.message.CarrierWrapper;
import cn.togeek.netty.message.TransferCenter;
import cn.togeek.netty.util.TranspondUtil;

public class TranspondServerHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      System.out.println("Receviced client channel...");
      TranspondUtil.addChannel(ctx.channel());
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if(!(msg instanceof Carrier)) {
         super.channelRead(ctx, msg);
         return;
      }

      Carrier carrier = (Carrier) msg;
      CarrierWrapper cWrapper = TransferCenter.get(carrier.getUuid());
      cWrapper.setCarrier(carrier);
      cWrapper.getCountDownLatch().countDown();
   }
}
