package org.eu.hanana.reimu.chatimage;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.gui.MenuCiManager;
import org.eu.hanana.reimu.chatimage.gui.ScreenCiManager;
import org.eu.hanana.reimu.chatimage.networking.HandlerOpenGui;
import org.eu.hanana.reimu.chatimage.networking.PayloadOpenGui;

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
    public static final Supplier<MenuType<MenuCiManager>> CI_MANAGER_MENU = MENUS.register("cim_menu", () -> new MenuType<>(MenuCiManager::new, FeatureFlags.DEFAULT_FLAGS));
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
    }
    private void init(FMLCommonSetupEvent event){
    }
    private void registerScreens(RegisterMenuScreensEvent event) {

        event.register(CI_MANAGER_MENU.get(), ScreenCiManager::new);
    }
}
