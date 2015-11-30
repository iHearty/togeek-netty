package cn.togeek.netty.helper;

import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

import cn.garden.util.UUIDUtil;
import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class TransportorHelper {
   public static Transportor getInitTransportor() {
      Entity.Builder ebuilder = Entity.newBuilder();
      Entity entity = ebuilder.setPayload(1 + "").build();

      Transportor.Builder tbuilder = Transportor.newBuilder();
      tbuilder.setType(TransportType.CHANNEL_INIT).setEntity(entity);

      return tbuilder.build();
   }

   public static Transportor getTransportor(TransportType type) {
      Transportor.Builder tbuilder = Transportor.newBuilder();
      tbuilder.setType(type);

      return tbuilder.build();
   }

   public static Transportor getTransportor(Request request) throws Exception {
      // 是否代理给python服务处理
      Form form = request.getResourceRef().getQueryAsForm();
      boolean proxy = "true".equals(form.getFirstValue("proxy"));

      // a.移除trans，否则会造成死循环。
      // b.将host设置成localhost，因为请求会被代理到电厂外网机，外网机上会启一个服务来处理该请求
      form.removeFirst("trans");

      Reference ref = request.getResourceRef().clone();
      ref.setScheme("http");

      if(proxy) {
         form.removeFirst("proxy");

         if(ref.getSegments().size() > 0) {
            List<String> segments = ref.getSegments();
            segments.remove(0);
            ref.setSegments(segments);
         }

         ref.setHostPort(52501);
      }
      else {
         ref.setHostPort(52500);
      }

      ref.setHostDomain("127.0.0.1");
      ref.setQuery(form.getQueryString());
      
      return getTransportor(TransportType.DDX_REQ, ref.toString(), request
         .getMethod().getName(), request.getEntity());
   }

   public static Transportor getTransportor(TransportType type, String url,
      String method, Representation msg) throws Exception
   {
      url = "http://192.168.0.112:52500/powerMobileApp/config/features/zone/3";
      
      Entity.Builder ebuilder = Entity.newBuilder();
      ebuilder.setUrl(url).setMethod(method);
System.out.println(msg.getClass() + " yyyyyyyyyyy");
      
      if(msg != null && msg.getText() != null) {
         ebuilder.setPayload(msg.getText());
      }
      
      Entity entity = ebuilder.build();
      String uuid = UUIDUtil.getUUID();

      Engine.getLogger(TransportorHelper.class)
         .log(Level.INFO, "生成请求ID: " + uuid);

      Transportor.Builder tbuilder = Transportor.newBuilder();
      tbuilder.setTransportId(uuid).setType(type).setEntity(entity);

      return tbuilder.build();
   }
}
