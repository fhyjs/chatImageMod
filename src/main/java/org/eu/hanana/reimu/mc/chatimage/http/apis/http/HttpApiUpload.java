package org.eu.hanana.reimu.mc.chatimage.http.apis.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.eclipse.jetty.http.MultiPartFormInputStream;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import org.eu.hanana.reimu.mc.chatimage.ChatImage;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.eu.hanana.reimu.mc.chatimage.Utils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

public class HttpApiUpload extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // 设置响应内容类型
        response.setContentType("application/json");

        // 获取输出流
        PrintWriter out = response.getWriter();
        // 用于存储字节数据的 ByteArrayOutputStream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MultiPartInputStreamParser parser = new MultiPartInputStreamParser(request.getInputStream(),
                request.getContentType(),
                null,
                null);
        List<Part> data=new ArrayList<>(parser.getParts());
        BufferedImage img;
        try {
            img = ImageIO.read(new ByteArrayInputStream(((MultiPartInputStreamParser.MultiPart) data.get(0)).getBytes()));
        }catch (Exception e){
            ChatImageMod.logger.error(e);
            return;
        }
        // 将 BufferedImage 转换为字节数组
        byte[] byteArray = Utils.imageToByteArray(img);
        ChatImage.ChatImageData cid = new ChatImage.ChatImageData();
        cid.information="";
        cid.h=100;
        cid.w=100;
        if (FMLCommonHandler.instance().getSide().isClient()){
            if (Minecraft.getMinecraft().getIntegratedServer()!=null) {
                //客户端内置服务器
                int fn = 0;
                if (!new File("chatimages/").exists())
                    new File("chatimages/").mkdir();
                while (new File("chatimages/"+fn).exists())
                    fn++;
                Utils.WriteFile("chatimages/"+fn,byteArray);

                cid.url="ci:lo/"+fn;
                sendText(Minecraft.getMinecraft().getIntegratedServer(), cid.getChatMsg());
            } else {
                //客户端
                try {
                    cid.url=Utils.SendLByte(byteArray);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Minecraft.getMinecraft().player.sendChatMessage(cid.toString());
            }
        }else {
            //服务器
            int fn = 0;
            if (!new File("chatimages/").exists())
                new File("chatimages/").mkdir();
            while (new File("chatimages/"+fn).exists())
                fn++;
            Utils.WriteFile("chatimages/"+fn,byteArray);

            cid.url="ci:lo/"+fn;
            sendText(FMLCommonHandler.instance().getMinecraftServerInstance(), cid.getChatMsg());
        }
    }
    public void sendText(MinecraftServer minecraftServer, ITextComponent text){
        minecraftServer.sendMessage(text);
        for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
            player.sendMessage(text);
        }
    }
}
