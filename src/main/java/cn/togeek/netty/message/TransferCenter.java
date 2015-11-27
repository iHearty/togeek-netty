package cn.togeek.netty.message;

import java.util.HashMap;
import java.util.Map;

public class TransferCenter {
   private static final Map<String, CarrierWrapper> transfers = new HashMap<>();
   
   public static void add(String uuid, CarrierWrapper cWraper) {
      transfers.put(uuid, cWraper);
   }
   
   public static CarrierWrapper get(String uuid) {
      return transfers.get(uuid);
   }
}
