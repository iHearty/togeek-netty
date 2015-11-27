package cn.togeek.netty.util;

import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import cn.togeek.netty.message.Carrier;

public class TranspondUtil {
   private static ChannelGroup channelGrp = new DefaultChannelGroup(
      new DefaultEventLoop());

   public static void addChannel(Channel channel) {
      channelGrp.add(channel);
   }

   public static void transpond(Carrier msg) throws Exception {
      System.out.println("TranspondUtil: " + msg);
      channelGrp.writeAndFlush(msg);
   }
}
