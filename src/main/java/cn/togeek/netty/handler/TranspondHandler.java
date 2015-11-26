package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class TranspondHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
   }
   
   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      System.out.println(" == Clinet === " + msg);
      if(msg instanceof List) {
         Thread.sleep(3000);
         List list = new ArrayList<String>();
         list.add("Hello 1");
         ctx.writeAndFlush(list);
      }
      
      super.channelRead(ctx, msg);
   }
}
