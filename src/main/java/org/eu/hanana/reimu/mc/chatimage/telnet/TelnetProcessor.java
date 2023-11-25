package org.eu.hanana.reimu.mc.chatimage.telnet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.eu.hanana.reimu.mc.chatimage.ChatImage;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.eu.hanana.reimu.mc.chatimage.TelnetServer;
import org.eu.hanana.reimu.mc.chatimage.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

public class TelnetProcessor implements Runnable{
    private final TelnetData telnetData;
    private final TelnetServer.ClientHandler clientHandler;

    public TelnetProcessor(TelnetData telnetData, TelnetServer.ClientHandler clientHandler){
        this.telnetData=telnetData;
        this.clientHandler=clientHandler;
    }
    @Override
    public void run() {
        if (telnetData.operation.equals("connect")){
            clientHandler.send("{\"operation\":\"connect\",\"payload\":{\"version\":\""+ ChatImageMod.VERSION +"\"}}");
        }
        if (telnetData.operation.equals("send")){
            String content = telnetData.payload.get("msg").getAsString();
            try {
                if (FMLCommonHandler.instance().getSide().isClient()) {
                    if (Utils.getIntegratedServer() != null)
                        sendText(Utils.getIntegratedServer(), content);
                    else
                        Minecraft.getMinecraft().player.sendChatMessage(content);
                } else
                    sendText(FMLCommonHandler.instance().getMinecraftServerInstance(), content);
                clientHandler.send("{\"operation\":\"send\",\"payload\":{\"state\":0}}");
            }catch (Exception e) {
                e.printStackTrace();
                clientHandler.send("{\"operation\":\"send\",\"payload\":{\"state\":-1,\"msg\":\""+e+"\"}}");
            }
        }
        if (telnetData.operation.equals("upload")){
            String base64String = telnetData.payload.get("data").getAsString();
            try {
                // 解码Base64字符串
                byte[] imageBytes = Base64.getDecoder().decode(base64String);

                // 创建 ByteArrayInputStream 对象
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);

                // 使用 ImageIO 读取 BufferedImage
                BufferedImage bufferedImage = ImageIO.read(bis);

                // 关闭 ByteArrayInputStream
                bis.close();

                // 将 BufferedImage 转换为字节数组
                byte[] byteArray = Utils.imageToByteArray(bufferedImage);
                ChatImage.ChatImageData cid = new ChatImage.ChatImageData();
                cid.information="";
                cid.h=100;
                cid.w=100;
                if (FMLCommonHandler.instance().getSide().isClient()){
                    if (Utils.getIntegratedServer()!=null) {
                        //客户端内置服务器
                        int fn = 0;
                        if (!new File("chatimages/").exists())
                            new File("chatimages/").mkdir();
                        while (new File("chatimages/"+fn).exists())
                            fn++;
                        Utils.WriteFile("chatimages/"+fn,byteArray);

                        cid.url="ci:lo/"+fn;
                        sendText(Utils.getIntegratedServer(), cid.getChatMsg());
                    } else {
                        //客户端
                        try {
                            cid.url=Utils.SendLByte(byteArray);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        FMLClientHandler.instance().getClientPlayerEntity().sendChatMessage(cid.toString());
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

                clientHandler.send("{\"operation\":\"upload\",\"payload\":{\"state\":0}}");
            }catch (Exception e) {
                e.printStackTrace();
                clientHandler.send("{\"operation\":\"upload\",\"payload\":{\"state\":-1,\"msg\":\""+e+"\"}}");
            }
        }
    }
    public static void sendText(MinecraftServer minecraftServer, String text){
        minecraftServer.sendMessage(new TextComponentString(text));
        for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
            player.sendMessage(new TextComponentString(text));
        }
    }
    public void sendText(MinecraftServer minecraftServer, ITextComponent text){
        minecraftServer.sendMessage(text);
        for (EntityPlayerMP player : minecraftServer.getPlayerList().getPlayers()) {
            player.sendMessage(text);
        }
    }
}
