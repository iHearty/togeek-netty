package cn.togeek.netty2.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.restlet.Context;
import org.restlet.data.Cookie;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import cn.garden.util.Tool;

import cn.togeek.netty.message.Transport.Entity;
import cn.togeek.netty.message.Transport.TransportType;
import cn.togeek.netty.message.Transport.Transportor;

public class RequestHelper {
   private static int MAX_BUSINESS_THREAD = 64;

   private static ExecutorService es = Executors
      .newFixedThreadPool(MAX_BUSINESS_THREAD);

   public static void doRequest(final Transportor transportor) {
      es.submit(new Runnable() {
         @Override
         public void run() {
            final Context context = new Context();
            context.getParameters().add("readTimeout", "180000");
            Entity entity = transportor.getEntity();
            ClientResource resource = new ClientResource(context, entity
               .getUrl());
            resource.getRequest().getCookies().add(getCookie());
            String method = entity.getMethod();
System.out.println("AAA 1 --> " + entity.getUrl());
            Representation rep = new StringRepresentation("NaN");//null;

            if(Tool.equals(method, Method.GET.getName())) {
               rep = resource.get();
            }
//            else if(Tool.equals(method, Method.POST.getName())) {
//               rep = resource.post(entity.getPayload(),
//                  MediaType.APPLICATION_JSON);
//            }
//            else if(Tool.equals(method, Method.PUT.getName())) {
//               rep = resource.put(entity.getPayload(),
//                  MediaType.APPLICATION_JSON);
//            }
//            else if(Tool.equals(method, Method.DELETE.getName())) {
//               rep = resource.delete();
//            }

            try {
               String text = rep.getText();
               text = text == null ? "" : text;
               Entity.Builder eb = entity.toBuilder().setPayload(text);
               Transportor.Builder b = transportor.toBuilder()
                  .setType(TransportType.DDX_RES).setEntity(eb.build());
System.out.println("AAA 2 --> " + entity.getUrl() + " <> " + text);
               ClientWriteHelper.transport(b.build());
            }
            catch(Exception e) {
               e.printStackTrace();
            }
         }
      });
   }

   public static final String COOKIE_VALUE = "gaGp+QEW7/2TeoCssnBe5L71Mx/8imMSzf7mWXYV38qyynV2kaeSr1qhydXorJ/b";

   public static Cookie getCookie() {
      return new Cookie("local_setting", COOKIE_VALUE);
   }
}
