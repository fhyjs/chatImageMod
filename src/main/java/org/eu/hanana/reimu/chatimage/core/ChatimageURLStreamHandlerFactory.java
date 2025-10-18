package org.eu.hanana.reimu.chatimage.core;

import cpw.mods.cl.ModularURLHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.function.Function;

public class ChatimageURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("ci".equals(protocol)) {
            return new ChatimageURLStreamHandler();
        }
        return null;
    }
    public static class ChatimageURLStreamHandler extends URLStreamHandler implements ModularURLHandler.IURLProvider  {
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return new ChatimageURLConnection(url);
        }

        @Override
        public String protocol() {
            return "ci";
        }

        @Override
        public Function<URL, InputStream> inputStreamFunction() {
            return new Function<URL, InputStream>() {
                @Override
                public InputStream apply(URL url) {
                    ChatimageURLConnection chatimageURLConnection = new ChatimageURLConnection(url);
                    try {
                        chatimageURLConnection.connect();
                        return chatimageURLConnection.getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}

