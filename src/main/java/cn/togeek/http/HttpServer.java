package cn.togeek.http;

import java.util.concurrent.CountDownLatch;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import cn.togeek.netty.ServerBootstrapWrapper;
import cn.togeek.netty.helper.LookupResponse;
import cn.togeek.netty.helper.TranspondHelper;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Transport.TransportType;
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
                  System.out.println("=============");
                  Transportor transportor = TransportorHelper
                     .getTransportor(request);
                  CountDownLatch latch = LookupResponse.register(
                     transportor.getTransportId(), response);
                  TranspondHelper.transpond(1, transportor);
                  latch.await();
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
            }
         });

         return router;
      }
   }
}
