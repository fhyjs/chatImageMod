package org.eu.hanana.reimu.chatimage.gui;

import com.google.gson.Gson;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.fml.i18n.I18nManager;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.chatimage.core.ImageStatus;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

public class ScreenImageInfo extends AbstractContainerScreen<MenuCiManager> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
    private ChatImage image;
    private float scale;
    private double img_x,img_y;
    public ScreenImageInfo(MenuCiManager pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth=256;
    }

    @Override
    protected void init() {
        super.init();
        ExtraData extraData = new Gson().fromJson(getMenu().data, ExtraData.class);
        try {
            this.image=ChatImage.getChatImage(extraData.extra());
        } catch (Throwable e) {
            getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
        }
        addRenderableWidget(Button.builder(Component.literal("X"),(button)->{
            ScreenImageInfo.this.onClose();
        }).bounds(getGuiLeft()+getXSize()-30,getGuiTop()+7,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("+"),(button)->{
            scale+=0.25f*111*111/image.w/image.h;
        }).bounds(getGuiLeft()+getXSize()-80,getGuiTop()+23,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("O"),(button)->{
            reset();
        }).bounds(getGuiLeft()+getXSize()-60,getGuiTop()+23,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("-"),(button)->{
            scale-=0.25f*111*111/image.w/image.h;
        }).bounds(getGuiLeft()+getXSize()-40,getGuiTop()+23,15,15).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.download"),(button)->{
            ResourceLocation texture = image.getTexture();
            if (texture!=null){
                DynamicTexture texture1 = (DynamicTexture) getMinecraft().getTextureManager().getTexture(texture);
                ScreenFileChooser screenFileChooser = new ScreenFileChooser(getMenu(),getMinecraft().player.getInventory(),Component.literal("FileChooser"));
                screenFileChooser.setSaveMode(true);
                screenFileChooser.setParent(this);
                screenFileChooser.setDefaultName(String.format("chat_image_%s.png",texture.getPath().replace("/","-")));
                screenFileChooser.setCallback((file)->{
                    if (!file.endsWith(".png"))
                        file+=".png";
                    File file1 = new File(file);
                    try {
                        Objects.requireNonNull(texture1.getPixels()).writeToFile(file1);
                        getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("Success/完成"), Component.literal(file)));
                    } catch (Exception e) {
                        getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
                        e.printStackTrace();
                    }
                });
                getMinecraft().setScreen(screenFileChooser);
            }
        }).bounds(getGuiLeft()+getXSize()-200,getGuiTop()+23,35,15).build());
        reset();
    }
    private void reset(){
        scale=1 ;
        img_x = 10 + (getXSize() - 30) / 2f - image.w * scale / 2;
        img_y = 50 + (getYSize() - 60) / 2f - image.h * scale / 2;
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
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        img_x+=  pDragX;
        img_y+=  pDragY;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        scale+= (float) (pScrollY*0.25f*111*111/image.w/image.h);
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {

        // Assume we have some Component 'label'
        // 'label' is drawn at 'labelX' and 'labelY'
        graphics.drawString(this.font, String.format("%s: %s",I18n.get("gui.ci.info"),image.info),7,10,0);
        var texture = image.getTexture();
        graphics.enableScissor(getGuiLeft()+10,getGuiTop()+50, getGuiLeft()+10+getXSize()-30,getGuiTop()+50+getYSize()-60);
        graphics.fill(0,0, getXSize(),getYSize(),0xB4000000);
        graphics.pose().pushPose();
        graphics.pose().translate(img_x,img_y,0);
        graphics.pose().scale(scale,scale,1);
        if (texture!=null) {
            graphics.blit(texture, 0, 0, 0, 0, image.w,image.h,image.w,image.h);
        }
        graphics.pose().popPose();
        graphics.disableScissor();
        var color = 0xff00d0;
        if (image.status== ImageStatus.OK)
            color=0x22ff00;
        else if (image.status== ImageStatus.WAIT)
            color=0xeeff00;
        else if (image.status== ImageStatus.ERROR)
            color=0xff2a00;
        graphics.drawString(this.font, String.format("[%s]",image.status),15,55,color);
        graphics.drawString(this.font, String.format("URL:%s",image.url),17,38,0);
        graphics.drawString(this.font, String.format("%.3fx",scale),getXSize()-122,28,0);

    }
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
