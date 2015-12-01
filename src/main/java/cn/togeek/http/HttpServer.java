package cn.togeek.http;

import java.util.concurrent.CountDownLatch;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import cn.togeek.netty.ServerBootstrapWrapper;
import cn.togeek.netty.helper.LookupResponse;
import cn.togeek.netty.helper.ServerTranspondHelper;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.Transportor;

public class HttpServer {
   public static void main(String[] args) throws Exception {
      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9009);
      ServerApplication server = new ServerApplication();

      component.getDefaultHost().attach("/http", server);
      component.start();

      ServerBootstrapWrapper.startService();
   }

   public static class ServerApplication extends Application {
      @Override
      public Restlet createInboundRoot() {
         Router router = new Router();
         router.attach("/server", new Restlet() {
            public void handle(Request request, Response response) {
               try {
                  System.out.println("服务器接收请求:" + request.getResourceRef());
                  Transportor transportor = TransportorHelper
                     .getTransportor(request);
                  CountDownLatch latch = LookupResponse.register(
                     transportor.getTransportId(), response);
                  ServerTranspondHelper.transpond(1, transportor);
                  latch.await();
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
            }
         });
         
         router.attach("/client", new Restlet() {
            public void handle(Request request, Response response) {
               System.out.println("Client接收请求:" + request.getResourceRef());
               Form form = request.getResourceRef().getQueryAsForm();
               String s = form.getFirstValue("sleep");
               int sleep = s == null ? 0 : Integer.parseInt(s);
               
               try {
                  Thread.sleep(sleep * 1000);
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }
               
               response.setEntity(sleep + "", MediaType.TEXT_PLAIN);
            }
         });

         return router;
      }
   }
}
