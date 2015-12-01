package cn.togeek.netty2;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import cn.togeek.netty.helper.RequestHelper;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class TranspondHandler extends ChannelHandlerAdapter {
   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(TransportorHelper.getInitTransportor());
   }

   @Override
   public void channelRead(final ChannelHandlerContext ctx, final Object msg)
      throws Exception
   {

      if(!(msg instanceof Transportor)
         || ((Transportor) msg).getType() != TransportType.DDX_REQ)
      {
         super.channelRead(ctx, msg);
         return;
      }

      if(!isValid((Transportor) msg)) {
         return;
      }
    
      RequestHelper.doRequest((Transportor) msg);
   }

   private boolean isValid(Transportor transportor) {
      Entity entity = transportor.getEntity();
      String url = entity.getUrl();
      String method = entity.getMethod();

      if(url.isEmpty() || method.isEmpty()) {
         return false;
      }
      
      return true;
   }
}
