package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ScreenCIChat extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation( "textures/gui/demo_background.png");
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    public GuiTextField textFieldCIURL;
    private GuiTextField textFieldCIINFO;
    private GuiTextField textFieldCIW;
    private GuiTextField textFieldCIH;
    private GuiTextField textFieldPV;
    public ScreenCIChat() {
        super(new Jvav_C());
        this.xSize = 256;
        this.ySize = 170;
        this.guiTop = (int) (sr.getScaledHeight()*0.08);
        this.guiLeft = (int) (sr.getScaledWidth()*0.21);
    }
    @Override
    public void updateScreen() {
        super.updateScreen();
        textFieldCIH.updateCursorCounter();
        textFieldCIINFO.updateCursorCounter();
        textFieldCIW.updateCursorCounter();
        textFieldCIURL.updateCursorCounter();
    }
    boolean lang_cn;
    @Override
    public void initGui() {
        super.initGui();
        lang_cn=mc.getLanguageManager().getCurrentLanguage().getLanguageCode().equalsIgnoreCase("zh_cn");
        buttonList.add(new GuiButton(0, guiLeft+xSize-35, guiTop+5,20, 20, "X"));
        buttonList.add(new GuiButton(1,guiLeft+xSize-32,guiTop+ySize-30,20,20, lang_cn?"发送":"Send"));
        buttonList.add(new GuiButton(2,guiLeft+95,guiTop+100,20,20, lang_cn?"添加":"Add"));
        textFieldCIURL = new GuiTextField(0, this.fontRenderer, guiLeft+12, guiTop+80, 100, 15);
        textFieldCIH = new GuiTextField(1, this.fontRenderer, guiLeft+12, guiTop+100, 20, 15);
        textFieldCIW = new GuiTextField(2, this.fontRenderer, guiLeft+60, guiTop+100, 20, 15);
        textFieldCIINFO = new GuiTextField(3, this.fontRenderer, guiLeft+12, guiTop+130, 100, 15);
        textFieldPV = new GuiTextField(4, this.fontRenderer, guiLeft+12, guiTop+20, 100, 15);
        textFieldPV.setMaxStringLength(Integer.MAX_VALUE);
        textFieldCIURL.setMaxStringLength(Integer.MAX_VALUE);
        buttonList.add(new GuiButton(3,guiLeft+xSize-90,guiTop+ySize-30,50,20, lang_cn?"图片管理":"Img Manager"));

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(lang_cn?"宽":"Width",35,105,0);
        fontRenderer.drawString("URL",15,70,0);
        fontRenderer.drawString(lang_cn?"高":"Height",85,105,0);
        fontRenderer.drawString(lang_cn?"图片信息":"Information",15,120,0);
        fontRenderer.drawString(lang_cn?"文本":"Text",15,10,0);
        // 使用字体渲染方法在GUI上绘制文本
        //this.fontRenderer.drawString(iTextComponent., 50, 50, 0xFFFFFF);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textFieldCIINFO.mouseClicked(mouseX,mouseY,mouseButton);
        textFieldCIW.mouseClicked(mouseX,mouseY,mouseButton);
        textFieldCIH.mouseClicked(mouseX,mouseY,mouseButton);
        textFieldCIURL.mouseClicked(mouseX,mouseY,mouseButton);
        textFieldPV.mouseClicked(mouseX,mouseY,mouseButton);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.color(0,0,0,1);

        textFieldCIURL.drawTextBox();
        textFieldCIH.drawTextBox();
        textFieldCIW.drawTextBox();
        textFieldCIINFO.drawTextBox();
        textFieldPV.drawTextBox();

        GlStateManager.popMatrix();
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        this.textFieldCIINFO.textboxKeyTyped(typedChar, keyCode);
        if ((textFieldCIW.isFocused()||textFieldCIH.isFocused())&&typedChar>='0'&&typedChar<='9'||keyCode==Keyboard.KEY_BACK||keyCode==Keyboard.KEY_DELETE||keyCode==Keyboard.KEY_LEFT||keyCode==Keyboard.KEY_RIGHT) {
            this.textFieldCIH.textboxKeyTyped(typedChar, keyCode);
            this.textFieldCIW.textboxKeyTyped(typedChar, keyCode);
        }
        this.textFieldCIURL.textboxKeyTyped(typedChar, keyCode);
        this.textFieldPV.textboxKeyTyped(typedChar, keyCode);
    }
    @Override
    public void handleKeyboardInput() throws IOException
    {
        char c0 = Keyboard.getEventCharacter();
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
                ChatImageMod.NETWORK.sendToServer(new JntmMessage(0));
                break;
            case 1:
                mc.player.sendChatMessage(textFieldPV.getText());
                textFieldPV.setText("");
                ChatImageMod.NETWORK.sendToServer(new JntmMessage(0));
                break;
            case 2:
                ChatImage.ChatImageData chatImageData = new ChatImage.ChatImageData();
                try{
                    chatImageData.url=textFieldCIURL.getText();
                    chatImageData.w=Integer.parseInt(textFieldCIW.getText());
                    chatImageData.h=Integer.parseInt(textFieldCIH.getText());
                    chatImageData.information=textFieldCIINFO.getText();
                    textFieldPV.setText(textFieldPV.getText()+chatImageData);

                    textFieldCIH.setText("");
                    textFieldCIINFO.setText("");
                    textFieldCIURL.setText("");
                    textFieldCIW.setText("");
                }catch (Throwable e){
                    mc.getToastGui().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,new TextComponentString("ERROR:"+(lang_cn?"请检查文本输入":"Please Check Your Inputs")).setStyle(new Style().setColor(TextFormatting.RED)),new TextComponentString(e.toString())));
                }
                break;
            case 3:
                mc.displayGuiScreen(new ScreenCILocalImg(this));
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
