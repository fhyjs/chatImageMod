package org.eu.hanana.reimu.mc.chatimage.mixin;

import io.netty.handler.codec.EncoderException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.eu.hanana.reimu.mc.chatimage.ChatImageMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
@Mixin(NetHandlerPlayServer.class)
public abstract class MixinPacketBuffer {
    @Shadow
    public EntityPlayerMP player;
    @Inject(at=@At("HEAD"),method = "sendPacket",cancellable = true)
    private void writeTextComponent(Packet<?> packetIn, CallbackInfo ci) {
        if (packetIn instanceof SPacketChat) {
            ITextComponent component = ObfuscationReflectionHelper.getPrivateValue(SPacketChat.class, ((SPacketChat) packetIn), "field_148919_a");
            if (!component.getUnformattedText().contains("CI{")||ITextComponent.Serializer.componentToJson(component).contains("!*CIpd*!")){
                return;
            }
            ServerChatEvent event;
            if (player!=null) {
                String unformattedText = component.getUnformattedText();
                ChatImageMod.INSTANCE.eventHandler.onServerSendMessage(event = new ServerChatEvent(player, unformattedText, component));
                component = event.getComponent();

            }
            TextComponentString iTextComponents = new TextComponentString("");
            iTextComponents.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new TextComponentString("!*CIpd*!")));
            component.appendSibling(iTextComponents);
            packetIn=new SPacketChat(component,((SPacketChat) packetIn).getType());
            ((NetHandlerPlayServer) ((Object) this)).sendPacket(packetIn);
            ci.cancel();
        }
    }
}
