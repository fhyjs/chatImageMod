package org.eu.hanana.reimu.chatimage.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLEnvironment;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.screen.widget.TextListWidget;

import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FileScreen extends Screen {
    public static final SystemToast.SystemToastId CHATIMAGE_TOAST_ID = new SystemToast.SystemToastId(2000);
    public static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/demo_background.png");
    public Runnable afterInit= () -> {};
    public Consumer<String> callback=s -> {};
    private Screen parent;
    public EditBox filenameTextField;
    public EditBox pathTextField;
    private TextListWidget selectList;
    private final Map<String,ResourceLocation> icons = new HashMap<>();
    private Thread iconThread;

    public FileScreen() {
        super(Component.translatable("gui.ci.file"));
    }
    @Override
    protected void init() {
        var x = this.width / 2;
        var y = this.height / 2;

        addRenderableWidget(Button.builder(Component.literal("X"),button -> {
            onClose();
        }).bounds(x+103,y-77,14,14).build());
        addRenderableWidget(Button.builder(Component.literal("->"),button -> {
            refresh();
        }).bounds(x+50,y-65,20,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ok"),button -> {
            selected();
        }).bounds(x+90,y+58,28,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"),button -> {
            onClose();
        }).bounds(x+55,y+58,28,20).build());

        addRenderableWidget(filenameTextField=new EditBox(font,x-100,y+58,130,20,Component.empty()));
        addRenderableWidget(pathTextField=new EditBox(font,x-80,y-65,130,20,Component.empty()));

        addRenderableWidget(selectList=new TextListWidget(this,220,y-40,y+55){
            @Override
            public void onSelect(TextEntry entry) {
                super.onSelect(entry);
                if (filenameTextField.getValue().equals(entry.text)){
                    doubleClicked(entry.text);
                }
                FileScreen.this.filenameTextField.setValue(entry.text);
            }
        });
        selectList.setX(x-110);

        pathTextField.setMaxLength(Integer.MAX_VALUE);
        filenameTextField.setMaxLength(Integer.MAX_VALUE);
        pathTextField.setValue(new File(".").getAbsolutePath());
        refresh();
        afterInit.run();
    }

    @Override
    public void onFilesDrop(List<Path> packs) {
        super.onFilesDrop(packs);
        Path first = packs.getFirst();
        File file = first.toFile();
        String parent1 = file.getParent();
        this.pathTextField.setValue(parent1);
        filenameTextField.setValue(file.getName());
        selected();
    }

    private void selected() {
        if (isGettingIcons()) return;
        var file = new File(new File(pathTextField.getValue()),filenameTextField.getValue());
        if (file.isDirectory()){
            try {
                pathTextField.setValue(file.getCanonicalFile().getAbsolutePath());
            } catch (IOException e) {
                pathTextField.setValue(file.getAbsolutePath());
            }
            refresh();
            getMinecraft().schedule(()->filenameTextField.setValue(""));
        }else {
            callback.accept(file.getAbsolutePath());
            onClose();
        }
    }

    private void doubleClicked(String text) {
        selected();
    }

    public void refresh() {
        refresh(pathTextField.getValue());
    }

    public void refresh(String value) {
        if (iconThread!=null&&iconThread.isAlive()) return;
        File file = new File(value);
        if (!file.exists()|| !file.isDirectory()) {
            getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.WORLD_BACKUP,title,Component.translatable("msg.ci.file_not_found")));
            return;
        }
        selectList.clearEntries();
        pathTextField.setValue(file.getAbsolutePath());

        iconThread=new Thread(()->{
            File[] files = file.listFiles();
            if (files==null) files=new File[0];
            Arrays.sort(files, (a, b) -> {
                // 文件夹优先
                if (a.isDirectory() && !b.isDirectory()) return -1;
                if (!a.isDirectory() && b.isDirectory()) return 1;
                // 同类则按名称字母序（不区分大小写）
                return a.getName().compareToIgnoreCase(b.getName());
            });
            var tls = new ArrayList<TextListWidget.TextEntry>();
            tls.add(selectList.newEntry("../"));
            for (File s : files) {
                tls.add(selectList.newEntry(s.getName()+(s.isDirectory()?"/":"")));
            }
            File[] finalFiles = files;

            for (File file1 : finalFiles) {
                var ext = Util.getFileExtension(file1);
                if (icons.containsKey(ext)) continue;
                BufferedImage bufferedImage = Util.iconToImage(FileSystemView.getFileSystemView().getSystemIcon(file1));
                ResourceLocation resourceLocation = null;
                try {
                    resourceLocation = Util.uploadBufferedImage(bufferedImage, ext);
                    icons.put(ext,resourceLocation);
                } catch (IOException ignored) { }
                for (TextListWidget.TextEntry tl : tls) {
                    tl.icon= icons.get(Util.getFileExtension(new File(file, tl.text)));
                }
            }
            for (TextListWidget.TextEntry tl : tls) {
                tl.icon= icons.get(Util.getFileExtension(new File(file, tl.text)));
            }

            tls.forEach(selectList::addEntry);
        });

        selectList.setScrollAmount(0);
        iconThread.start();
    }
    public boolean isGettingIcons(){
        return iconThread!=null&&iconThread.isAlive();
    }
    @Override
    public void onClose() {
        if (iconThread!=null&&iconThread.isAlive()) return;
        for (ResourceLocation value : icons.values()) {
            if (FMLEnvironment.dist.isClient()){
                AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(value);
                if (texture instanceof DynamicTexture dynamicTexture){
                    dynamicTexture.close();
                }
            }
        }
        if (parent!=null){
            getMinecraft().screen=(parent);
            return;
        }
        super.onClose();
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

        p_281247_.drawString(this.font, title, i, j, -14737633, false);
        if (isGettingIcons())
            p_281247_.drawString(this.font, Component.translatable("gui.ci.working"), i+60, j, 0xffff1d1d, false);
        else
            p_281247_.drawString(this.font, Component.translatable("gui.ci.drag_tip"), i+60, j, 0xff1ffc1d, false);

        p_281247_.drawString(this.font, Component.translatable("gui.ci.path"), i+3, j+15, -14737633, false);

    }

    public FileScreen setParent(Screen screen) {
        this.parent=screen;
        return this;
    }
}
