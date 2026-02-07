package org.eu.hanana.reimu.chatimage.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.ClickEvent;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.screen.ViewImageScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public class MixinScreen {
    @Inject(method = {"defaultHandleClickEvent"},at=@At("RETURN"))
    private static void defaultHandleClickEvent(ClickEvent clickEvent, Minecraft minecraft, Screen screen, CallbackInfo ci) {
        if (clickEvent.action()== Actions.getViewImage()) {
            ChatimageMod.logger.info("But the chatimage mod knows!");
            minecraft.setScreen(new ViewImageScreen(((Actions.ViewImage) clickEvent).value()));
        }
    }
    @Inject(method = {"keyPressed"},at=@At("HEAD"), cancellable = true)
    public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (event.key()!= InputConstants.KEY_DOWN &&event.key()!= InputConstants.KEY_UP) return;
        if (((Screen)((Object) this)).getClass() == ChatScreen.class){
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
