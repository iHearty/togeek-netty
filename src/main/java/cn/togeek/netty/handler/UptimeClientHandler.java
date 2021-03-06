package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class UptimeClientHandler extends ChannelHandlerAdapter {
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