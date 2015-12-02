package cn.togeek.netty.message;

public class TransportRequest {
   private String requestId;

   private String url;

   private String method;

   private String payload;

   public TransportRequest(String requestId) {
      this.requestId = requestId;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }

   public String getUrl() {
      return this.url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getMethod() {
      return this.method;
   }

   public void setMethod(String method) {
      this.method = method;
   }

   public String getPayload() {
      return this.payload;
   }

   public void setPayload(String payload) {
      this.payload = payload;
   }
}
