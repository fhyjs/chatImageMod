package org.eu.hanana.reimu.chatimage;

import cpw.mods.cl.ModularURLHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.core.ChatimageURLStreamHandlerFactory;
import org.eu.hanana.reimu.chatimage.gui.MenuCiManager;
import org.eu.hanana.reimu.chatimage.gui.ScreenCiManager;
import org.eu.hanana.reimu.chatimage.gui.ScreenFileChooser;
import org.eu.hanana.reimu.chatimage.networking.*;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.eu.hanana.reimu.chatimage.ChatimageMod.MOD_ID;

@Mod(MOD_ID)
public class ChatimageMod {
    public static final String MOD_ID = "chatimage";
    public static final Logger logger = LogManager.getLogger();
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
            // The registry we want to use.
            // Minecraft's registries can be found in BuiltInRegistries, NeoForge's registries can be found in NeoForgeRegistries.
            // Mods may also add their own registries, refer to the individual mod's documentation or source code for where to find them.
            BuiltInRegistries.MENU,
            // Our mod id.
            MOD_ID
    );
    public static boolean GLOBAL_PROTOCOL=true;
    public static final Supplier<MenuType<MenuCiManager>> CI_MANAGER_MENU = MENUS.register("cim_menu", () -> new MenuType<>(MenuCiManager::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<MenuCiManager>> FILE_CHOOSER_MENU = MENUS.register("cim_file_chooser", () -> new MenuType<>(MenuCiManager::new, FeatureFlags.DEFAULT_FLAGS));
    public ChatimageMod(IEventBus modBus, ModContainer container) {
        NeoForge.EVENT_BUS.register(new EventHandler());
        modBus.addListener(this::registerScreens);
        modBus.addListener(this::registerPayloads);
        MENUS.register(modBus);
        modBus.addListener(this::init);
    }
    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                PayloadOpenGui.TYPE,
                PayloadOpenGui.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        HandlerOpenGui::handleData,
                        HandlerOpenGui::handleData
                )
        );
        registrar.playBidirectional(
                PayloadUpload.TYPE,
                PayloadUpload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        HandlerUploadCl::handleData,
                        HandlerUploadSv::handleData
                )
        );
        registrar.playBidirectional(
                PayloadDownload.TYPE,
                PayloadDownload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        HandlerDownloadCl::handleData,
                        HandlerDownloadSv::handleData
                )
        );
    }
    private void init(FMLCommonSetupEvent event){
        try {
            Util.deleteDirectory(new File(".","chatimage"));
        }catch (Exception e){
            logger.error(e);
        }
        GLOBAL_PROTOCOL=true;
        try {
            // 注册自定义的URL流处理器工厂
            URL.setURLStreamHandlerFactory(new ChatimageURLStreamHandlerFactory());
        }catch (Throwable e){
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
                logger.error("Failed to reg ci protocol!Using default!");
            }
        }
    }
    private void registerScreens(RegisterMenuScreensEvent event) {

        event.register(CI_MANAGER_MENU.get(), ScreenCiManager::new);
        event.register(FILE_CHOOSER_MENU.get(), ScreenFileChooser::new);
    }
}
