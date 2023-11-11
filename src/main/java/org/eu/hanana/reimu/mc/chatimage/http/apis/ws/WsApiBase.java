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
    public static void sendStrMsg(Session session,String string) throws IOException {
        session.getRemote().sendString("{\n" +
                "    \"payload\": [\n" +
                "        {\n" +
                "            \"atType\": 0,\n" +
                "            \"avatarMeta\": \"\",\n" +
                "            \"channelId\": \"\",\n" +
                "            \"channelName\": \"ChatImgMod\",\n" +
                "            \"chatType\": 1,\n" +
                "            \"clientSeq\": \"8765\",\n" +
                "            \"cntSeq\": \"0\",\n" +
                "            \"commentCnt\": \"0\",\n" +
                "            \"directMsgFlag\": 0,\n" +
                "            \"directMsgMembers\": [],\n" +
                "            \"editable\": false,\n" +
                "            \"elements\": [\n" +
                "                {\n" +
                "                    \"elementId\": \"7300116751999756958\",\n" +
                "                    \"elementType\": 1,\n" +
                "                    \"extBufForUI\": \"0x\",\n" +
                "                    \"textElement\": {\n" +
                "                        \"atChannelId\": \"0\",\n" +
                "                        \"atNtUid\": \"\",\n" +
                "                        \"atTinyId\": \"0\",\n" +
                "                        \"atType\": 0,\n" +
                "                        \"atUid\": \"0\",\n" +
                "                        \"content\": \""+string+"\",\n" +
                "                        \"subElementType\": 0\n" +
                "                    }\n" +
                "                }\n" +
                "            ],\n" +
                "            \"emojiLikesList\": [],\n" +
                "            \"fromAppid\": \"0\",\n" +
                "            \"fromChannelRoleInfo\": {\n" +
                "                \"color\": 0,\n" +
                "                \"name\": \"\",\n" +
                "                \"roleId\": \"0\"\n" +
                "            },\n" +
                "            \"fromGuildRoleInfo\": {\n" +
                "                \"color\": 0,\n" +
                "                \"name\": \"\",\n" +
                "                \"roleId\": \"0\"\n" +
                "            },\n" +
                "            \"fromUid\": \"0\",\n" +
                "            \"generalFlags\": \"0x\",\n" +
                "            \"guildCode\": \"0\",\n" +
                "            \"guildId\": \"\",\n" +
                "            \"guildName\": \"\",\n" +
                "            \"isImportMsg\": false,\n" +
                "            \"isOnlineMsg\": true,\n" +
                "            \"levelRoleInfo\": {\n" +
                "                \"color\": 0,\n" +
                "                \"name\": \"\",\n" +
                "                \"roleId\": \"0\"\n" +
                "            },\n" +
                "            \"msgId\": \"00000000000\",\n" +
                "            \"msgMeta\": \"0x\",\n" +
                "            \"msgRandom\": \"1364696059\",\n" +
                "            \"msgSeq\": \"140\",\n" +
                "            \"msgTime\": \"1699690882\",\n" +
                "            \"msgType\": 2,\n" +
                "            \"peerName\": \"\",\n" +
                "            \"peerUid\": \"u_VaRhrBHZSiEF0sCkoXFA9w\",\n" +
                "            \"peerUin\": \"00000000\",\n" +
                "            \"recallTime\": \"0\",\n" +
                "            \"records\": [],\n" +
                "            \"roleId\": \"0\",\n" +
                "            \"roleType\": 0,\n" +
                "            \"sendMemberName\": \"\",\n" +
                "            \"sendNickName\": \"\",\n" +
                "            \"sendStatus\": 2,\n" +
                "            \"sendType\": 0,\n" +
                "            \"senderUid\": \"u_VaRhrBHZSiEF0sCkoXFA9w\",\n" +
                "            \"senderUin\": \"00000000\",\n" +
                "            \"subMsgType\": 1,\n" +
                "            \"timeStamp\": \"0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"type\": \"message::recv\"\n" +
                "}");
    }
}
