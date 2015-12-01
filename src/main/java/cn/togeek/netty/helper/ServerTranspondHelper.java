package cn.togeek.netty.helper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.togeek.netty.message.Transport.Transportor;

public class ServerTranspondHelper {
   private static Map<Integer, ChannelId> channels = new HashMap<>();

   private static DefaultChannelGroup channelGrp = new DefaultChannelGroup(
      GlobalEventExecutor.INSTANCE);

   public static void addChannel(int plantId, Channel channel) {
      channelGrp.add(channel);
      channels.put(plantId, channel.id());
   }

   public static void removeChannel(Channel channel) {
      Set<Integer> keys = channels.keySet();

      for(Integer key : keys) {
         if(channel.id().equals(channels.get(key))) {
            channels.remove(key);
            return;
         }
      }
   }

   public static void transpond(final Integer plantId, Transportor msg) {
      channelGrp.writeAndFlush(msg, new ChannelMatcher() {
         @Override
         public boolean matches(Channel channel) {
            if(plantId == null) {
               return true;
            }
            
            ChannelId cid = channels.get(plantId);
            
            if(cid == null) {
               return false;
            }
            
            return channel.id().equals(cid);
         }
      });
   }
}
