package org.eu.hanana.reimu.mc.chatimage.mixin;

import io.netty.handler.codec.EncoderException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
@Mixin(PacketBuffer.class)
public abstract class MixinPacketBuffer {
    @Inject(at=@At("HEAD"),method = "writeTextComponent",cancellable = true)
    private void writeTextComponent(ITextComponent component, CallbackInfoReturnable<PacketBuffer> cir) {
        ServerChatEvent event;
            if (component.getUnformattedText().startsWith("<")) {
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
                    ChatImageMod.logger.info("CI_JSON found! Making a TextComponent. _ChatImageMod_");
                }
            }
        cir.setReturnValue(this.writeString(ITextComponent.Serializer.componentToJson(component)));
        cir.cancel();
    }
    @Shadow
    public PacketBuffer writeString(String string)
    {
        return null;
    }
}
