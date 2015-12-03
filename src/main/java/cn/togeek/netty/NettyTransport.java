package cn.togeek.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.PlatformDependent;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.restlet.Request;
import org.restlet.Response;

import cn.garden.util.UUIDUtil;

import cn.togeek.netty.client.ClientTransportService;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Listener;
import cn.togeek.netty.message.Transport.Transportor;

public class NettyTransport {
   public static void main(String[] args) throws Exception {
      Settings settings =
         Settings.builder().put("comm.server.host", "0.0.0.0")
            .put("comm.server.port", 52400).put("comm.this.plantid", 1)
            .put("TCP_NODELAY", true).put("SO_KEEPALIVE", true)
            .put("SO_BACKLOG", 100).build();
      ClientTransportService.INSTANCE.start(settings);
   }
   
   public static final NettyTransport INSTANCE = new NettyTransport();
   
   private final ChildChannelGroup channels = new ChildChannelGroup(
      GlobalEventExecutor.INSTANCE);

   private final ConcurrentMap<String, RequestHolder<Response>> holders =
      PlatformDependent.newConcurrentHashMap();
   
   private NettyTransport() {
      
   }
   
   public void addChildChannel(int plantId, Channel channel) {
      channels.add(plantId, channel);
   }

   public void removeChildChannel(Channel channel) {
      channels.remove(channel);
   }

   public void sendRequest(Request request, Listener<Response> listener)
      throws Exception
   {
      // 1. 生成request id
      String requestId = UUIDUtil.getUUID();

      // 2. 将requestId放入map
      holders.put(requestId, new RequestHolder<>(listener));

      // 3. 获得电厂ID
      // User user = request.getClientInfo().getUser();
      // int plantId = ((PlantUser) user).getPlantId();
      int plantId = 1;

      Transportor transportor =
         TransportorHelper.getRequestTransportor(requestId, request);
      channels.find(plantId).writeAndFlush(transportor);
   }

   public void processResponse(String requestId, String entity) {
      RequestHolder<Response> holder = holders.remove(requestId);
      holder.listener().onResponse(entity);
   }

   private class ChildChannelGroup extends DefaultChannelGroup {
      public ChildChannelGroup(EventExecutor executor) {
         super(executor);
      }

      private final ConcurrentMap<Integer, ChannelId> mapping =
         PlatformDependent.newConcurrentHashMap();

      @Override
      @Deprecated
      public boolean add(Channel channel) {
         throw new UnsupportedOperationException(
            "use add(plantId, channel) instead");
      }

      public boolean add(int plantId, Channel channel) {
         boolean added = super.add(channel);

         if(added) {
            mapping.put(plantId, channel.id());
         }

         return added;
      }

      @Override
      public boolean remove(Object obj) {
         Set<Integer> keys = mapping.keySet();

         if(obj instanceof ChannelId) {
            for(Integer key : keys) {
               if(((ChannelId) obj).equals(mapping.get(key))) {
                  mapping.remove(key);
               }
            }
         }
         else if(obj instanceof Channel) {
            for(Integer key : keys) {
               if(((Channel) obj).id().equals(mapping.get(key))) {
                  mapping.remove(key);
               }
            }
         }

         return super.remove(obj);
      }

      public Channel find(int plantId) {
         ChannelId channelId = mapping.get(plantId);
         System.out.println(plantId + " -->> " + channelId + " " + System.identityHashCode(this));
         return super.find(channelId);
      }
   }

   private class RequestHolder<T extends Response> {
      private Listener<T> listener;

      RequestHolder(Listener<T> listener) {
         this.listener = listener;
      }

      public Listener<T> listener() {
         return listener;
      }
   }
}