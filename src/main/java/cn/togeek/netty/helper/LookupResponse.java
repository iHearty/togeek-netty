package cn.togeek.netty.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.restlet.Response;
import org.restlet.ext.jackson.JacksonRepresentation;

import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.Transportor;

public class LookupResponse {
   private static Map<String, ResonseWrapper> lookups = new HashMap<>();

   private LookupResponse() {
   }

   public static CountDownLatch register(String uuid, Response res) {
      CountDownLatch latch = new CountDownLatch(1);
      lookups.put(uuid, new ResonseWrapper(res, latch));

      return latch;
   }

   public static void responseComplete(Transportor transportor) {
      String uuid = transportor.getTransportId();
      ResonseWrapper resWrapper = lookups.get(uuid);
      
      if(resWrapper == null) {
         return;
      }
      
      Entity entity = transportor.getEntity();
      Response res = resWrapper.getResponse();
      res.setEntity(new JacksonRepresentation<>(entity.getPayload()));
      resWrapper.getConntdownLatch().countDown();
   }
   
   private static class ResonseWrapper {
      private Response res;

      private CountDownLatch latch;

      public ResonseWrapper(Response res, CountDownLatch latch) {
         this.res = res;
         this.latch = latch;
      }
      
      public Response getResponse() {
         return this.res;
      }
      
      public CountDownLatch getConntdownLatch() {
         return this.latch;
      }
   }
}
