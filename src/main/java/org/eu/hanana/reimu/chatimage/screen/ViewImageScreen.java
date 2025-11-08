package org.eu.hanana.reimu.chatimage.screen;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.config.ChatImageConfig;
import org.eu.hanana.reimu.chatimage.core.ChatImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ViewImageScreen extends Screen {
    public static final SystemToast.SystemToastId CHATIMAGE_TOAST_ID = new SystemToast.SystemToastId(2000);
    public static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    private ChatImage image;
    protected float img_x,img_y,scale=1;
    private boolean dragging;

    public ViewImageScreen(String value) {
        super(Component.translatable("gui.ci.info"));
        try {
            image=ChatImage.getChatImage(value);
        } catch (Throwable e) {
            ChatimageMod.logger.error("Can not gen ci instance.",e);
        }
    }
    @Override
    protected void init() {
        var x = this.width / 2;
        var y = this.height / 2;

        addRenderableWidget(Button.builder(Component.literal("X"),button -> {
            onClose();
        }).bounds(x+103,y-77,14,14).build());

        addRenderableWidget(Button.builder(Component.literal("+"),(button)->{
            scale+=0.25f*111*111/image.w/image.h;
        }).bounds(x-80,y-66,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("O"),(button)->{
            reset();
        }).bounds(x-60,y-66,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("-"),(button)->{
            scale-=0.25f*111*111/image.w/image.h;
        }).bounds(x-40,y-66,15,15).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.raw"),(button)->{
            image.viewRaw(true);
            removeWidget(button);
            reset();
        }).bounds(x-20,y-66,45,15).build());
        addRenderableWidget(Button.builder(Component.translatable("chat.copy"), new Button.OnPress() {
            @Override
            public void onPress(Button button) {
                if(ChatImageConfig.copy_base64){
                    ResourceLocation texture = image.getTexture();
                    if (texture==null){
                        return;
                    }
                    try{
                        DynamicTexture texture1 = (DynamicTexture) getMinecraft().getTextureManager().getTexture(texture);
                        BufferedImage bufferedImage = org.eu.hanana.reimu.chatimage.Util.convertToBufferedImage(texture1.getPixels());
                        // 将 BufferedImage 转换为字节数据
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "PNG", baos);
                        byte[] imageData = baos.toByteArray();

                        // 使用 GLFW 设置剪贴板
                        getMinecraft().keyboardHandler.setClipboard("image/png;base64," + Base64.getEncoder().encodeToString(imageData));
                    }catch (Exception e){
                        ChatimageMod.logger.error(e);
                    }

                }else {
                    getMinecraft().keyboardHandler.setClipboard(new ChatImage.ChatImageData(image).toString());
                }
            }
        }).bounds(x+30,y-66,40,15).build());

        reset();
    }

    @Override
    public void onClose() {
        super.onClose();
        image.viewRaw(false);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        scale+= (float) (pScrollY*0.25f*111*111/image.w/image.h);
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }
    private void reset() {
        var x = this.width / 2;
        var y = this.height / 2;
        scale= (float) getBestScale(image.w,image.h,220,120);
        img_x=x-110+(220 - image.w*scale) / 2;
        img_y=y-50+(120 - image.h*scale) / 2;

    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var flag = false;
        var x = this.width / 2;
        var y = this.height / 2;
        if (mouseX>=x-110&&mouseX<=x+110&&mouseY>=y-50&&mouseY<=y+50) {
            flag=true;
            dragging=true;
        }
        return super.mouseClicked(mouseX, mouseY, button)||flag;
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging=false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        //System.out.println(pMouseX+","+pMouseY);
        if (dragging) {
            img_x += (float) pDragX;
            img_y += (float) pDragY;
        }
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }
    @Override
    public void renderBackground(GuiGraphics pGuiGraphics, int p_295532_, int p_296277_, float p_295918_) {
        super.renderBackground(pGuiGraphics, p_295532_, p_296277_, p_295918_);
        var x = this.width / 2;
        var y = this.height / 2;

        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, DEMO_BACKGROUND_LOCATION, i, j, 0.0F, 0.0F, 248, 166, 256, 256);
        pGuiGraphics.enableScissor(x-110,y-50, x+110,y+70);
        pGuiGraphics.fill(0,0, width,height,0xB4000000);
        var texture = image.getTexture();
        pGuiGraphics.pose().pushMatrix();
        pGuiGraphics.pose().translate(img_x,img_y);
        pGuiGraphics.pose().scale(scale,scale);
        if (texture!=null) {
            pGuiGraphics.blitInscribed(texture, 0, 0, image.w, image.h, image.w, image.h, true, true);
        }
        pGuiGraphics.pose().popMatrix();
        pGuiGraphics.disableScissor();

    }
    public static double getBestScale(int w, int h, int w1, int h1) {
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("图片宽高必须大于0");
        }
        double sx = (double) w1 / w;
        double sy = (double) h1 / h;
        return Math.min(sx, sy);
    }
    @Override
    public void render(GuiGraphics p_281247_, int p_281844_, int p_283693_, float p_281842_) {
        super.render(p_281247_, p_281844_, p_283693_, p_281842_);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        var x = this.width / 2;
        var y = this.height / 2;

        p_281247_.drawString(this.font, title, i, j, -14737633, false);
        p_281247_.drawString(this.font, String.format("x%.2f",scale), x+80, y-60, -14737633, false);
    }
}
