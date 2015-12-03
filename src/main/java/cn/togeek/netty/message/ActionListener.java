package cn.togeek.netty.message;

/**
 * A listener for action responses or failures.
 */
public interface ActionListener<Response> {
   /**
    * A response handler.
    */
   void onResponse(Response response);

   /**
    * A failure handler.
    */
   void onFailure(Throwable e);
}