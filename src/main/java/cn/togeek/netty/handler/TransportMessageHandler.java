package cn.togeek.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import cn.garden.util.Tool;

import cn.togeek.domain.Plant;
import cn.togeek.netty.NettyTransport;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.TransportRequest;
import cn.togeek.netty.message.TransportResponse;

public class TransportMessageHandler extends ChannelHandlerAdapter {
   private NettyTransport transport;

   public TransportMessageHandler(NettyTransport transport) {
      this.transport = transport;
   }

   @Override
   public void channelRead(final ChannelHandlerContext ctx, Object msg)
      throws Exception
   {
      if(msg instanceof Plant) {
         transport.addChildChannel(((Plant) msg).getId(), ctx.channel());
         return;
      }

      if(msg instanceof TransportRequest) {
         TransportRequest request = (TransportRequest) msg;
         final Context context = new Context();
         context.getParameters().add("readTimeout", "180000");
         ClientResource resource = new ClientResource(context, request.getUrl());
         // resource.getRequest().getCookies().add(getCookie());
         String method = request.getMethod();
         Representation rep = null;

         if(Tool.equals(method, Method.GET.getName())) {
            rep = resource.get();
         }
         else if(Tool.equals(method, Method.POST.getName())) {
            rep = resource.post(request.getPayload(),
               MediaType.APPLICATION_JSON);
         }
         else if(Tool.equals(method, Method.PUT.getName())) {
            rep = resource
               .put(request.getPayload(), MediaType.APPLICATION_JSON);
         }
         else if(Tool.equals(method, Method.DELETE.getName())) {
            rep = resource.delete();
         }

         try {
            TransportResponse response = new TransportResponse(
               request.getRequestId());
            String text = rep.getText();
            text = text == null ? "" : text;
            response.setPayload(text);
            ctx.writeAndFlush(TransportorHelper.getTransportor(response));
         }
         catch(Exception e) {
            e.printStackTrace();
         }
      }

      if(msg instanceof TransportResponse) {
         TransportResponse response = (TransportResponse) msg;
         transport.processResponse(response.getResponseId(),
            response.getPayload());

         return;
      }

      super.channelRead(ctx, msg);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      transport.removeChildChannel(ctx.channel());
   }
}