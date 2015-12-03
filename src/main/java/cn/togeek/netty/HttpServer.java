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
      Settings settings =
         Settings.builder().put("comm.server.host", "0.0.0.0")
            .put("comm.server.port", 52400).put("comm.this.plantid", 1)
            .put("TCP_NODELAY", true).put("SO_KEEPALIVE", true)
            .put("SO_BACKLOG", 100).build();

      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9009);
      ServerApplication server = new ServerApplication();

      component.getDefaultHost().attach("/http", server);
      component.start();

      ServerTransportService.INSTANCE.start(settings);
   }

   public static class ServerApplication extends Application {
      @Override
      public Restlet createInboundRoot() {
         Router router = new Router();
         router.attach("/server", new Restlet() {
            @Override
            public void handle(Request request, final Response response) {
               final CountDownLatch latch = new CountDownLatch(1);

               try {
                  NettyTransport.INSTANCE.sendRequest(request,
                     new Listener<Response>() {
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
               response.setEntity("client", MediaType.TEXT_HTML);
            }
         });

         return router;
      }
   }
}