package cn.togeek.netty.message;

import org.restlet.Response;

public interface Listener<T extends Response> {
   public void onResponse(String entity);
}