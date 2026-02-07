package org.eu.hanana.reimu.chatimage.core;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.network.FileRequestPayload;
import org.eu.hanana.reimu.chatimage.network.transporter.FtpManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChatimageURLConnection extends URLConnection {
    protected ChatimageURLConnection(URL url) {
        super(url);
    }
    protected byte[] data=null;
    @Override
    public void connect() throws IOException {
        String[] split = getURL().getPath().split("/");
        if (split[0].equals("lo")) {
            String key = Util.randomString(20);
            CompletableFuture<byte[]> future = new CompletableFuture<>();

            // 注册回调
            FtpManager.ftpDownloadPendingCallback.put(key, tuple -> {
                try (var input = tuple.getB()) {
                    future.complete(input.readAllBytes());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
            ClientPacketDistributor.sendToServer(new FileRequestPayload(split[1], key));
            // ✅ 阻塞等待数据返回，最长等待 10 秒
            try {
                data = future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new IOException("Timeout waiting for FTP data", e);
            }
            //data = Util.download(split[1]);
        }else if (split[0].equals("cp")) {
            data = this.getClass().getClassLoader().getResourceAsStream(getURL().getPath().substring(3)).readAllBytes();
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // 返回一个输入流，这里可以是从自定义协议读取的数据
        return new ByteArrayInputStream(data);
    }
}
