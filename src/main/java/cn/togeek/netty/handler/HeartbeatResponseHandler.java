package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class HeartbeatResponseHandler extends ChannelHandlerAdapter {
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if((msg instanceof Transportor)
         && ((Transportor) msg).getType() == TransportType.HEARTBEAT_REQ)
      {
         ctx.writeAndFlush(TransportorHelper
            .getTransportor(TransportType.HEARTBEAT_RES));
         return;
      }

      super.channelRead(ctx, msg);
   }
}
