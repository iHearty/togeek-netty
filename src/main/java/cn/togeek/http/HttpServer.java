package cn.togeek.http;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.routing.Router;

import cn.togeek.netty.ServerBootstrapUtil;
import cn.togeek.netty.util.TranspondUtil;

public class HttpServer {
   public static void main(String[] args) throws Exception {
      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9000);
      ServerApplication server = new ServerApplication();

      component.getDefaultHost().attach("/http", server);
      component.start();
      
      ServerBootstrapUtil.startService();
   }
   
   public static class ServerApplication extends Application {
      @Override
      public Restlet createInboundRoot() {
         Router r = new Router();
         r.attach("/server", new Restlet() {
            public void handle(Request request, Response response) {
               try {
                  TranspondUtil.transpond(null);
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
               
               response.setEntity(new JacksonRepresentation<>("11111"));
            }
         });
         
         return r;
      }
   }
}
