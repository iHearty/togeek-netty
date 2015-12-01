package cn.togeek.test;

import java.io.IOException;
import java.util.Random;

import org.restlet.Context;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestHttpRequest {
   public static void main(String[] args) {
      final Random random = new Random();
      
      for(int i = 0; i < 10; i++) {
         final int idx = i;
         new Thread(new Runnable() {
            @Override
            public void run() {
               String url = "http://localhost:9009/http/server?index=" + idx + "&timestamp=";
               url += Math.random();
//               url += "&sleep=0" + random.nextInt(4);
               
               System.out.println("URL = " + url);
               final Context context = new Context();
               ClientResource resource = new ClientResource(context, url);
               long t1 = System.currentTimeMillis();
               Representation rep = resource.get();
               
               try {
                  System.out.println(idx + " 开始时间:" + t1 + 
                     ", 用时:" + (System.currentTimeMillis() - t1) +
                     ", 结果:" + rep.getText());
               }
               catch(IOException e) {
                  e.printStackTrace();
               }
            }
            
         }).start();
      }
      
   }
}
