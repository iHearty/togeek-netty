package cn.togeek.netty.handler;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Initializer;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class UptimeClientHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(final ChannelHandlerContext ctx) throws Exception {
      Initializer init = new Initializer();
      init.setPlantId(1);
      ctx.writeAndFlush(TransportorHelper.getTransportor(init));
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
      throws Exception
   {
      if(!(evt instanceof IdleStateEvent)) {
         return;
      }

      ctx.channel().close();
   }
}