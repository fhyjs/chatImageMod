package org.eu.hanana.reimu.chatimage.network.transporter;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.config.ChatImageConfig;
import org.eu.hanana.reimu.chatimage.network.FileTransportPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class FtpInputStream extends InputStream implements Runnable{
    public static final String TR      = "tr_";
    public static final String RT      = "rt_";
    public static final String TR_START= TR +"start";
    public static final String RT_START= RT +"start";
    public static final String TR_DATA = TR +"data";
    public static final String TR_END = TR +"end";
    public static final String RT_NEXT = RT +"next";
    public static final String RT_CALLBACK = RT +"callbackReceiver";
    private static final Logger log = LogManager.getLogger(FtpInputStream.class);
    private final AtomicLong totalBytesReceived = new AtomicLong(0);
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
    private final byte[] data;
    private final PendingData pendingData;
    private boolean closed;
    private byte[] currentChunk = null;
    private int pos = 0;
    //T1: filename Remote; T2: data stream
    public Consumer<Tuple<String,FtpInputStream>> callbackReceiver = ftpInputStream -> {
        // 自动关闭流
        try (this) {
            Files.copy(ftpInputStream.getB(), Path.of("chatimage",ftpInputStream.getA()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };
    public Consumer<Tuple<String,FtpInputStream>> callbackTransfer = ftpInputStream -> {};
    @Nullable
    public ServerPlayer player;
    public FtpInputStream(byte[] data, @Nullable ServerPlayer player,String key){
        this.data=data;
        this.player=player;
        closed=false;
        pendingData = new PendingData();
        pendingData.key = key;
        pendingData.ftpInputStream=this;
        pendingData.startTime=System.nanoTime();
        pendingData.send=true;
        FtpManager.ftpUploadPendingData.put(pendingData.key, pendingData);
    }
    public FtpInputStream(byte[] data, @Nullable ServerPlayer player){
        this(data,player,Util.randomString(50));
    }

    public FtpInputStream(PendingData data1) {
        data=null;
        pendingData=data1;
        player=null;
    }
    @Override
    public void run() {
        if (data.length==0) return;
        sendPacket(new FileTransportPayload(TR_START,pendingData.key,0,new byte[0],""));
    }
    public void receivedPacket(@NotNull FileTransportPayload payload, @NotNull IPayloadContext context) throws InterruptedException, IOException {
        var op = payload.op();
        ChatimageMod.logger.debug("Received Network FTP, {}",op);
        if (op.equals(TR_START)){
            if (FtpManager.ftpDownloadPendingCallback.containsKey(pendingData.key)){
                callbackReceiver=FtpManager.ftpDownloadPendingCallback.get(pendingData.key);
                FtpManager.ftpDownloadPendingCallback.remove(pendingData.key);
            }
            sendPacket(new FileTransportPayload(RT_START,pendingData.key,0,new byte[0],""));
        } else if (op.equals(RT_START)) {
            feed(data);
            close();
            sendPacket(new FileTransportPayload(TR_DATA,pendingData.key,0,getNextData(30*1024),""));
        } else if (op.equals(TR_DATA)) {
            feed(payload.payload());

            if (totalBytesReceived.get() > ChatImageConfig.maxFileSize){
                sendPacket(new FileTransportPayload(RT_CALLBACK, pendingData.key, 0, new byte[0], "File Too Big!"));
            }else {
                sendPacket(new FileTransportPayload(RT_NEXT,pendingData.key,0,new byte[0],""));
            }
        } else if (op.equals(RT_NEXT)) {
            var data = getNextData(30*1024);
            if (data.length==0){
                sendPacket(new FileTransportPayload(TR_END, pendingData.key, 0, new byte[0], ""));
            }else {
                sendPacket(new FileTransportPayload(TR_DATA, pendingData.key, 0, data, ""));
            }
        } else if (op.equals(TR_END)) {
            close();

            var dir = new File("chatimage");
            dir.mkdirs();
            Path outputPath = Files.createTempFile(dir.toPath(),"upload-", ".tmp");
            callbackReceiver.accept(new Tuple<>(outputPath.getFileName().toString(),this));
            sendPacket(new FileTransportPayload(RT_CALLBACK, pendingData.key, 0, new byte[0], outputPath.getFileName().toString()));
            FtpManager.ftpDownloadPendingData.remove(pendingData.key);
            log.info("Receive completed!");
        } else if (op.equals(RT_CALLBACK)) {
            FtpManager.ftpUploadPendingData.remove(pendingData.key);
            callbackTransfer.accept(new Tuple<>(payload.extra(),this));
            log.info("Transfer completed! remote: {}",payload.extra());
        }
    }
    public byte[] getNextData(int length) throws IOException {
        byte[] buffer = new byte[length];
        int readLen = read(buffer);
        if (readLen == -1) {
            return new byte[0]; // EOF
        }
        if (readLen == buffer.length) {
            return buffer; // 满包直接返回
        }
        // 返回实际读取长度的子数组
        byte[] exact = new byte[readLen];
        System.arraycopy(buffer, 0, exact, 0, readLen);
        return exact;
    }

    private void sendPacket(CustomPacketPayload payload) {
        if (player==null) {
            try {
                Class.forName("net.neoforged.neoforge.client.network.ClientPacketDistributor").getMethod("sendToServer", CustomPacketPayload.class, CustomPacketPayload[].class).invoke(null,payload,new CustomPacketPayload[0]);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                     ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            PacketDistributor.sendToPlayer(player,payload);
        }
    }
    public boolean isToServer(){
        return player==null;
    }
    /**
     * 底层网络收到数据时调用
     */
    public void feed(byte[] data) {
        if (!closed && data != null && data.length > 0) {
            queue.offer(data);
            totalBytesReceived.addAndGet(data.length);
        }
    }

    public boolean isClosed() {
        return closed;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        while (true) {
            if (currentChunk == null || pos >= currentChunk.length) {
                try {
                    currentChunk = queue.poll(5, TimeUnit.SECONDS);
                    pos = 0;
                    if (currentChunk.length == 0) {
                        // 空数组表示关闭信号
                        return -1;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting for network data", e);
                }
            }

            int remaining = currentChunk.length - pos;
            int toRead = Math.min(len, remaining);
            System.arraycopy(currentChunk, pos, b, off, toRead);
            pos += toRead;
            return toRead;
        }
    }
    @Override
    public int available() {
        int available = (currentChunk != null ? (currentChunk.length - pos) : 0);
        for (byte[] chunk : queue) {
            available += chunk.length;
        }
        return available;
    }
    /**
     * 通知流已关闭（例如远端断开）
     */
    @Override
    public void close() {
        closed = true;
        queue.offer(new byte[0]); // 唤醒阻塞的read()
    }

    @Override
    public int read() throws IOException {
        byte[] buf = new byte[1];
        int n = read(buf, 0, 1);
        return (n == -1) ? -1 : (buf[0] & 0xFF);
    }
}
