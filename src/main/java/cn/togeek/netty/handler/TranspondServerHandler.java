package cn.togeek.netty.handler;

import cn.togeek.netty.util.TranspondUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TranspondServerHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      System.out.println("Receviced client channel...");
      TranspondUtil.addChannel(ctx.channel());
   }
}
