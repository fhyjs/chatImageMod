package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.SignedMessageBody;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SignedMessageBody.Packed.class)
public abstract class MixinSignedMessageBodyPacked {

    @Redirect(
        method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/FriendlyByteBuf;readUtf(I)Ljava/lang/String;"
        )
    )
    private static String redirectReadUtf(FriendlyByteBuf buf, int limit) {
        // 扩展到 512
        return buf.readUtf(Integer.MAX_VALUE);
    }

    @Redirect(
        method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/FriendlyByteBuf;writeUtf(Ljava/lang/String;I)Lnet/minecraft/network/FriendlyByteBuf;"
        )
    )
    private FriendlyByteBuf redirectWriteUtf(FriendlyByteBuf buf, String content, int limit) {
        // 同样扩展到 512
        return buf.writeUtf(content, Integer.MAX_VALUE);
    }
}
