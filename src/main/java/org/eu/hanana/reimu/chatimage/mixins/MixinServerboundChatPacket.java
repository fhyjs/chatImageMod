package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerboundChatPacket.class)
public abstract class MixinServerboundChatPacket {

    @Redirect(
        method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;"
        )
    )
    private static String redirectReadUtf(FriendlyByteBuf buf, int limit) {
        // 强制扩展到512字符
        return buf.readUtf(Integer.MAX_VALUE);
    }

    @Redirect(
        method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;I)Lnet/minecraft/network/FriendlyByteBuf;"
        )
    )
    private FriendlyByteBuf redirectWriteUtf(FriendlyByteBuf buf, String message, int limit) {
        // 同样扩展到512字符
        return buf.writeUtf(message, Integer.MAX_VALUE);
    }
}
