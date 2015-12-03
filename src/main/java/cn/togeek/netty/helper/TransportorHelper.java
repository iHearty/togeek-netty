package cn.togeek.netty.helper;

import java.util.List;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.engine.util.StringUtils;
import org.restlet.representation.Representation;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.togeek.netty.message.Transport.Transportor;
import cn.togeek.netty.message.TransportRequest2;

public class TransportorHelper {
   private static ObjectMapper jm = new ObjectMapper();

   public static Transportor getTransportor(Object obj) throws Exception {
      Transportor.Builder tbuilder = Transportor.newBuilder();
      tbuilder.setClazz(obj.getClass().getName()).setJson(
         jm.writeValueAsString(obj));

      return tbuilder.build();
   }

   public static Transportor getRequestTransportor(String rid, Request request)
      throws Exception
   {
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

      TransportRequest2 req = new TransportRequest2(rid);
      req.setUrl(ref.toString().replace("server", "client"));
      req.setMethod(request.getMethod().getName());
      Representation msg = request.getEntity();

      String text = msg.getText();

      if(!StringUtils.isNullOrEmpty(text)) {
         req.setPayload(text);
      }

      return getTransportor(req);
   }
}
