package cn.togeek.netty.message;

public class TransportResponse {
   private String responseId;

   private String payload;

   public TransportResponse(String responseId) {
      this.responseId = responseId;
   }

   public String getResponseId() {
      return this.responseId;
   }

   public void setResponseId(String responseId) {
      this.responseId = responseId;
   }

   public String getPayload() {
      return this.payload;
   }

   public void setPayload(String payload) {
      this.payload = payload;
   }

}
