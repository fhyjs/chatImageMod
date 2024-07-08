package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.util.text.event.ClickEvent;
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
import org.eu.hanana.reimu.mc.chatimage.http.HttpServer;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Mod(modid = ChatImageMod.MODID, name = ChatImageMod.NAME, version = ChatImageMod.VERSION,guiFactory = "org.eu.hanana.reimu.mc.chatimage.CIMGuiFactory")
public class ChatImageMod
{
    public static SimpleNetworkWrapper NETWORK = null;
    public static final String MODID = "chatimage";
    public static final String NAME = "chatimage Mod";
    public static final String VERSION = "1.2";
    public static ChatImageMod INSTANCE;
    public static Logger logger;
    public HttpServer httpServer;
    public org.eu.hanana.reimu.mc.chatimage.EventHandler eventHandler;
    public ChatImageMod(){
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(eventHandler=new org.eu.hanana.reimu.mc.chatimage.EventHandler());
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        ConfigCore.loadConfig(event);
        System.out.println(System.getProperty("java.class.path"));
        if (ConfigCore.isenabledTelnet) {

            new Thread(new TelnetServer()).start();

            if (Utils.isClassExists("org.eclipse.jetty.server.Server") && Utils.isClassExists("javax.servlet.Servlet")) {
                httpServer = HttpServer.newHttpServer(25566);
                httpServer.start();
            } else {
                logger.warn("No Jetty Plugin!");
            }
        }
    }
    private void registerMessage(){
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(ChatImageMod.MODID);
        NETWORK.registerMessage(JntmMessageHandler.class, JntmMessage.class, 0, Side.CLIENT);
        NETWORK.registerMessage(JntmMessageHandler.class, JntmMessage.class, 0, Side.SERVER);
        NETWORK.registerMessage(UploadMessageHandler.class, UploadMessage.class, 1, Side.CLIENT);
        NETWORK.registerMessage(UploadMessageHandler.class, UploadMessage.class, 1, Side.SERVER);
        NETWORK.registerMessage(UploadMMessageHandler.class, UploadMMessage.class, 2, Side.CLIENT);
        NETWORK.registerMessage(UploadMMessageHandler.class, UploadMMessage.class, 2, Side.SERVER);
        NETWORK.registerMessage(Caclmd5MessageHandler.class, Caclmd5Message.class, 3, Side.CLIENT);
        NETWORK.registerMessage(Caclmd5MessageHandler.class, Caclmd5Message.class, 3, Side.SERVER);
        NETWORK.registerMessage(DownloadMessageHandler.class, DownloadMessage.class, 4, Side.CLIENT);
        NETWORK.registerMessage(DownloadMessageHandler.class, DownloadMessage.class, 4, Side.SERVER);
    }
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Object hoverEvents= ObfuscationReflectionHelper.getPrivateValue(HoverEvent.Action.class, null, "field_150690_d");
        if (hoverEvents instanceof Map) {
            ((Map<String, HoverEvent.Action>) hoverEvents).put(Actions.SHOW_IMAGE.getCanonicalName(), Actions.SHOW_IMAGE);
        }
        Object clickEvents= ObfuscationReflectionHelper.getPrivateValue(ClickEvent.Action.class, null, "field_150679_e");
        if (hoverEvents instanceof Map) {
            ((Map<String, ClickEvent.Action>) clickEvents).put(Actions.VIEW_IMAGE.getCanonicalName(), Actions.VIEW_IMAGE);
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
