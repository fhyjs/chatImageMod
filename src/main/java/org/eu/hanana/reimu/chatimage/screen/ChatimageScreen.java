package org.eu.hanana.reimu.chatimage.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.ChatImage;

import java.net.URI;

public class ChatimageScreen extends Screen {
    public static final SystemToast.SystemToastId CHATIMAGE_TOAST_ID = new SystemToast.SystemToastId(2000);
    public static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    public EditBox editBoxText;
    public EditBox editBoxUrl;
    public EditBox editBoxDescribe;
    public EditBox editBoxW;
    public EditBox editBoxH;
    public Runnable afterInit= () -> {};
    public ChatimageScreen() {
        super(Component.translatable("gui.ci.text"));
    }
    @Override
    protected void init() {
        var x = this.width / 2;
        var y = this.height / 2;

        addRenderableWidget(Button.builder(Component.literal("X"),button -> {
            onClose();
        }).bounds(x+103,y-77,14,14).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.send"),button -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.gui.getChat().addRecentChat(editBoxText.getValue());
                this.minecraft.player.connection.sendChat(editBoxText.getValue());
            }
            onClose();
        }).bounds(x+90,y+58,28,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.add"),button -> {
            ChatImage.ChatImageData chatImageData;
            try {
                chatImageData = new ChatImage.ChatImageData(URI.create(editBoxUrl.getValue()).toString(), Integer.parseInt(editBoxW.getValue()), Integer.parseInt(editBoxH.getValue()), editBoxDescribe.getValue());
            } catch (Exception e) {
                ChatimageMod.logger.error("error gen ci code", e);
                if (minecraft != null) {
                    minecraft.getToastManager().addToast(new SystemToast(CHATIMAGE_TOAST_ID, Component.translatable("msg.ci.error_gen"), Component.literal(e.toString())));
                }
                return;
            }
            editBoxText.setValue(editBoxText.getValue()+ chatImageData);
            editBoxDescribe.setValue("");
            editBoxH.setValue("");
            editBoxW.setValue("");
            editBoxUrl.setValue("");
        }).bounds(x-35,y+5,20,20).build());

        addRenderableWidget(editBoxText=new EditBox(font,x-116,y-64,100,20,Component.empty()));
        addRenderableWidget(editBoxUrl=new EditBox(font,x-116,y-20,100,20,Component.empty()));
        addRenderableWidget(editBoxW=new EditBox(font,x-116,y+5,30,20,Component.empty()));
        addRenderableWidget(editBoxH=new EditBox(font,x-75,y+5,30,20,Component.empty()));
        addRenderableWidget(editBoxDescribe=new EditBox(font,x-116,y+40,100,20,Component.empty()));
        editBoxText.setMaxLength(50000);
        editBoxUrl.setMaxLength(100);
        editBoxW.setMaxLength(1000);
        editBoxH.setMaxLength(1000);
        editBoxDescribe.setMaxLength(1000);

        afterInit.run();
    }
    @Override
    public void renderBackground(GuiGraphics p_283391_, int p_295532_, int p_296277_, float p_295918_) {
        super.renderBackground(p_283391_, p_295532_, p_296277_, p_295918_);

        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        p_283391_.blit(RenderPipelines.GUI_TEXTURED, DEMO_BACKGROUND_LOCATION, i, j, 0.0F, 0.0F, 248, 166, 256, 256);
    }

    @Override
    public void render(GuiGraphics p_281247_, int p_281844_, int p_283693_, float p_281842_) {
        super.render(p_281247_, p_281844_, p_283693_, p_281842_);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        var x = this.width / 2;
        var y = this.height / 2;

        p_281247_.drawString(this.font, this.title, i, j, -14737633, false);
        p_281247_.drawString(this.font, "URL", x-111, y-28, -14737633, false);
        p_281247_.drawString(this.font, Component.translatable("gui.ci.height"), x-43, y+13, -14737633, false);
        p_281247_.drawString(this.font, Component.translatable("gui.ci.width"), x-85, y+13, -14737633, false);
        p_281247_.drawString(this.font, Component.translatable("gui.ci.info"), x-111, y+30, -14737633, false);
    }
}
