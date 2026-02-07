package org.eu.hanana.reimu.chatimage.core;


import org.eu.hanana.reimu.mc.hnnlib.url.HananaUrlFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.function.Function;

public class ChatimageURLStreamHandlerFactory extends HananaUrlFactory {
        @Override
    public URLStreamHandler createURLStreamHandler() {
        return new ChatimageURLStreamHandler();
    }

    public String protocol() {
        return "ci";
    }

    public static class ChatimageURLStreamHandler extends URLStreamHandler {//implements ModularURLHandler.IURLProvider  {
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return new ChatimageURLConnection(url);
        }

    }
}

