package org.eu.hanana.reimu.chatimage;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import org.eu.hanana.reimu.chatimage.networking.HandlerDownloadCl;
import org.eu.hanana.reimu.chatimage.networking.HandlerUploadCl;
import org.eu.hanana.reimu.chatimage.networking.PayloadDownload;
import org.eu.hanana.reimu.chatimage.networking.PayloadUpload;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

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

}
