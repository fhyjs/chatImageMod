package org.eu.hanana.reimu.mc.chatimage.http;

import net.minecraft.network.Packet;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.eu.hanana.reimu.mc.chatimage.Utils;
import org.eu.hanana.reimu.mc.chatimage.http.red.RedData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WsHandler extends WebSocketAdapter {
    private final List<Object> wsApis = new ArrayList<>();
    @Override
    public void onWebSocketText(String message) {
        // 处理接收到的文本消息
        System.out.println("Received message: " + message);

        // 发送回复消息
        try {
            RedData data = new RedData(message);
            for (Object wsApi : wsApis) {
                if(data.accept(wsApi)){
                    Method[] methods =  wsApi.getClass().getMethods();
                    for (Method method : methods) {
                        for (Annotation annotation : method.getAnnotations()) {
                            if (annotation instanceof WsApiMethod){
                                method.invoke(wsApi,data,getSession());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketConnect(Session session) {
        // 客户端连接时的处理逻辑
        super.onWebSocketConnect(session);
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());

        List<Class<?>> classes = Utils.getClasses("org.eu.hanana.reimu.mc.chatimage.http.apis");
        for (Class<?> aClass : classes) {
            for (Annotation annotation : aClass.getAnnotations()) {
                if (annotation instanceof WsApi){
                    try {
                        wsApis.add(aClass.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        ChatImageMod.logger.error(e);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        // 连接关闭时的处理逻辑
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Connection closed: " + statusCode + ", " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        // 发生错误时的处理逻辑
        super.onWebSocketError(cause);
        cause.printStackTrace();
    }
}

