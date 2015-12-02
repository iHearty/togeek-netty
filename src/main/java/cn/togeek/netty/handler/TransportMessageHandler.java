package cn.togeek.netty.handler;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import cn.garden.util.Tool;
import cn.togeek.netty.NettyTransport;
import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TransportMessageHandler extends ChannelHandlerAdapter {
   private NettyTransport transport;

   public TransportMessageHandler(NettyTransport transport) {
      this.transport = transport;
   }

   @Override
   public void channelRead(final ChannelHandlerContext ctx, Object msg)
      throws Exception {
      if(msg instanceof Transportor) {
         final Transportor transportor = (Transportor) msg;

         if(transportor.getType() == TransportType.CHANNEL_INIT) {
            int plantId = Integer.parseInt(transportor.getEntity()
               .getPayload());
            transport.addChildChannel(plantId, ctx.channel());
            return;
         }
         else if(transportor.getType() == TransportType.DDX_REQ) {
            ctx.channel().eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  final Context context = new Context();
                  context.getParameters().add("readTimeout", "180000");
                  Entity entity = transportor.getEntity();
                  ClientResource resource = new ClientResource(context, entity
                     .getUrl());
                  // resource.getRequest().getCookies().add(getCookie());
                  String method = entity.getMethod();

                  Representation rep = null;

                  if(Tool.equals(method, Method.GET.getName())) {
                     rep = resource.get();
                  }
                  else if(Tool.equals(method, Method.POST.getName())) {
                     rep = resource.post(entity.getPayload(),
                        MediaType.APPLICATION_JSON);
                  }
                  else if(Tool.equals(method, Method.PUT.getName())) {
                     rep = resource.put(entity.getPayload(),
                        MediaType.APPLICATION_JSON);
                  }
                  else if(Tool.equals(method, Method.DELETE.getName())) {
                     rep = resource.delete();
                  }

                  try {
                     String text = rep.getText();
                     text = text == null ? "" : text;
                     Entity.Builder eb = entity.toBuilder().setPayload(text);
                     Transportor.Builder b = transportor.toBuilder().setType(
                        TransportType.DDX_RES).setEntity(eb.build());
                     ctx.writeAndFlush(b.build());
                  }
                  catch(Exception e) {
                     e.printStackTrace();
                  }
               }
            });
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