package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import cn.togeek.netty.helper.LookupResponse;
import cn.togeek.netty.helper.ServerTranspondHelper;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class TranspondServerHandler extends ChannelHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if(msg instanceof Transportor) {
         Transportor transportor = (Transportor) msg;
         
         if(transportor.getType() == TransportType.CHANNEL_INIT) {
            int plantId = Integer.parseInt(transportor.getEntity().getPayload());
            ServerTranspondHelper.addChannel(plantId, ctx.channel());
            return;
         }
         System.out.println("SSS --> " + transportor.getTransportId());
         if(transportor.getType() == TransportType.DDX_RES) {
            LookupResponse.responseComplete(transportor);
            return;
         }
      }

      super.channelRead(ctx, msg);
   }
   
   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      ServerTranspondHelper.removeChannel(ctx.channel());
   }
}
