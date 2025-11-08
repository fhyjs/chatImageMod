package org.eu.hanana.reimu.chatimage;

import cpw.mods.cl.ModularURLHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.config.ChatImageConfig;
import org.eu.hanana.reimu.chatimage.core.ChatimageURLStreamHandlerFactory;
import org.eu.hanana.reimu.chatimage.core.LookupHelper;
import org.eu.hanana.reimu.chatimage.network.FileRequestHandle;
import org.eu.hanana.reimu.chatimage.network.FileRequestPayload;
import org.eu.hanana.reimu.chatimage.network.FileTransportHandle;
import org.eu.hanana.reimu.chatimage.network.FileTransportPayload;
import org.eu.hanana.reimu.chatimage.register.MenuRegister;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.eu.hanana.reimu.chatimage.ChatimageMod.MOD_ID;

@Mod(MOD_ID)
public class ChatimageMod {
    public static final String MOD_ID = "chatimage";
    public static final Logger logger = LogManager.getLogger();
    public static boolean GLOBAL_PROTOCOL=true;
    public ChatimageMod(IEventBus modBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(new EventHandler());
        MenuRegister.register(modBus);
        modBus.addListener(this::init);
        container.getEventBus().register(ChatImageConfig.class);
        container.registerConfig(ModConfig.Type.COMMON, ChatImageConfig.commonSpec);
        container.registerConfig(ModConfig.Type.CLIENT, ChatImageConfig.clientSpec);

        modBus.addListener(this::registerPayloads);

//        if (ModList.get().isLoaded("legacy_command_registry")){
//            EVENT.register(new LegacyCommandRegistrationEvent() {
//                @Override
//                public void register(CommandManager commandManager) {
//                    commandManager.register(new ChatImageCommand());
//                }
//            });
//        }

    }
    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.NETWORK);
        var fth = new FileTransportHandle();
        registrar.playBidirectional(FileTransportPayload.TYPE,FileTransportPayload.STREAM_CODEC,fth,fth);
        registrar.playToServer(FileRequestPayload.TYPE,FileRequestPayload.STREAM_CODEC,new FileRequestHandle());

    }

    private void init(FMLCommonSetupEvent event){
        if (ChatImageConfig.remove_all) {
            try {
                Util.deleteDirectory(new File(".", "chatimage"));
            } catch (Exception e) {
                logger.error(e);
            }
        }
        GLOBAL_PROTOCOL=true;
        try {
            // 注册自定义的URL流处理器工厂
            URL.setURLStreamHandlerFactory(new ChatimageURLStreamHandlerFactory());
        }catch (Throwable e){
            try {
                // 创建高权限 lookup
                MethodHandles.Lookup lookup = LookupHelper.trustedLookup(ModularURLHandler.class);

                // 获取 VarHandle
                VarHandle vh = lookup.findVarHandle(ModularURLHandler.class, "handlers", Map.class);

                // 读取当前 Map
                Map<String, ModularURLHandler.IURLProvider> handlers =
                        (Map<String, ModularURLHandler.IURLProvider>) vh.get(ModularURLHandler.INSTANCE);

                // 创建新的副本并插入协议
                Map<String, ModularURLHandler.IURLProvider> eMap = new HashMap<>(handlers);
                eMap.put("ci", new ChatimageURLStreamHandlerFactory.ChatimageURLStreamHandler());

                // 设置回去
                vh.set(ModularURLHandler.INSTANCE, eMap);

                ChatimageMod.logger.warn("Add protocol with Lookup — safe and module-aware!");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.warn("Trying unsafe!");
            }
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                var unsafe = (Unsafe) field.get(null);
                long handlers = unsafe.objectFieldOffset(ModularURLHandler.class.getDeclaredField("handlers"));
                Map<String, ModularURLHandler.IURLProvider> object = (Map<String, ModularURLHandler.IURLProvider>) unsafe.getObject(ModularURLHandler.INSTANCE, handlers);
                Map<String, ModularURLHandler.IURLProvider> eMap = new HashMap<>(object);
                eMap.put("ci",new ChatimageURLStreamHandlerFactory.ChatimageURLStreamHandler());
                unsafe.getAndSetObject(ModularURLHandler.INSTANCE,handlers,eMap);
                logger.warn("Add protocol with Unsafe,this is unsafe!");
            } catch (Throwable ex) {
                GLOBAL_PROTOCOL=false;
                ex.printStackTrace();
                logger.error("Failed to add ci protocol!Using default!");
            }
        }
    }
}
