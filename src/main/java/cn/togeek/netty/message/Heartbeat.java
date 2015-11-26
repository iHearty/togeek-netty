package cn.togeek.netty.message;

import java.io.Serializable;

public class Heartbeat implements Serializable {
   private static final long serialVersionUID = -1107061858776245809L;

   public static final int REQ = 1;
   
   public static final int RES = 2;
   
   private int type;
   
   protected Heartbeat() {
      
   }
   
   private Heartbeat(int type) {
      this.type = type;
   }
   
   public int getType() {
      return this.type;
   }
   
   public static Heartbeat getHeartbeat(int type) {
      return new Heartbeat(type);
   }
   
   @Override
   public String toString() {
      return System.identityHashCode(this) + " " + (this.type == 1 ? "REQ" : "RES");
   }
}
