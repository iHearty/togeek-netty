package cn.togeek.netty.message;

import java.util.concurrent.CountDownLatch;

import org.restlet.Request;
import org.restlet.Response;

public class CarrierWrapper {
   private CountDownLatch latch = null;
   
   private Carrier carrier = null;
   
   public CarrierWrapper() {
      this.latch = new CountDownLatch(1);
      carrier = new Carrier(System.identityHashCode(this) + "");
   }
   
   public Carrier getCarrier() {
      return carrier;
   }
   
   public void setCarrier(Carrier carrier) {
      this.carrier = carrier;
   }
   
   public CountDownLatch getCountDownLatch() {
      return latch;
   }
   
   public void taskComplete() {
      latch.countDown();
   }
   
   @Override
   public String toString() {
      return System.identityHashCode(this) + " -> " + carrier.toString();
   }
}
