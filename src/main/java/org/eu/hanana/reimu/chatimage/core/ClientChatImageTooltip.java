package org.eu.hanana.reimu.chatimage.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.config.ChatImageConfig;

public class ClientChatImageTooltip implements ClientTooltipComponent {
    private final ChatImage ci;
    public int w,h;
    public ClientChatImageTooltip(ChatImage chatImage){
        this.ci = chatImage;
        w=Math.min(ci.w, ChatImageConfig.maxPvHeight);
        h=Math.min(ci.h, ChatImageConfig.maxPvHeight);
    }
    @Override
    public int getWidth(Font font) {
        if (ci.status!=ImageStatus.OK) return 0;
        return w+4;
    }

    @Override
    public int getHeight(Font font) {
        if (ci.status!=ImageStatus.OK) return 0;
        return h+4;
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {

    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        ClientTooltipComponent.super.renderImage(font, x, y, width, height, guiGraphics);
        if (ci==null) return;
        ResourceLocation texture = (ResourceLocation) ci.getTexture();
        if (texture==null) return;
        try {
            guiGraphics.blitInscribed(texture, x+2, y+2, w, h, w, h, true, true);
        } catch (Exception e) {
            ChatimageMod.logger.info("Detected a unexpected bug fixed",e);
            ci.viewRaw(false);
        }

    }
}