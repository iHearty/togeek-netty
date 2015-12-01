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

public class HttpServer {
   public final static TransportService service = new TransportService();

   public static void main(String[] args) throws Exception {
      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9009);
      ServerApplication server = new ServerApplication();

      component.getDefaultHost().attach("/http", server);
      component.start();

      Settings settings = Settings.builder().put("comm.server.host", "0.0.0.0")
         .put("comm.server.port", 52400).put("comm.this.plantid", 1).build();

      NettyServerTransport.start(settings);
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
                  service.sendRequest(request, new Listener<Response>() {
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