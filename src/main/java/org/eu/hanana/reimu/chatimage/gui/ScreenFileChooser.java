package org.eu.hanana.reimu.chatimage.gui;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

import java.io.File;
import java.util.Objects;

public class ScreenFileChooser extends AbstractContainerScreen<MenuCiManager> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
    private  Callback callback;
    private StringListWidget stringListWidget;
    public File cDir;
    private EditBox pathName;
    private EditBox fileName;
    private StringListWidget.StringEntry oldSel;
    public ScreenFileChooser(MenuCiManager pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth=256;
        cDir=new File(".");
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    @Override
    protected void init() {
        super.init();
        try {
            cDir=cDir.getCanonicalFile();
        }catch (Exception e){
            getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
        }
        clearWidgets();
        stringListWidget=new StringListWidget(this,getXSize()-40,getGuiTop()+50,getGuiTop()+getYSize()-30);
        stringListWidget.setPosition(getGuiLeft()+10,getGuiTop()+50);
        addRenderableWidget(Button.builder(Component.literal("->"),(button)->{
            try {
                cDir=new File(pathName.getValue());
                init();
            }catch (Exception e){
                getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
            }
        }).bounds(getGuiLeft()+getXSize()-100,getGuiTop()+25,15,15).build());
        addRenderableWidget(Button.builder(Component.literal("^"),(button)->{
            try {
                cDir=new File(cDir,"..");
                init();
            }catch (Exception e){
                getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
            }
        }).bounds(getGuiLeft()+getXSize()-80,getGuiTop()+25,15,15).build());
        addRenderableWidget(stringListWidget);
        pathName = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+20,140,20,Component.literal("Path")));
        pathName.setMaxLength(10000);
        pathName.setValue(cDir.getAbsolutePath());
        addRenderableWidget(pathName);

        fileName = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+getYSize()-25,140,20,Component.literal("FileName")));
        fileName.setMaxLength(100);

        stringListWidget.add(new ScreenFileChooser.FileString(new File(cDir,"..")));
        try {
            for (File file : Objects.requireNonNull(cDir.listFiles())) {
                if (file != null) {
                    stringListWidget.add(new ScreenFileChooser.FileString(file));
                }
            }
        }catch (Exception e){
            getMinecraft().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        StringListWidget.StringEntry selected = stringListWidget.getSelected();
        if (selected != null&&!selected.equals(oldSel)) {
            oldSel=selected;
            ChatimageMod.logger.info("FileChooser triggered click on " + selected.getObj());
            File file = ((FileString) selected.getObj()).file();
            if (file.isDirectory()) {
                stringListWidget.setSelected(null);
                cDir = file;
                init();
            }else {
                fileName.setValue(file.getName());
            }
        }
    }
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(pKeyCode,pScanCode))
            return false;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private record FileString(File file){
        @Override
        public String toString() {
            var fileStr = file.getName().replace("\\","/");;
            fileStr=fileStr.replace("./","");
            if (file.isDirectory()){
                return fileStr +"/";
            }else {
                return fileStr;
            }
        }
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {

    }
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    public interface Callback{
        void call(String choose);
    }
}
