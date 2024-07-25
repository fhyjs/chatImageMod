package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledDirectByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HandlerUploadSv {
    public final static Map<Player, byte[][]> UPLOAD_BUFFER = new HashMap<>();
    public static void handleData(final PayloadUpload data, final IPayloadContext context) {
        String opt = data.opt();
        if (opt.equals("START")){
            UPLOAD_BUFFER.put(context.player(),new byte[data.pos()][]);
            context.reply(new PayloadUpload("R-START",0,new byte[0]));
        }
        if (opt.equals("UPLOAD")){
            byte[][] bytes = UPLOAD_BUFFER.get(context.player());
            bytes[data.pos()]= data.bytes();
            UPLOAD_BUFFER.put(context.player(),bytes);
            var ok = true;
            for (byte[] aByte : bytes) {
                if (aByte==null){
                    ok=false;
                }
            }
            if (ok){
                context.reply(new PayloadUpload("OK",0,new byte[0]));
            }
        }
    }
}
