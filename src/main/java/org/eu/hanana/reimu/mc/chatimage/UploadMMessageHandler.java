package org.eu.hanana.reimu.mc.chatimage;

import io.netty.buffer.*;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

import javax.rmi.CORBA.Util;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UploadMMessageHandler implements IMessageHandler<UploadMMessage, IMessage> {

    @Override
    public IMessage onMessage(UploadMMessage message, MessageContext ctx) {
        if (ctx.side.isServer()){
            if (message.o.startsWith("start")){
                UploadManager.UpLoads.put(ctx.getServerHandler().player.getEntityId(),new HashMap<>());
                UploadManager.UpLoadsSize.put(ctx.getServerHandler().player.getEntityId(),message.a);
            }
            if (message.o.startsWith("finish")){
                boolean ok=false;
                int ok1 = 0;
                Map<Integer, byte[]> uploaddata = UploadManager.UpLoads.get(ctx.getServerHandler().player.getEntityId());
                int maxd=0;
                for (Integer i : uploaddata.keySet()) {
                    maxd=Math.max(i,maxd);
                }
                if (maxd==UploadManager.UpLoadsSize.get(ctx.getServerHandler().player.getEntityId())-1){
                    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                    for (int i = 0; i <= maxd; i++) {
                        byte[] bytet = uploaddata.get(i);
                        buf.writeBytes(bytet);
                    }
                    byte[] fbyte = new byte[buf.capacity()];
                    buf.getBytes(0,fbyte);


                    int fn = 0;
                    if (!new File("chatimages/").exists())
                        new File("chatimages/").mkdir();
                    while (new File("chatimages/"+fn).exists())
                        fn++;
                    Utils.WriteFile("chatimages/"+fn,fbyte);
                    ok=true;
                    ok1=fn;
                }else {
                    ChatImageMod.logger.error("Error Data");
                }
                UploadManager.UpLoads.remove(ctx.getServerHandler().player.getEntityId());
                UploadManager.UpLoadsSize.remove(ctx.getServerHandler().player.getEntityId());
                if (ok)
                    return new UploadMMessage(100000001,String.valueOf(ok1));
                return new UploadMMessage(100000000,"ok");
            }
            return new UploadMMessage(0,"ok");
        }else {
            if (message.a==100000001){
                Utils.SvReply = Integer.valueOf(message.o);
            }
            Utils.OK=true;
            return null;
        }
    }

}
