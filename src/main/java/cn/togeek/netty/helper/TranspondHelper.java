package cn.togeek.netty.helper;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.togeek.netty.message.Transport.Transportor;

public class TranspondHelper {
   private static Map<Integer, Channel> channels = new HashMap<>();
   
   public static void addChannel(int plantId, Channel channel) {
      channels.put(plantId, channel);
   }
   
   public static void removeChannel(Channel channel) {
      Set<Integer> keys = channels.keySet();
      
      for(Integer key : keys) {
         if(channels.get(key) == channel) {
            channels.remove(key);
            
            return;
         }
      }
   }
   
   public static void transpond(int plantId, Transportor msg) throws Exception {
      Channel channel = channels.get(plantId);
      
      if(channel == null || !channel.isActive()) {
         return;
      }
      
      channel.writeAndFlush(msg);
   }
}
