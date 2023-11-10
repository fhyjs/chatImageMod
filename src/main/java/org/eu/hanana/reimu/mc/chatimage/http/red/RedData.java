package org.eu.hanana.reimu.mc.chatimage.http.red;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eu.hanana.reimu.mc.chatimage.http.WsApi;

public class RedData {
    private final String type;
    public final JsonObject payload;
    private final Gson gson;
    private final JsonParser jsonParser;
    public RedData(String json){
        this.jsonParser=new JsonParser();
        this.gson=new Gson();
        this.type=jsonParser.parse(json).getAsJsonObject().get("type").getAsString();
        this.payload=jsonParser.parse(json).getAsJsonObject().get("payload").getAsJsonObject();
    }
    public String getNameSpace(){
        return type.split("::")[0];
    }
    public String getName(){
        return type.split("::")[1];
    }
    public boolean accept(Object obj){
        return obj.getClass().getAnnotation(WsApi.class).namespace().equals(getNameSpace())&&obj.getClass().getAnnotation(WsApi.class).name().equals(getName());
    }
}
