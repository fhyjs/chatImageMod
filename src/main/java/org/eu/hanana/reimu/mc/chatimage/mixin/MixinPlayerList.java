package org.eu.hanana.reimu.mc.chatimage.mixin;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
@Mixin(PlayerList.class)
public abstract class MixinPlayerList{
    @Final
    @Shadow
    private MinecraftServer mcServer;
    @Inject(at=@At("HEAD"),method = "sendMessage(Lnet/minecraft/util/text/ITextComponent;Z)V", cancellable = true)
    public void sendMessage(ITextComponent component, boolean isSystem, CallbackInfo ci)
    {
        ServerChatEvent event;
        if (component instanceof TextComponentTranslation){
            if (((TextComponentTranslation) component).getKey().equals("chat.type.text")) {
                String unformattedText = component.getUnformattedText();
                Pattern pattern = Pattern.compile("<(.*?)>");
                Matcher matcher = pattern.matcher(unformattedText);

                EntityPlayerMP playerByUsername = null;
                if (matcher.find()) {
                    String result = matcher.group(1); // 获取第一个匹配到的括号里的内容
                    playerByUsername = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(result);
                } else {
                    ChatImageMod.logger.warn("Can not get message sender!");
                }
                if (playerByUsername != null) {
                    unformattedText=unformattedText.substring(playerByUsername.getName().length()+3);
                    ChatImageMod.INSTANCE.eventHandler.onServerSendMessage(event = new ServerChatEvent(playerByUsername, unformattedText, component));
                    component = event.getComponent();
                }
            }
        }
        this.mcServer.sendMessage(component);
        ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
        this.sendPacketToAllPlayers(new SPacketChat(component, chattype));
        ci.cancel();
    }
    @Shadow
    public void sendPacketToAllPlayers(Packet<?> packetIn){
        // Shadowed method to be modified or used
    }

}
