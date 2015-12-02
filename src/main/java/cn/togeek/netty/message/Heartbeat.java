package cn.togeek.netty.message;

public class Heartbeat {
   public static final int TYPE_REQ = 1;

   public static final int TYPE_RES = 2;

   public static Heartbeat REQ = new Heartbeat(TYPE_REQ);

   public static Heartbeat RES = new Heartbeat(TYPE_RES);

   private int type = 1;

   public Heartbeat() {
   }

   private Heartbeat(int type) {
      this.type = type;
   }

   public int getType() {
      return this.type;
   }

   public void setType(int type) {
      this.type = type;
   }

   @Override
   public boolean equals(Object obj) {
      if(!(obj instanceof Heartbeat)) {
         return false;
      }

      return this.type == ((Heartbeat) obj).getType();
   }
}