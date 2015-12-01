package cn.togeek.netty2.message;

import org.restlet.Response;

public interface Listener<T extends Response> {
   public void onResponse(String entity);
}