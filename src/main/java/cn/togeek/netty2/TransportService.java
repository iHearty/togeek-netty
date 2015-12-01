package cn.togeek.netty2;

import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Request;
import org.restlet.Response;

import cn.garden.util.UUIDUtil;
import cn.togeek.netty.helper.ServerTranspondHelper;
import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class TransportService {
   final ConcurrentHashMap<String, RequestHolder<Response>> clientHandlers = new ConcurrentHashMap<>();

   public void sendRequest(Request request, Listener<Response> listener) {
      // 1. 生成request id
      String requestId = UUIDUtil.getUUID();

      // 2. 将requestId放入map
      clientHandlers.put(requestId, new RequestHolder<>(listener));

      // 3. 获得电厂ID
      // User user = request.getClientInfo().getUser();
      // int plantId = ((PlantUser) user).getPlantId();
      int plantId = 1;

      Entity.Builder ebuilder = Entity.newBuilder();
      ebuilder.setUrl("http://127.0.0.1:9009/http/client").setMethod("GET");
      ebuilder.setPayload("server");
      Entity entity = ebuilder.build();
      Transportor.Builder tbuilder = Transportor.newBuilder();
      tbuilder.setTransportId(requestId).setType(TransportType.DDX_REQ)
         .setEntity(entity);
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