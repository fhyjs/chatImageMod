package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.util.StringUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringUtil.class)
public class MixinStringUtil {
    @Inject(method = {"trimChatMessage"},at = @At("HEAD"), cancellable = true)
    private static void trimChatMessage(String string, CallbackInfoReturnable<String> cir){
        cir.setReturnValue(string);
        cir.cancel();
    }
}
