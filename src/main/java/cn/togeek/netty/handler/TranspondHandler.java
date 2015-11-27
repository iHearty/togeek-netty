package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;

import java.util.List;

import cn.togeek.netty.message.Carrier;

public class TranspondHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
   }
   
   @Override
   public void channelRead(final ChannelHandlerContext ctx, final Object msg)
      throws Exception
   {
      super.channelRead(ctx, msg);
      System.out.println(" == Clinet === " + msg);
      if(msg instanceof Carrier) {
//         EventLoop evtloop = ctx.channel().eventLoop();
//         evtloop.submit(new Runnable() {
//            @Override
//            public void run() {
               Object v = ((Carrier) msg).getBody();
//               System.out.println(v + " XXXXXXXXXX " + ("2".equals(((List) v).get(0))));
//               long id = Thread.currentThread().getId();
//               System.out.println(" = UUUUUUUUUUUUU " + id);
//               try {
                  if("2".equals(((List) v).get(0))) {
                     Thread.sleep(120000);
                  }
                  else {
                     Thread.sleep(50000);
                  }
                  
                  ctx.writeAndFlush(msg);
//               }
//               catch(InterruptedException e) {
//                  // TODO Auto-generated catch block
//                  e.printStackTrace();
//               }
//               
//            }
//         });
      }
   }
}
