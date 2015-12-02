package cn.togeek.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.togeek.netty.AbstractTransportService;
import cn.togeek.netty.Settings;
import cn.togeek.netty.SettingsException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientTransportService
   extends AbstractTransportService<Bootstrap> {
   public static final ClientTransportService INSTANCE =
      new ClientTransportService();

   private ScheduledExecutorService scheduleExecutor =
      Executors.newScheduledThreadPool(1);

   private NioEventLoopGroup workGroup;

   private ClientTransportService() {
      super();
   }

   @Override
   protected void init(Settings settings) throws SettingsException {
      this.bootstrap = new Bootstrap();
      workGroup = new NioEventLoopGroup();
      options(settings).group(workGroup).channel(NioSocketChannel.class)
         .handler(new ClientInitializer(transport));
   }

   @Override
   public void start(final Settings settings) throws Exception {
      init(settings);
      
      String host = settings.get("comm.server.host");
      int port = settings.getAsInt("comm.server.port", 52400);

      try {
         bootstrap.connect(new InetSocketAddress(host, port)).sync().channel()
            .closeFuture().sync();
      }
      catch(Exception e) {
         throw new RuntimeException(
            "Failed to connect to [" + host + ", " + port + "]", e);
      }
      finally {
         workGroup.shutdownGracefully();

         scheduleExecutor.schedule(new Runnable() {
            @Override
            public void run() {
               try {
                  start(settings);
               }
               catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }, 30, TimeUnit.SECONDS);
      }
   }
}