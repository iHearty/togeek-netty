package cn.togeek.netty;

import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Request;
import org.restlet.Response;

import cn.garden.util.UUIDUtil;

import cn.togeek.netty.helper.ServerTranspondHelper;
import cn.togeek.netty.helper.TransportorHelper;
import cn.togeek.netty.message.Listener;
import cn.togeek.netty.message.Transport.Transportor;

public class TransportService {
   final ConcurrentHashMap<String, RequestHolder<Response>> clientHandlers = new ConcurrentHashMap<>();

   public void sendRequest(Request request, Listener<Response> listener)
      throws Exception
   {
      // 1. 生成request id
      String requestId = UUIDUtil.getUUID();

      // 2. 将requestId放入map
      clientHandlers.put(requestId, new RequestHolder<>(listener));

      // 3. 获得电厂ID
      // User user = request.getClientInfo().getUser();
      // int plantId = ((PlantUser) user).getPlantId();
      int plantId = 1;

      Transportor.Builder tbuilder = TransportorHelper.getTransportor(request)
         .toBuilder().setTransportId(requestId);
      ServerTranspondHelper.transpond(plantId, tbuilder.build());
   }

   public void processResponse(String requestId, String entity) {
      RequestHolder<Response> holder = clientHandlers.remove(requestId);
      holder.listener().onResponse(entity);
   }

   static class RequestHolder<T extends Response> {
      private Listener<T> listener;

      RequestHolder(Listener<T> listener) {
         this.listener = listener;
      }

      public Listener<T> listener() {
         return listener;
      }
   }
}