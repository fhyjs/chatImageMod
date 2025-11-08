package org.eu.hanana.reimu.chatimage.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ChatImage {
    public static final Map<String,ChatImage> BufferedChatImage = new HashMap<>();
    public static final Map<String,ResourceLocation> BufferedTexture = new HashMap<>();
    public URL url;
    public int w,h,rawW,rawH;
    public String info;
    public boolean currentRaw;
    public ImageStatus status;
    private static Class<ClientReflect> clientRClass;
    static {
        if(FMLEnvironment.dist.isClient()) {
            try {
                //noinspection unchecked
                clientRClass = (Class<ClientReflect>) Class.forName("org.eu.hanana.reimu.chatimage.core.ClientReflect");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected ChatImage(){
        status=ImageStatus.NEW;
    }
    protected ChatImage(String url,int w,int h,String info) throws MalformedURLException {
        this();
        if (!ChatimageMod.GLOBAL_PROTOCOL) {
            if (url.startsWith("ci:")) {
                this.url = URL.of(URI.create(url), new ChatimageURLStreamHandlerFactory.ChatimageURLStreamHandler());
                ChatimageMod.logger.warn("The ci protocol was not added to system.May because by an error.Failed to a stable mode.");
            }else {
                this.url=URI.create(url).toURL();
            }
        }else {
            this.url=URI.create(url).toURL();
        }
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
        if(FMLEnvironment.dist.isClient()) {
            try {
                clientRClass.getMethod("clearCache").invoke(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        BufferedTexture.clear();
        BufferedChatImage.clear();
        ChatimageMod.logger.info("cached cleared");
    }
    public String getTextureId(){
        return Integer.toHexString(url.toString().hashCode())+"@"+w+","+h;
    }
    //@OnlyIn(Dist.CLIENT)
    public boolean viewRaw(boolean isRaw){
        if (status!=ImageStatus.OK) return false;
        if (isRaw==currentRaw){
            return false;
        }
        if (getTexture()!=null){
            var texId=getTexture();
            try {
                clientRClass.getMethod("closeTexture", ResourceLocation.class,String.class).invoke(null,texId,getTextureId());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        // 交换 w 和 rawW
        w = w ^ rawW;
        rawW = w ^ rawW;
        w = w ^ rawW;
        // 交换 h 和 rawH
        h = h ^ rawH;
        rawH = h ^ rawH;
        h = h ^ rawH;

        currentRaw=isRaw;
        this.status=ImageStatus.NEW;
        return true;
    }
    @Nullable
    //@OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexture() {
        if (status==ImageStatus.OK&&BufferedTexture.containsKey(getTextureId())){
            return BufferedTexture.get(getTextureId());
        }else if (status==ImageStatus.NEW){
            new Thread(this::downloadImg).start();
        }
        return null;
    }
    //@OnlyIn(Dist.CLIENT)
    public void downloadImg(){
        status=ImageStatus.WAIT;
        try {
            URLConnection urlConnection = url.openConnection();
            // 读取输入流

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
                outputStream.write(dataBuffer, 0, bytesRead);
            }

            byte[] abyte = outputStream.toByteArray();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(abyte));
            if (!currentRaw) {
                rawW = image.getWidth();
                rawH = image.getHeight();
            }
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
            AtomicBoolean finish = new AtomicBoolean(false);
            AtomicReference<Throwable> error = new AtomicReference<>(null);
            //noinspection unchecked
            ((BlockableEventLoop<Runnable>) clientRClass.getMethod("getClientBlockableEventLoop").invoke(null)).schedule(()->{
                try {
                    clientRClass.getMethod("registerDynamicTexture", ResourceLocation.class, NativeImage.class).invoke(null,resourcelocation,read);
                    //Minecraft.getInstance().getTextureManager().register(resourcelocation, new DynamicTexture(null, read));
                    BufferedTexture.put(getTextureId(), resourcelocation);
                    status=ImageStatus.OK;
                    finish.set(true);
                }catch (Throwable throwable){
                    error.set(throwable);
                    finish.set(true);
                }
            });
            while (!finish.get()){
                Thread.sleep(100);
            }
            if (error.get()!=null){
                throw error.get();
            }

        } catch (Throwable e) {
            e.printStackTrace();
            ChatimageMod.logger.error(e);
            status=ImageStatus.ERROR;
            if (FMLEnvironment.dist.isClient()){
                try {
                    clientRClass.getMethod("sendToast", Component.class, Component.class).invoke(null,Component.literal("ERROR/错误"),Component.literal(e.toString()));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
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
        @NotNull
        public static String[] getCiCodes(String longStr) {
            List<String> results = new ArrayList<>();
            int index = 0;

            while (index < longStr.length()) {
                int start = longStr.indexOf("CI{", index);
                if (start == -1) break;

                int braceLevel = 0;
                int end = -1;
                for (int i = start + 3; i < longStr.length(); i++) {
                    char c = longStr.charAt(i);
                    if (c == '{') braceLevel++;
                    else if (c == '}') {
                        if (braceLevel == 0) { // 找到匹配的最外层 '}'
                            end = i;
                            break;
                        } else {
                            braceLevel--;
                        }
                    }
                }

                if (end != -1) {
                    results.add(longStr.substring(start, end + 1));
                    index = end + 1;
                } else {
                    // 没有匹配的闭合括号，退出
                    break;
                }
            }

            return results.isEmpty() ? new String[0] : results.toArray(new String[0]);
        }
        public String toCiCode(){
            return "CI"+gson.toJson(this);
        }

        public void setImageSizeRaw() throws IOException {
            try (InputStream inputStream = URI.create(url).toURL().openConnection().getInputStream()) {
                BufferedImage read = ImageIO.read(inputStream);
                this.w=read.getWidth();
                this.h=read.getHeight();
            }
        }
    }
}
