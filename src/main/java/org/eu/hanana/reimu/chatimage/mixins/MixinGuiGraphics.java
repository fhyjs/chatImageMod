package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.eu.hanana.reimu.chatimage.client.RenderCi;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {
    @Inject(method = "renderComponentHoverEffect",at = @At("RETURN"))
    public void renderComponentHoverEffect(Font pFont, @Nullable Style pStyle, int pMouseX, int pMouseY, CallbackInfo callbackInfo) {
        if (pStyle!=null&&pStyle.getHoverEvent()!=null){
            HoverEvent hoverEvent = pStyle.getHoverEvent();
            Component component = hoverEvent.getValue(Actions.getShowImage());
            if (component != null) {
                RenderCi.render(pFont, component.getString(), ((GuiGraphics)((Object) this)), pMouseX, pMouseY);
            }
        }
    }
}
