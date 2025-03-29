package org.eu.hanana.reimu.chatimage;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.BundleSelectedItemSpecialRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.*;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.chatimage.networking.HandlerDownloadCl;
import org.eu.hanana.reimu.chatimage.networking.HandlerUploadCl;
import org.eu.hanana.reimu.chatimage.networking.PayloadDownload;
import org.eu.hanana.reimu.chatimage.networking.PayloadUpload;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.eu.hanana.reimu.chatimage.EventHandler.escapeSpecialRegexChars;
import static org.eu.hanana.reimu.chatimage.EventHandler.splitWithDelimiter;

public class Util {
    public static String reply;
    public static byte[][] DOWNLOAD_BUFFER=null;
    public static String upload(String path) throws IOException, InterruptedException {
        byte[] bytes = readFileToByteArray(new File(path));
        if (bytes.length==0){
            throw new IOException("Canceled/已取消");
        }
        var split = splitByteArray(bytes,3000);
        PacketDistributor.sendToServer(new PayloadUpload("START",split.size(),new byte[0]));
        waitReply("R-START");
        for (int i = 0; i < split.size(); i++) {
            PacketDistributor.sendToServer(new PayloadUpload("UPLOAD",i,split.get(i)));
        }
        waitReply("OK");
        return "ci:lo/"+ new String(HandlerUploadCl.payloadUpload.bytes());
    }
    public static byte[] download(String path) throws IOException, InterruptedException {
        while (DOWNLOAD_BUFFER!=null){
            Thread.sleep(100);
        }
        byte[][] bytes1 = null;
        try {
            PacketDistributor.sendToServer(new PayloadDownload("START",0,path.getBytes(StandardCharsets.UTF_8)));
            waitReply("R-START");
            DOWNLOAD_BUFFER=new byte[HandlerDownloadCl.payloadUpload.pos()][];
            int time=0;
            PacketDistributor.sendToServer(new PayloadDownload("DL",0,new byte[0]));
            while (true){
                Thread.sleep(10);
                time+=10;
                if (time>5000)
                    throw new RuntimeException("TIMEOUT");
                var ok = true;
                for (byte[] bytes : DOWNLOAD_BUFFER) {
                    if (bytes==null)
                        ok=false;
                }
                if (ok) break;
            }
            bytes1=DOWNLOAD_BUFFER;
        }finally {
            DOWNLOAD_BUFFER=null;
        }

        return mergeByteArrays(bytes1);
    }
    /**
     * 合并二维字节数组为一维字节数组
     *
     * @param byteArrays 要合并的二维字节数组
     * @return 合并后的字节数组
     */
    public static byte[] mergeByteArrays(byte[][] byteArrays) {
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

        return mergedArray;
    }
    /**
     * 递归删除目录及其内容
     *
     * @param directory 要删除的目录
     * @return 如果目录及其内容删除成功，则返回 true；否则返回 false
     */
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子目录
                        deleteDirectory(file);
                    } else {
                        // 删除文件
                        file.delete();
                    }
                }
            }
        }

        // 删除空目录
        return directory.delete();
    }
    public static void waitReply(String opt) throws InterruptedException {
        int time=0;
        while (!opt.equals(reply)){
            Thread.sleep(10);
            time+=10;
            if (time>5000)
                throw new RuntimeException("TIMEOUT");
        }
    }
    public static List<byte[]> splitByteArray(byte[] data, int chunkSize) {
        List<byte[]> chunks = new ArrayList<>();
        for (int i = 0; i < data.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, data.length);
            byte[] chunk = new byte[end - i];
            System.arraycopy(data, i, chunk, 0, chunk.length);
            chunks.add(chunk);
        }
        return chunks;
    }
    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, length);
            }
            return bos.toByteArray();
        } finally {
            if (fis != null) {
                fis.close();
            }
            bos.close();
        }
    }
    public static void signBook(ServerGamePacketListenerImpl serverGamePacketListener, FilteredText title, List<FilteredText> pages, int index, CallbackInfo ci, ItemStack itemstack1) {
        var raw_data=itemstack1.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (raw_data == null) {
            return;
        }
        var data = new ArrayList<>(raw_data.pages());
        var data1 = new ArrayList<Filterable<Component>>();
        for (Filterable<Component> datum : data) {
            var vEvent = new ServerChatEvent(serverGamePacketListener.player,datum.get(true).getString(),datum.get(true));
            genCIMsg(vEvent);
            data1.add(new Filterable<>(vEvent.getMessage(), Optional.empty()));
        }
        itemstack1.set(
                DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(raw_data.title(), raw_data.author(), raw_data.generation(), data1, raw_data.resolved())
        );
    }
    public static BufferedImage convertToBufferedImage(NativeImage nativeImage) {
        // 1. 验证格式必须是 RGBA
        if (nativeImage.format() != NativeImage.Format.RGBA) {
            throw new IllegalArgumentException("只支持 RGBA 格式的 NativeImage");
        }

        // 2. 获取图像尺寸
        int width = nativeImage.getWidth();
        int height = nativeImage.getHeight();

        // 3. 创建目标 BufferedImage
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 4. 批量获取像素数据（避免逐像素操作）
        IntBuffer pixelBuffer = MemoryUtil.memIntBuffer(nativeImage.getPointer(), width * height);
        int[] pixels = new int[width * height];
        pixelBuffer.get(pixels);

        // 5. 转换 ABGR 到 ARGB（利用游戏工具类）
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = ARGB.fromABGR(pixels[i]);
        }

        // 6. 一次性设置到 BufferedImage
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

        return bufferedImage;
    }
    public static void genCIMsg(ServerChatEvent event){
        Component message = event.getMessage();
        String[] ciCodes = ChatImage.ChatImageData.getCiCodes(message.getString());
        if (ciCodes!=null) {
            String input = message.getString();
            for (String ciCode : ciCodes) {
                input = input.replaceFirst(escapeSpecialRegexChars(ciCode),"*#*#");
            }
            List<String> strings = splitWithDelimiter(input,"\\*#\\*#");
            int cp=0;
            MutableComponent result = Component.empty();
            for (int i = 0; i < strings.size(); i++) {
                String s = strings.get(i);
                if (s.equals("*#*#")){
                    try {
                        ChatImage.getChatImage(ciCodes[cp]);
                    } catch (Throwable e) {
                        result.append(Component.translatable("msg.ci.photo").setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.RED)
                                        .withHoverEvent(new Actions.ShowImage(Component.literal(e.toString())))
                        ));
                        continue;
                    }
                    result.append(Component.translatable("msg.ci.photo").setStyle(
                            Style.EMPTY
                                    .withColor(ChatFormatting.GREEN)
                                    .withHoverEvent(new Actions.ShowImage(Component.literal(ciCodes[cp])))
                                    .withClickEvent(new Actions.ViewImage(ciCodes[cp]))
                    ));
                    cp++;
                }else {
                    result.append(s);
                }
            }
            event.setMessage(result);
        }
    }
}
