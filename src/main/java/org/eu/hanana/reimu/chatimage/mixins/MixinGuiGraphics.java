package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImageToolTipRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics {
    @Shadow @Nullable private Runnable deferredTooltip;

    @Inject(method = {"renderComponentHoverEffect"},at= @At("HEAD"),cancellable = true)
    public void renderComponentHoverEffect(Font font, Style style, int mouseX, int mouseY, CallbackInfo ci){
        if (style != null && style.getHoverEvent() != null) {
            var he = style.getHoverEvent();
            if (he.action()==Actions.getShowImage()){
                if (this.deferredTooltip == null) {
                    this.deferredTooltip = () -> ChatImageToolTipRender.renderTooltip((((GuiGraphics)(Object) this)),font,mouseX,mouseY,((Actions.ShowImage) he).component().getString());
                }
                ci.cancel();
            }
        }
    }
}
