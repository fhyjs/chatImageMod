package org.eu.hanana.reimu.mc.chatimage.http.apis.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.eu.hanana.reimu.mc.chatimage.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class HttpApiSend extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // 设置响应内容类型
        response.setContentType("application/json");

        // 获取输出流
        PrintWriter out = response.getWriter();
        StringBuilder rawData= new StringBuilder();
        String tmp;
        while((tmp=request.getReader().readLine())!=null){
            rawData.append(tmp).append("\n");
        }

        Spliterator<JsonElement> jsonElements = new JsonParser().parse(rawData.toString()).getAsJsonObject().get("elements").getAsJsonArray().spliterator();
        try {
            jsonElements.forEachRemaining(jsonElement -> {
                JsonObject data = jsonElement.getAsJsonObject();
                int type = data.get("elementType").getAsInt();
                JsonObject textElement = data.get("textElement").getAsJsonObject();
                int atType = textElement.get("atType").getAsInt();
                String content = textElement.get("content").getAsString();
                if (type == 1) {
                    if (FMLCommonHandler.instance().getSide().isClient()){
                        if (Utils.getIntegratedServer() !=null)
                            sendText(Utils.getIntegratedServer(),content);
                        else
                            Minecraft.getMinecraft().player.sendChatMessage(content);
                    }else
                        sendText(FMLCommonHandler.instance().getMinecraftServerInstance(), content);
                }
            });
        }catch (Exception e){
            // 生成动态内容
            out.println("{\n" +
                    "    \"errMsg\": \""+e+"\",\n" +
                    "    \"result\": -1\n" +
                    "}");
            ChatImageMod.logger.error(e);
            return;
        }
        // 生成动态内容
        out.println("{\n" +
                "    \"errMsg\": \"\",\n" +
                "    \"result\": 0\n" +
                "}");
    }
    public static void sendText(MinecraftServer minecraftServer,String text){
        minecraftServer.sendMessage(new TextComponentString(text));
        for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
            player.sendMessage(new TextComponentString(text));
        }
    }
}
