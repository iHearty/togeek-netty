package cn.togeek.netty.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Carrier implements Serializable {
   private static final long serialVersionUID = -1570647400353233300L;

   private String uuid = null;

   private Map<String, Object> headers = null;

   private Object body;

   public Carrier(String uuid) {
      this.uuid = uuid;
      this.headers = new HashMap<>();
   }

   public String getUuid() {
      return uuid;
   }

   public void addHeader(String key, Object val) {
      headers.put(key, val);
   }

   public void removeHeader(String key) {
      headers.remove(key);
   }

   public Object getHeader(String key) {
      return headers.get(key);
   }

   public Object getBody() {
      return this.body;
   }

   public void setBody(Object body) {
      this.body = body;
   }
}
