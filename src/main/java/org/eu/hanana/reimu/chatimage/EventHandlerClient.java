package org.eu.hanana.reimu.chatimage;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.chatimage.network.transporter.FtpManager;
import org.eu.hanana.reimu.chatimage.screen.ChatimageScreen;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.eu.hanana.reimu.chatimage.Util.splitByCiCodes;

public class EventHandlerClient {
    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Post event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {

        Screen screen = event.getScreen();
        if (screen instanceof ChatScreen chatScreen){
            ExtendedButton addBtn = new ExtendedButton(new Button.Builder(Component.literal("+"), button -> {
                var cis = new ChatimageScreen();
                cis.afterInit=()->{
                  cis.editBoxText.setValue(chatScreen.input.getValue());
                };
                screen.getMinecraft().setScreen(cis);
            }).pos(0,0).size(20,20));
            screen.addRenderableWidget(addBtn);
            chatScreen.input.setMaxLength(100000);
        }
    }

}
