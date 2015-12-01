package cn.togeek.netty.helper;

import io.netty.channel.Channel;

import cn.togeek.netty.BootstrapWrapper;
import cn.togeek.netty.message.Transport.Transportor;

public class ClientWriteHelper {
   private static Channel writeChannel = null;

   private ClientWriteHelper() {
   }

   public static void transport(Transportor msg) throws Exception {
      if(writeChannel == null || !writeChannel.isActive()) {
         writeChannel = takeWriteChannel();
      }

      writeChannel.eventLoop().execute(new TransportTask(writeChannel, msg));
   }
   
   public static void freeChannel() {
      writeChannel = null;
   }
   
   private static Channel takeWriteChannel() throws Exception {
      return BootstrapWrapper.getWriteChannel();
   }

   private static class TransportTask implements Runnable {
      private Transportor msg;

      private Channel channel;

      public TransportTask(Channel channel, Transportor msg) {
         this.channel = channel;
         this.msg = msg;
      }

      @Override
      public void run() {
         channel.writeAndFlush(msg);
      }
   }
}
