package cn.togeek.netty.message;

public class Heartbeat {
   public static final int HEARTBEAT_REQ = 1;

   public static final int HEARTBEAT_RES = 2;

   public static Heartbeat REQ = new Heartbeat(HEARTBEAT_REQ);

   public static Heartbeat RES = new Heartbeat(HEARTBEAT_RES);

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
}
