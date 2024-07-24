package org.eu.hanana.reimu.chatimage.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatImage {
    public static final Map<String,ChatImage> BufferedChatImage = new HashMap<>();
    public static final Map<String,ResourceLocation> BufferedTexture = new HashMap<>();
    public URL url;
    public int w,h;
    public String info;
    public ImageStatus status;
    private ChatImage(){
        status=ImageStatus.NEW;
    }
    private ChatImage(String url,int w,int h,String info) throws MalformedURLException {
        this();
        this.url= URI.create(url).toURL();
        this.w=w;
        this.h=h;
        this.info=info;
    }
    public static ChatImage getChatImage(String ciCode) throws Throwable {
        if (!BufferedChatImage.containsKey(ciCode)){
            ChatImageData chatImageData = new ChatImageData(ciCode);
            BufferedChatImage.put(ciCode,new ChatImage(chatImageData.url,chatImageData.w,chatImageData.h,chatImageData.info));
        }
        return BufferedChatImage.get(ciCode);
    }

    public static void clearCache() {
        for (ResourceLocation value : BufferedTexture.values()) {
            if (FMLEnvironment.dist.isClient()){
                AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(value);
                if (texture instanceof DynamicTexture dynamicTexture){
                    dynamicTexture.close();
                }
            }
        }
        BufferedTexture.clear();
        BufferedChatImage.clear();
    }
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture() {
        if (status==ImageStatus.OK&&BufferedTexture.containsKey(url.toString())){
            return BufferedTexture.get(url.toString());
        }else if (status==ImageStatus.NEW){
            status=ImageStatus.WAIT;
            new Thread(this::downloadImg).start();
        }
        return null;
    }
    @OnlyIn(Dist.CLIENT)
    public void downloadImg(){
        try {
            URLConnection urlConnection = url.openConnection();
            // 读取输入流
            try (InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                    outputStream.write(dataBuffer, 0, bytesRead);
                }

                byte[] abyte = outputStream.toByteArray();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(abyte));

                // 创建一个新的图片缓冲区，用于存放缩放后的图片
                BufferedImage outputImage = new BufferedImage(w, h, image.getType());

                // 获取Graphics2D对象，用于在图片上绘制
                Graphics2D g2d = outputImage.createGraphics();
                // 设置渲染质量（高质量）
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                // 执行缩放操作
                g2d.drawImage(image, 0, 0, w, h, null);

                // 释放资源
                g2d.dispose();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(outputImage,"png",byteArrayOutputStream);
                abyte=byteArrayOutputStream.toByteArray();

                ByteBuffer bytebuffer = MemoryUtil.memAlloc(abyte.length);
                NativeImage read;
                try {
                    read = NativeImage.read(bytebuffer.put(abyte).flip());
                } catch (IOException ioexception) {
                    throw new IOException(ioexception);
                } finally {
                    MemoryUtil.memFree(bytebuffer);
                }
                ResourceLocation resourcelocation = ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "dynamic/ci/" + BufferedTexture.size());
                Minecraft.getInstance().getTextureManager().register(resourcelocation, new DynamicTexture(read));
                BufferedTexture.put(url.toString(), resourcelocation);
                status=ImageStatus.OK;
            }
        } catch (IOException e) {
            ChatimageMod.logger.error(e);
            status=ImageStatus.ERROR;
            if (FMLEnvironment.dist.isClient()){
                Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"),Component.literal(e.toString())));
            }
        }
    }
    public static class ChatImageData{
        @Expose
        public String url;
        @Expose
        public int w,h;
        @Expose
        public String info;
        public ChatImageData(String url,int w,int h,String info) {
            this.url= url;
            this.w=w;
            this.h=h;
            this.info=info;
        }
        public ChatImageData(ChatImage ci) {
            this(ci.url.toString(), ci.w, ci.h, ci.info);
        }
        public ChatImageData(String ciCode) {
            ciCode=ciCode.substring(2);
            ChatImageData chatImageData = gson.fromJson(ciCode, ChatImageData.class);
            this.url=chatImageData.url;
            this.w=chatImageData.w;
            this.h=chatImageData.h;
            this.info=chatImageData.info;
        }
        private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        @Override
        public String toString() {
            return toCiCode();
        }
        @Nullable
        public static String[] getCiCodes(String longStr){

            // 正则表达式解释：
            // CI\{     - 匹配文本"CI{"
            // (.*?)    - 匹配并捕获尽可能少的任意字符（非贪婪模式），直到遇到下一个"}"
            // \}       - 匹配文本"}"
            String regex = "CI\\{(.*?)\\}";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(longStr);

            List<String> jsonStrings = new ArrayList<>();

            while (matcher.find()) {
                String jsonString = matcher.group(0);
                jsonStrings.add(jsonString);
            }

            if (jsonStrings.isEmpty()) return null;
            return jsonStrings.toArray(new String[0]);
        }
        public String toCiCode(){
            return "CI"+gson.toJson(this);
        }
    }
}
