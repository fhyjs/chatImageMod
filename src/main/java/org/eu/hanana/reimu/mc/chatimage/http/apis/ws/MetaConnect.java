package org.eu.hanana.reimu.mc.chatimage.http.apis.ws;

import org.eclipse.jetty.websocket.api.Session;
import org.eu.hanana.reimu.mc.chatimage.http.WsApi;
import org.eu.hanana.reimu.mc.chatimage.http.red.RedData;

@WsApi(namespace = "meta" ,name = "connect")
public class MetaConnect extends WsApiBase {

    @Override
    public void run(RedData data, Session session) throws Exception {
        int raccont=1432582213;
        sendStr(session,"{\n" +
                "    \"payload\": {\n" +
                "        \"authData\": {\n" +
                "            \"a2\": \"\",\n" +
                "            \"account\": \""+raccont+"\",\n" +
                "            \"age\": 0,\n" +
                "            \"d2\": \"\",\n" +
                "            \"d2key\": \"\",\n" +
                "            \"faceUrl\": \"\",\n" +
                "            \"gender\": 0,\n" +
                "            \"mainAccount\": \"\",\n" +
                "            \"nickName\": \"\",\n" +
                "            \"uid\": \"u__htjqoR98ZR250Vk47nH3g\",\n" +
                "            \"uin\": \""+raccont+"\"\n" +
                "        },\n" +
                "        \"name\": \"chronocat\",\n" +
                "        \"version\": \"0.0.39\"\n" +
                "    },\n" +
                "    \"type\": \"meta::connect\"\n" +
                "}");
    }
}
