package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class MixinScreen {

    @Inject(method = {"keyPressed"},at=@At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode!=264&&keyCode!=265) return;
        if (((Screen)((Object) this)).getClass() == ChatScreen.class){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
