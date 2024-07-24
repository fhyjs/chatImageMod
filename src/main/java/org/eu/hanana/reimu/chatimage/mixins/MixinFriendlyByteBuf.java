package org.eu.hanana.reimu.chatimage.mixins;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FriendlyByteBuf.class)
public abstract class MixinFriendlyByteBuf extends ByteBuf implements net.neoforged.neoforge.common.extensions.IFriendlyByteBufExtension{
    @Shadow
    public ByteBuf source;
    @Inject(method = "readUtf(I)Ljava/lang/String;",at = @At("HEAD"),cancellable = true)
    public void readUtf(int maxLen, CallbackInfoReturnable<String> callbackInfo){
        if(maxLen==256){
            callbackInfo.setReturnValue(Utf8String.read(source,32767));
            callbackInfo.cancel();
        }
    }
    @Inject(method = "writeUtf(Ljava/lang/String;I)Lnet/minecraft/network/FriendlyByteBuf;",at = @At("HEAD"),cancellable = true)
    public void writeUtf(String pString, int pMaxLength, CallbackInfoReturnable<FriendlyByteBuf> callbackInfo){
        if(pMaxLength==256){
            Utf8String.write(source, pString, 32767);
            callbackInfo.setReturnValue((FriendlyByteBuf)((Object) this));
            callbackInfo.cancel();
        }
    }
}
