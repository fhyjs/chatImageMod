package org.eu.hanana.reimu.chatimage.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.fml.i18n.I18nManager;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.core.ChatImage;

import java.io.IOException;
import java.util.Objects;

public class ScreenCiManager extends AbstractContainerScreen<MenuCiManager> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
    public EditBox textToSend;
    public EditBox url;
    public EditBox w;
    public EditBox h;
    public EditBox info;
    public ScreenCiManager(MenuCiManager pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth=256;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.literal("X"),(button)->{
            ScreenCiManager.this.onClose();
        }).bounds(getGuiLeft()+getXSize()-30,getGuiTop()+7,15,15).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.send"),(button)->{
            this.getMinecraft().gui.getChat().addRecentChat(textToSend.getValue());
            Objects.requireNonNull(this.getMinecraft().player).connection.sendChat(textToSend.getValue());
            ScreenCiManager.this.onClose();
        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-30,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.clear"),(button)->{
            try {
                ChatImage.clearCache();
                getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("SUCCESS/完成"),Component.literal("OK")));
            } catch (Throwable e) {
                Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
            }

        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-60,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.upload"),(button)->{
            ScreenFileChooser fileChooser = new ScreenFileChooser(menu, getMinecraft().player.getInventory(), Component.literal("FileChooser"));
            fileChooser.setParent(this);
            getMinecraft().setScreen(fileChooser);
            fileChooser.setCallback((path)->{
                getMinecraft().setScreen(ScreenCiManager.this);
                Thread thread = new Thread(()->{
                    try {
                        url.setValue(Util.upload(path));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Minecraft.getInstance().execute(()->{
                        getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("SUCCESS/完成"),Component.literal("OK")));
                    });
                });
                thread.setUncaughtExceptionHandler((t,e)->{
                    Minecraft.getInstance().execute(()->{
                        Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
                    });
                });
                getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("WORKING/处理中"),Component.literal("DO NOT CLOSE THIS WINDOW/不要关闭本窗口")));
                thread.start();
            });


        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-100,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.add"),(button)->{
            try {
                ChatImage chatImage = ChatImage.getChatImage(new ChatImage.ChatImageData(url.getValue(), Integer.parseInt(w.getValue()), Integer.parseInt(h.getValue()), info.getValue()).toCiCode());
                ChatImage.ChatImageData chatImageData = new ChatImage.ChatImageData(chatImage);
                textToSend.setValue(textToSend.getValue()+chatImageData);

                url.setValue("");
                w.setValue("");
                h.setValue("");
                info.setValue("");
            }catch (Throwable e){
                ChatimageMod.logger.warn(e);
                getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("ERROR/错误"),Component.literal(e.toString())));
            }
        }).bounds(getGuiLeft()+100,getGuiTop()+112,20,20).build());
        textToSend = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+30,100,20,Component.translatable("gui.ci.gen")));
        url = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+90,100,20,Component.literal("URL")));
        w = addRenderableWidget(new EditBox(this.font,getGuiLeft()+20,getGuiTop()+112,30,20,Component.translatable("gui.ci.width")));
        h = addRenderableWidget(new EditBox(this.font,getGuiLeft()+60,getGuiTop()+112,30,20,Component.translatable("gui.ci.height")));
        info = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+142,100,20,Component.translatable("gui.ci.info")));
        url.setMaxLength(114514);
        textToSend.setMaxLength(114514);
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
        graphics.drawString(this.font, I18n.get("gui.ci.title"),7,10,0);
        graphics.drawString(this.font, I18n.get("gui.ci.text"),10,20,0);
        graphics.drawString(this.font, I18n.get("gui.ci.width"),10,115,0);
        graphics.drawString(this.font, I18n.get("gui.ci.height"),50,115,0);
        graphics.drawString(this.font, I18n.get("gui.ci.info"),10,132,0);
        graphics.drawString(this.font, "URL",10,80,0);
    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
