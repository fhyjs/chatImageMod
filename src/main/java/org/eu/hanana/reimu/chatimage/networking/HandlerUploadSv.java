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
import org.eu.hanana.reimu.chatimage.ChatimageMod;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                byte[][] byteArrays = UPLOAD_BUFFER.get(context.player());
                // 计算合并后的字节数组长度
                int totalLength = 0;
                for (byte[] array : byteArrays) {
                    totalLength += array.length;
                }

                // 创建合并后的字节数组
                byte[] mergedArray = new byte[totalLength];

                // 将每个子数组的内容复制到合并后的字节数组中
                int currentIndex = 0;
                for (byte[] array : byteArrays) {
                    System.arraycopy(array, 0, mergedArray, currentIndex, array.length);
                    currentIndex += array.length;
                }

                UPLOAD_BUFFER.remove(context.player());

                File file = new File(".","chatimage");
                if (!file.exists()) file.mkdirs();
                if (!file.isDirectory()){
                    file.delete();
                    file.mkdirs();
                }
                try {
                    int length = Objects.requireNonNull(file.listFiles()).length;
                    file=new File(file, String.valueOf(length));
                    Files.write(file.toPath(),mergedArray);
                    context.reply(new PayloadUpload("OK",0,String.valueOf(length).getBytes(StandardCharsets.UTF_8)));
                }catch (Exception e){
                    ChatimageMod.logger.error(e);
                }
            }
        }
    }
}
