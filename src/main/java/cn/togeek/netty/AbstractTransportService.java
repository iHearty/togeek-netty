package cn.togeek.netty;

import org.restlet.engine.util.StringUtils;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

public abstract class AbstractTransportService<T extends AbstractBootstrap<T, ? extends Channel>> {
   protected T bootstrap;
   protected abstract void init(Settings settings) throws SettingsException;

   protected T options(Settings settings) throws SettingsException {
      boolean tcpNoDelay = settings.getAsBoolean("TCP_NODELAY", false);
      boolean tcpKeepAlive = settings.getAsBoolean("SO_KEEPALIVE", false);

      if(tcpNoDelay) {
         bootstrap.option(ChannelOption.TCP_NODELAY, tcpNoDelay);
      }

      if(tcpKeepAlive) {
         bootstrap.option(ChannelOption.SO_KEEPALIVE, tcpKeepAlive);
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_SNDBUF"))) {
         bootstrap.option(ChannelOption.SO_SNDBUF,
            settings.getAsInt("SO_SNDBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_RCVBUF"))) {
         bootstrap.option(ChannelOption.SO_RCVBUF,
            settings.getAsInt("SO_RCVBUF", 8192));
      }

      if(!StringUtils.isNullOrEmpty(settings.get("SO_BACKLOG"))) {
         bootstrap.option(ChannelOption.SO_BACKLOG,
            settings.getAsInt("SO_BACKLOG", 50));
      }

      return bootstrap;
   }

   public abstract void start(Settings settings) throws Exception;
}
