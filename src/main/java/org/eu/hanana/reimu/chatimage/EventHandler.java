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

public class EventHandler {
    @SubscribeEvent
    public void onClientDisconnected(ClientPlayerNetworkEvent.LoggingOut event){
        ChatImage.clearCache();
        FtpManager.ftpDownloadPendingCallback.clear();
        FtpManager.ftpUploadPendingData.clear();
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event){
        List<Component> flatList = event.getMessage().toFlatList();
        var result = Component.empty();
        for (Component component : flatList) {
            String string = component.getString();
            List<String> parts = splitByCiCodes(string);
            for (String part : parts) {
                if (part.startsWith("CI{")){
                    try {
                        ChatImage.getChatImage(part);
                        if (FMLEnvironment.dist.isDedicatedServer())
                            ChatImage.clearCache();
                        result.append(Component.translatable("msg.ci.photo").setStyle(
                                component.getStyle()
                                        .withColor(ChatFormatting.GREEN)
                                        .withHoverEvent(new Actions.ShowImage(Component.literal(part)))
                                        .withClickEvent(new Actions.ViewImage(part))
                        ));
                    } catch (Throwable e) {
                        result.append(Component.translatable("msg.ci.photo").setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.RED)
                                        .withHoverEvent(new HoverEvent.ShowText(Component.translatable("msg.ci.error_gen").append(Component.literal("\n")).append(Component.literal(e.toString()))))
                        ));
                    }
                }else {
                    result.append(Component.literal(part).setStyle(component.getStyle()));
                }
            }
        }
        event.setMessage(result);
    }

}
