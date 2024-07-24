package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractContainerEventHandler implements Renderable {
    @Inject(method = "keyPressed(III)Z",at = @At("HEAD"),cancellable = true)
    public void keyPressed(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> callbackInfo) {
        if ((Object)this instanceof ChatScreen chatScreen){
            if (pKeyCode == 256 && chatScreen.shouldCloseOnEsc()) {
                chatScreen.onClose();
            }
            callbackInfo.setReturnValue(super.keyPressed(pKeyCode, pScanCode, pModifiers));
            callbackInfo.cancel();
        }
    }
}
