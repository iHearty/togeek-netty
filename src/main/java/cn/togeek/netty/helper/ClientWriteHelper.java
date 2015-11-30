package cn.togeek.netty.helper;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.togeek.netty.BootstrapWrapper;
import cn.togeek.netty.WriteChannelWrapper;
import cn.togeek.netty.message.Transport.Transportor;

public class ClientWriteHelper {
   private static int MAX_WRITE_CHANNEL = 64;

   private static List<WriteChannelWrapper> writeChannels = new ArrayList<>();

   private static Queue<Transportor> msgQueue = new LinkedList<>();

   private static WriteReadyListener listener = new WriteReadyListener() {
      @Override
      public void ready() throws Exception {
         if(msgQueue.isEmpty()) {
            return;
         }
         
         Object msg = msgQueue.poll();

         if(msg == null) {
            return;
         }
         
         WriteChannelWrapper wc = takeWriteChannel();

         if(wc == null) {
            return;
         }
         
         wc.write(msg);
      }
   };

   private ClientWriteHelper() {
   }

   public static ChannelFuture transport(Transportor msg) throws Exception {
      WriteChannelWrapper wc = takeWriteChannel();
System.out.println("ClientWriteHelper ->" + wc + " " + System.identityHashCode(wc.channel()));
      if(wc == null) {
         msgQueue.offer(msg);
         return null;
      }

      return wc.writeAndFlush(msg);
   }

   private static WriteChannelWrapper takeWriteChannel() throws Exception {
      for(WriteChannelWrapper writeChannel : writeChannels) {
         if(!writeChannel.channel().isActive()) {
            writeChannels.remove(writeChannel);
            continue;
         }

         if(!writeChannel.isWriting()) {
            return writeChannel;
         }
      }

      if(writeChannels.size() >= MAX_WRITE_CHANNEL) {
         return null;
      }

      
      Channel channel = BootstrapWrapper.getWriteChannel();
      WriteChannelWrapper writeChannel = new WriteChannelWrapper(channel);
      writeChannel.addListener(listener);
      writeChannels.add(writeChannel);

      return writeChannel;
   }
}
