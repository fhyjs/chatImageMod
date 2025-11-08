package org.eu.hanana.reimu.chatimage;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import static org.eu.hanana.reimu.chatimage.ChatimageMod.MOD_ID;

@Mod(value = MOD_ID,dist = Dist.CLIENT)
public class ChatimageModClient {
    public ChatimageModClient(IEventBus modBus,ModContainer container){
        NeoForge.EVENT_BUS.register(new EventHandlerClient());
        clientSideInit(container);
        modBus.addListener(this::registerScreens);
    }
    private void clientSideInit(ModContainer container){
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> new ConfigurationScreen(container, parent));
    }
    private void registerScreens(RegisterMenuScreensEvent event) {
        //event.register(MenuRegister.EMPTY_MENU.get(), ChatimageScreen::new);
    }
}
