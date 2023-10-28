package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ScreenCILocalImg extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation( "textures/gui/demo_background.png");
    private final GuiScreen par;
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    public ScreenCILocalImg(GuiScreen par) {
        super(new Jvav_C());
        this.par=par;
        this.xSize = 256;
        this.ySize = 170;
        this.guiTop = (int) (sr.getScaledHeight()*0.08);
        this.guiLeft = (int) (sr.getScaledWidth()*0.21);
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
        buttonList.add(new GuiButton(1, guiLeft+15, guiTop+20,40, 20, lang_cn?"选择文件":"Choose a file"));
        buttonList.add(new GuiButton(2, guiLeft+15, guiTop+55,40, 20, lang_cn?"清除":"Clear"));

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(lang_cn?"使用本地图片":"use local img",10,10,0);
        fontRenderer.drawString(lang_cn?"清除缓存":"Clear cache",10,42,0);
        // 使用字体渲染方法在GUI上绘制文本
        //this.fontRenderer.drawString(iTextComponent., 50, 50, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.color(0,0,0,1);


        GlStateManager.popMatrix();
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);


    }
    @Override
    public void handleKeyboardInput() throws IOException
    {
        char c0 = Keyboard.getEventCharacter();
        if (c0=='\u001B'){
            mc.currentScreen=par;
            return;
        }
        if (c0=='e') {
            this.keyTyped(c0,c0);
            return;
        }
        super.handleKeyboardInput();
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id){
            case 0:
                mc.currentScreen=par;
                break;
            case 1:
                String fp = Utils.FileChooser();
                if (fp != null) {
                    File file = new File(fp);
                    try {
                        if(ImageIO.read(file)==null)
                            throw new RuntimeException("ERROR:"+(lang_cn?"这不是图片":"Not img"));
                    }catch (Exception e){
                        mc.getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,new TextComponentString("ERROR:"+(lang_cn?"请检查输入":"Please Check Your Inputs")).setStyle(new Style().setColor(TextFormatting.RED)),new TextComponentString(e.getMessage())));
                        break;
                    }


                    try {
                        byte[] bytes = Utils.ReadFile(file);
                        String surl = Utils.SendLByte(bytes);
                        if (par instanceof ScreenCIChat){
                            if (surl != null) {
                                ((ScreenCIChat) par).textFieldCIURL.setText(surl);
                                mc.currentScreen=par;
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                break;
            case 2:
                ChatImage.clearCache();
                mc.getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,new TextComponentString("SUCCESS:").setStyle(new Style().setColor(TextFormatting.GREEN)),new TextComponentString(lang_cn?"已清除缓存":"Cleared")));
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
