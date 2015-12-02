package cn.togeek.netty;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

import org.restlet.engine.util.StringUtils;

abstract public class AbstractTransportService<T extends AbstractBootstrap<T, ? extends Channel>> {
   abstract protected T getBootstrap();

   protected void init(Settings settings) throws SettingsException {
      boolean tcpNoDelay = settings.getAsBoolean("TCP_NODELAY", false);
      boolean tcpKeepAlive = settings.getAsBoolean("SO_KEEPALIVE", false);

      if(tcpNoDelay) {
         getBootstrap().option(ChannelOption.TCP_NODELAY, tcpNoDelay);
      }

      if(tcpKeepAlive) {
         getBootstrap().option(ChannelOption.SO_KEEPALIVE, tcpKeepAlive);
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_SNDBUF"))) {
         getBootstrap().option(ChannelOption.SO_SNDBUF,
            settings.getAsInt("SO_SNDBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_RCVBUF"))) {
         getBootstrap().option(ChannelOption.SO_RCVBUF,
            settings.getAsInt("SO_RCVBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_BACKLOG"))) {
         getBootstrap().option(ChannelOption.SO_BACKLOG,
            settings.getAsInt("SO_BACKLOG", 50));
      }
   }
   
   abstract public void startService(Settings settings) throws Exception;
}
