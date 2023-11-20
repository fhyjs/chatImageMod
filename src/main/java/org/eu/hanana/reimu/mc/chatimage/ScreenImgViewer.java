package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ScreenImgViewer extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation( "textures/gui/demo_background.png");
    public final ChatImage chatImage;
    private final GuiScreen par;
    public float size=1;
    int px=0,py=0,ox,oy;
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public ScreenImgViewer(ChatImage ci, GuiScreen par) {
        super(new Jvav_C());
        this.chatImage = ci;
        this.xSize = 256;
        this.par = par;
        this.ySize = 170;
        //this.guiTop = (int) (sr.getScaledHeight()*0.08);
        //this.guiLeft = (int) (sr.getScaledWidth()*0.21);
    }
    @Override
    public void updateScreen() {
        super.updateScreen();
    }
    boolean lang_cn;
    @Override
    public void initGui() {
        super.initGui();
        lang_cn=mc.getLanguageManager().getCurrentLanguage().getLanguageCode().equalsIgnoreCase("zh_cn");
        buttonList.add(new GuiButton(0, guiLeft+xSize-35, guiTop+5,20, 20, "X"));
        buttonList.add(new GuiButton(1, guiLeft+xSize-35, guiTop+27,20, 20, "-"));
        buttonList.add(new GuiButton(2, guiLeft+xSize-60, guiTop+27,20, 20, "0"));
        buttonList.add(new GuiButton(3, guiLeft+xSize-85, guiTop+27,20, 20, "+"));

        sr = new ScaledResolution(Minecraft.getMinecraft());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString((lang_cn?"图片信息":"Information"),10,10,0);
        fontRenderer.drawString((lang_cn?"状态":"Status")+":"+chatImage.status,100,10,chatImage.status.equals(ChatImage.ImageStatus.OK)?0x0da100:0xbf8600);
        fontRenderer.drawString("URL: "+chatImage.source,30,20,0);
        fontRenderer.drawString(String.format("%.1fx",size),xSize-100,32,0);

        drawImg();
        // 使用字体渲染方法在GUI上绘制文本
        //this.fontRenderer.drawString(iTextComponent., 50, 50, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseX>=guiLeft+10&&mouseX<=guiLeft+xSize-20&&mouseY>=guiTop+50&&mouseY<=guiTop+ySize-10){
            ox=mouseX;
            oy=mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        ox=-114514;
        oy=-114514;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        clickedMouseButton++;
        if (!(ox==-114514&&oy==-114514)) {
            px += (mouseX - ox)*clickedMouseButton*(isShiftKeyDown()?5:1);
            py += (mouseY - oy)*clickedMouseButton*(isShiftKeyDown()?5:1);
            oy=mouseY;
            ox=mouseX;
        }
        clickedMouseButton--;
    }

    private void drawImg(){
        // 获取渲染引擎

        drawRect(10,50,xSize-20,ySize-10,0xcf000000);
        if (chatImage.status!= ChatImage.ImageStatus.OK&&chatImage.status!= ChatImage.ImageStatus.WAITING&&chatImage.status!= ChatImage.ImageStatus.ERROR){
            chatImage.getImage(chatImage.source);
        }
        if (chatImage.status == ChatImage.ImageStatus.OK) {

            ResourceLocation resourceLocation = chatImage.getOriginalImg();
            if (resourceLocation==null){
                //textLines.add("§4"+ net.minecraft.client.resources.I18n.format("image.fail"));
            }else {

                double scaleW = mc.displayWidth / sr.getScaledWidth_double();
                double scaleH = mc.displayHeight / sr.getScaledHeight_double();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor((int)((guiLeft+10) * scaleW), (int)(mc.displayHeight - ((guiTop+ySize-10) * scaleH)),
                        (int)((xSize-30) * scaleW), (int)((ySize-60) * scaleH));

                GlStateManager.pushMatrix();

                float scaledWidth = chatImage.width * size;
                float scaledHeight = chatImage.height * size;

                // 计算在指定区域内居中绘制图片的位置
                float xOffset = 10 + (xSize - 20 - scaledWidth) / 2.0f;
                float yOffset = 50 + (ySize - 10 - scaledHeight) / 2.0f;

                // 限制绘制区域，不超过 (xSize-20, ySize-10)
                xOffset = Math.max(10, Math.min(xOffset, xSize - 20 - scaledWidth));
                yOffset = Math.max(50, Math.min(yOffset, ySize - 10 - scaledHeight));

                GlStateManager.translate(xOffset, yOffset, 0.0F);
                GlStateManager.scale(size, size, 1.0F);

                GlStateManager.color(1, 1, 1, 1);

                mc.getTextureManager().bindTexture(resourceLocation); // 绑定材质
                // 绘制材质
                drawModalRectWithCustomSizedTexture(px, py, 0, 0, chatImage.originalBI.getWidth(), chatImage.originalBI.getHeight(), chatImage.originalBI.getWidth(), chatImage.originalBI.getHeight());
                GlStateManager.popMatrix();
                //禁用Scissor测试
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.color(0,0,0,1);
        GlStateManager.popMatrix();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        chatImage.delOriginalImg();
    }

    @Override
    public void handleKeyboardInput() throws IOException
    {
        char c0 = Keyboard.getEventCharacter();
        if (c0=='\u001B'){
            this.onGuiClosed();
            mc.currentScreen=par;
            return;
        }
        if (c0=='e') {
            this.onGuiClosed();
            mc.currentScreen=par;
            return;
        }
        super.handleKeyboardInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id){
            case 0:
                this.onGuiClosed();
                mc.currentScreen = par;
                break;
            case 1:
                if (!isShiftKeyDown())
                    size-=0.2f;
                else
                    size-=0.01f;
                break;
            case 2:
                size=1;
                px=0;
                py=0;
                break;
            case 3:
                if (!isShiftKeyDown())
                    size+=.2f;
                else
                    size+=0.01f;
                break;
            case 4:

                break;
            case 5:

                break;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        // 绘制主背景
        mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, 256, 170);
    }
}
