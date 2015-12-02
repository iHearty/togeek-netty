package cn.togeek.netty.handler;

import cn.togeek.netty.NettyServerTransport;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TransportMessageHandler extends ChannelHandlerAdapter {
   private NettyServerTransport transport;

   public TransportMessageHandler(NettyServerTransport transport) {
      this.transport = transport;
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception {
      if(msg instanceof Transportor) {
         Transportor transportor = (Transportor) msg;

         if(transportor.getType() == TransportType.CHANNEL_INIT) {
            int plantId = Integer.parseInt(transportor.getEntity()
               .getPayload());
            transport.addChildChannel(plantId, ctx.channel());
            return;
         }
         else if(transportor.getType() == TransportType.DDX_RES) {
            transport.processResponse(transportor.getTransportId(), transportor
               .getEntity().getPayload());
            return;
         }
      }

      super.channelRead(ctx, msg);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      transport.removeChildChannel(ctx.channel());
   }
}