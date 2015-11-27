package cn.togeek.http;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

import cn.togeek.netty.ServerBootstrapUtil;
import cn.togeek.netty.message.Carrier;
import cn.togeek.netty.message.CarrierWrapper;
import cn.togeek.netty.message.TransferCenter;
import cn.togeek.netty.util.TranspondUtil;

public class HttpServer {
   public static void main(String[] args) throws Exception {
      Component component = new Component();
      component.getServers().add(Protocol.HTTP, 9009);
      ServerApplication server = new ServerApplication();

      component.getDefaultHost().attach("/http", server);
      component.start();

      ServerBootstrapUtil.startService();
   }

   public static class ServerApplication extends Application {
      @Override
      public Restlet createInboundRoot() {
         Router router = new Router();
         router.attach("/server", new Restlet() {
            public void handle(Request request, Response response) {
               try {
                  CarrierWrapper carrierWrapper = new CarrierWrapper();
                  CountDownLatch latch = carrierWrapper.getCountDownLatch();
                  Carrier carrier = carrierWrapper.getCarrier();
                  String v = request.getResourceRef().getQueryAsForm()
                     .getFirstValue("a");
                  carrier.setBody(Arrays.asList(v));
                  System.out.println(v + " =============");
                  TranspondUtil.transpond(carrier);
                  TransferCenter.add(carrierWrapper.getCarrier().getUuid(),
                     carrierWrapper);

                  latch.await();

//                  if(rs) {
                     carrier = carrierWrapper.getCarrier();
                     response.setEntity(new JacksonRepresentation<>(carrier
                        .getBody()));
//                  }
//                  else {
//                     response.setStatus(Status.CONNECTOR_ERROR_COMMUNICATION);
//                  }
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
