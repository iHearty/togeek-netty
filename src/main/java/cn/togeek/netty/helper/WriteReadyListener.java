package cn.togeek.netty.helper;

import java.util.EventListener;

public interface WriteReadyListener extends EventListener {
   public void ready() throws Exception;
}
