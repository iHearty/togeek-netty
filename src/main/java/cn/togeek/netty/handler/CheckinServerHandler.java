package cn.togeek.netty.handler;

import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Initializer;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class CheckinServerHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      Initializer init = new Initializer();
      init.setPlantId(1);
      ctx.writeAndFlush(TransportorHelper.getTransportor(init));
      
      ctx.fireChannelActive();
   }
}