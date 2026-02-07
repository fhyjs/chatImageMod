package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImageToolTipRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {
    @Shadow @Nullable private Runnable deferredTooltip;

    @Shadow protected abstract void setTooltipForNextFrameInternal(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, @org.jetbrains.annotations.Nullable Identifier background, boolean focused);

    @Inject(method = {"renderComponentHoverEffect"},at= @At("HEAD"),cancellable = true)
    public void renderComponentHoverEffect(Font font, Style style, int mouseX, int mouseY, CallbackInfo ci){
        if (style != null && style.getHoverEvent() != null) {
            var he = style.getHoverEvent();
            if (he.action()==Actions.getShowImage()){
                setTooltipForNextFrameInternal(font,ChatImageToolTipRender.getClientTooltipComponents(((Actions.ShowImage) he).component().getString()),mouseX,mouseY, DefaultTooltipPositioner.INSTANCE,null,false);
                ci.cancel();
            }
        }
    }
}
