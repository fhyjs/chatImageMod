package org.eu.hanana.reimu.chatimage.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.core.ChatImage;

import java.io.IOException;
import java.util.Objects;

public class ScreenImageInfo extends AbstractContainerScreen<MenuCiManager> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
    public ScreenImageInfo(MenuCiManager pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth=256;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("X"),(button)->{
            ScreenImageInfo.this.onClose();
        }).bounds(getGuiLeft()+getXSize()-30,getGuiTop()+7,15,15).build());
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(pKeyCode,pScanCode))
            return false;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {

        // Assume we have some Component 'label'
        // 'label' is drawn at 'labelX' and 'labelY'
        graphics.drawString(this.font, I18n.get("gui.ci.info"),7,10,0);
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
