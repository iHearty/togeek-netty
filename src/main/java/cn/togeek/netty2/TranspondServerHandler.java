package cn.togeek.netty2;

import cn.togeek.netty.helper.ServerTranspondHelper;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TranspondServerHandler extends ChannelHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
      if(msg instanceof Transportor) {
         Transportor transportor = (Transportor) msg;

         if(transportor.getType() == TransportType.CHANNEL_INIT) {
            int plantId = Integer.parseInt(transportor.getEntity()
               .getPayload());
            ServerTranspondHelper.addChannel(plantId, ctx.channel());
            return;
         }
         else if(transportor.getType() == TransportType.DDX_RES) {
            HttpServer.service.processResponse(transportor.getTransportId(),
               transportor.getEntity().getPayload());
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