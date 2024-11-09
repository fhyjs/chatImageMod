package org.eu.hanana.reimu.chatimage.core;

import org.eu.hanana.reimu.chatimage.Util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ChatimageURLConnection extends URLConnection {
    protected ChatimageURLConnection(URL url) {
        super(url);
    }
    protected byte[] data=null;
    @Override
    public void connect() throws IOException {
        try {
            String[] split = getURL().getPath().split("/");
            if (split[0].equals("lo")) {
                data = Util.download(split[1]);
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        // 返回一个输入流，这里可以是从自定义协议读取的数据
        return new ByteArrayInputStream(data);
    }
}
