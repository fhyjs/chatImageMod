package org.eu.hanana.reimu.chatimage.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.gui.ModListScreen;

public class ClientChatImageTooltip implements ClientTooltipComponent {
    private final ChatImage ci;

    public ClientChatImageTooltip(ChatImage chatImage){
        this.ci = chatImage;
    }
    @Override
    public int getWidth(Font font) {
        if (ci.status!=ImageStatus.OK) return 0;
        return ci.w+4;
    }

    @Override
    public int getHeight(Font font) {
        if (ci.status!=ImageStatus.OK) return 0;
        return ci.h+4;
    }

    @Override
    public void renderText(GuiGraphics guiGraphics, Font font, int x, int y) {

    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        ClientTooltipComponent.super.renderImage(font, x, y, width, height, guiGraphics);
        if (ci==null) return;
        ResourceLocation texture = ci.getTexture();
        if (texture==null) return;
        guiGraphics.blitInscribed(texture, x+2, y+2, ci.w, ci.h, ci.w, ci.h, true, true);
    }
}