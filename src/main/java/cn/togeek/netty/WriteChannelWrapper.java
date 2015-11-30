package cn.togeek.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

import java.util.EventListener;

import cn.togeek.netty.helper.WriteReadyListener;

public class WriteChannelWrapper {
   private boolean isWriting = false;

   private WriteReadyListener listener = null;

   private Channel channel = null;

   public WriteChannelWrapper(Channel channel) {
      this.channel = channel;
   }
   
   public void addListener(EventListener listener) {
      if(listener instanceof WriteReadyListener) {
         this.listener = (WriteReadyListener) listener;
      }
   }

   public boolean isWriting() {
      return isWriting;
   }

   private void fireReady() throws Exception {
      if(listener != null) {
         listener.ready();
      }
   }
   
   public Channel channel() {
      return this.channel;
   }
   
   public ChannelFuture write(Object msg) throws Exception {
      return write(msg, channel.newPromise());
   }

   public ChannelFuture write(Object msg, ChannelPromise promise)
      throws Exception
   {
      isWriting = true;
      ChannelFuture cf = channel.write(msg, promise);
      isWriting = false;
      fireReady();

      return cf;
   }

   public ChannelFuture writeAndFlush(Object msg) throws Exception {
      return writeAndFlush(msg, channel.newPromise());
   }

   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise)
      throws Exception
   {
      isWriting = true;
      ChannelFuture cf = channel.writeAndFlush(msg, promise);
      isWriting = false;
      fireReady();

      return cf;
   }
}
