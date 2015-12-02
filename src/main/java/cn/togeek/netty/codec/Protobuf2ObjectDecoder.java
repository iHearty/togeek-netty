package cn.togeek.netty.codec;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;

import cn.togeek.netty.message.Transport.Transportor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

public class Protobuf2ObjectDecoder extends ProtobufDecoder {
   private ObjectMapper mapper = new ObjectMapper();

   public Protobuf2ObjectDecoder(MessageLite prototype) {
      super(prototype);
   }

   public Protobuf2ObjectDecoder(MessageLite prototype,
      ExtensionRegistry extensionRegistry)
   {
      super(prototype, extensionRegistry);
   }

   public Protobuf2ObjectDecoder(MessageLite prototype,
      ExtensionRegistryLite extensionRegistry)
   {
      super(prototype, extensionRegistry);
   }

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
      List<Object> out) throws Exception
   {
      super.decode(ctx, msg, out);

      List<Object> list = new ArrayList<>();

      for(Object obj : out) {
         if(obj instanceof Transportor) {
            Transportor transportor = (Transportor) obj;
            String clz = transportor.getClazz();

            try {
               Class<?> cls = Class.forName(clz);
               Object nobj = mapper.readValue(transportor.getJson(), cls);
               list.add(nobj);
            }
            catch(Exception ex) {
               throw ex;
            }
         }
      }

      out.clear();
      out.addAll(list);
   }
}