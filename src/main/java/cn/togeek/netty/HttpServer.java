package cn.togeek.netty;

import java.util.concurrent.CountDownLatch;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Router;

import cn.togeek.netty.message.Listener;
import cn.togeek.netty.server.ServerTransportService;

public class HttpServer {
   public static void main(String[] args) throws Exception {
      new HttpServer();
   }

   HttpServer() throws Exception {
      Settings settings = Settings.builder().put("comm.server.host", "0.0.0.0")
         .put("comm.server.port", 52400).put("comm.this.plantid", 1).put(
            "TCP_NODELAY", true).put("SO_KEEPALIVE", true).put("SO_BACKLOG",
               100).build();

      NettyTransport transport = new NettyTransport();

      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9009);
      ServerApplication server = new ServerApplication(transport);

      component.getDefaultHost().attach("/http", server);
      component.start();

      ServerTransportService.INSTANCE.start(settings);
   }

   public static class ServerApplication extends Application {
      private NettyTransport transport;

      ServerApplication(NettyTransport transport) {
         this.transport = transport;
      }

      @Override
      public Restlet createInboundRoot() {
         Router router = new Router();
         router.attach("/server", new Restlet() {
            @Override
            public void handle(Request request, final Response response) {
               final CountDownLatch latch = new CountDownLatch(1);

               try {
                  transport.sendRequest(request, new Listener<Response>() {
                     @Override
                     public void onResponse(String entity) {
                        response.setEntity(new StringRepresentation(entity));
                        latch.countDown();
                     }
                  });

                  latch.await();
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
               finally {
                  latch.countDown();
               }
            }
         });

         router.attach("/client", new Restlet() {
            @Override
            public void handle(Request request, Response response) {
               try {
                  Thread.sleep(10000);
               }
               catch(InterruptedException e) {
                  e.printStackTrace();
               }

               response.setEntity("client", MediaType.TEXT_HTML);
            }
         });

         return router;
      }
   }
}