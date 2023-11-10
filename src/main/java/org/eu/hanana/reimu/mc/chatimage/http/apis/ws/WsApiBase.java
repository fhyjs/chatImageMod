package org.eu.hanana.reimu.mc.chatimage.http.apis.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eu.hanana.reimu.mc.chatimage.http.WsApiMethod;
import org.eu.hanana.reimu.mc.chatimage.http.red.RedData;

import java.io.IOException;

public abstract class WsApiBase {
    @WsApiMethod
    public void run0(RedData data, Session session) throws Exception {
        run(data,session);
    }
    abstract public void run(RedData data, Session session) throws Exception;
    public void sendStr(Session session,String string) throws IOException {
        session.getRemote().sendString(string);
    }
}
