package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.init.Blocks;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.mc.chatimage.enums.Actions;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Mod(modid = ChatImageMod.MODID, name = ChatImageMod.NAME, version = ChatImageMod.VERSION)
public class ChatImageMod
{
    public static SimpleNetworkWrapper INSTANCE = null;
    public static final String MODID = "chatimage";
    public static final String NAME = "chatimage Mod";
    public static final String VERSION = "1.0";

    public static Logger logger;
    public ChatImageMod(){
        MinecraftForge.EVENT_BUS.register(new org.eu.hanana.reimu.mc.chatimage.EventHandler());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
    private void registerMessage(){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ChatImageMod.MODID);
        INSTANCE.registerMessage(JntmMessageHandler.class, JntmMessage.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(JntmMessageHandler.class, JntmMessage.class, 0, Side.SERVER);
        INSTANCE.registerMessage(UploadMessageHandler.class, UploadMessage.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(UploadMessageHandler.class, UploadMessage.class, 1, Side.SERVER);
        INSTANCE.registerMessage(UploadMMessageHandler.class, UploadMMessage.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(UploadMMessageHandler.class, UploadMMessage.class, 2, Side.SERVER);
        INSTANCE.registerMessage(Caclmd5MessageHandler.class, Caclmd5Message.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(Caclmd5MessageHandler.class, Caclmd5Message.class, 3, Side.SERVER);
        INSTANCE.registerMessage(DownloadMessageHandler.class, DownloadMessage.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(DownloadMessageHandler.class, DownloadMessage.class, 4, Side.SERVER);
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Object hoverEvents= ObfuscationReflectionHelper.getPrivateValue(HoverEvent.Action.class, null, "field_150690_d");
        if (hoverEvents instanceof Map) {
            ((Map<String, HoverEvent.Action>) hoverEvents).put(Actions.SHOW_IMAGE.getCanonicalName(), Actions.SHOW_IMAGE);
        }
        // 注册自定义协议处理程序
        URL.setURLStreamHandlerFactory(new ChatImage.ChatImageHandlerFactory());
        registerMessage();
        List<File> files = Utils.traverseFolder(new File("chatimages/"));
        for (File file : files) {
            file.delete();
        }
    }
}
