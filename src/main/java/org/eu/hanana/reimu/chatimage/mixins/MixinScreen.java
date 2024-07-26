package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static net.minecraft.client.gui.screens.Screen.hasShiftDown;

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
    @Inject(method = "handleComponentClicked(Lnet/minecraft/network/chat/Style;)Z",at=@At("RETURN"))
    public void handleComponentClicked(@Nullable Style pStyle, CallbackInfoReturnable<Boolean> callbackInfo){
        if (pStyle == null) {
            return;
        }
        ClickEvent clickevent = pStyle.getClickEvent();
        if (hasShiftDown()) {
            return;
        }
        if (clickevent != null) {
            if (clickevent.getAction() == Actions.getViewImage()) {
                ChatimageMod.logger.info("But the Chatimage Mod knows!");
                //TODO: 显示详情
            }
        }
    }
}
