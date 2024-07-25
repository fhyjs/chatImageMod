package org.eu.hanana.reimu.chatimage;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import org.eu.hanana.reimu.chatimage.networking.PayloadUpload;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Util {
    public static String reply;
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
        return String.valueOf(bytes.length);
    }
    public static void waitReply(String opt) throws InterruptedException {
        int time=0;
        while (opt.equals(reply)){
            Thread.sleep(100);
            time+=100;
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
