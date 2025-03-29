package org.eu.hanana.reimu.chatimage.gui;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.fml.i18n.I18nManager;
import net.neoforged.neoforge.network.PacketDistributor;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.client.ServConfig;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.chatimage.networking.PayloadGetModConfig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class ScreenCiManager extends AbstractContainerScreen<MenuCiManager> implements IHasData{
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
    public EditBox textToSend;
    public EditBox url;
    public EditBox w;
    public EditBox h;
    public EditBox info;
    public Screen oldScreen;
    public Method sendMethod;
    public String oldScreenName;
    public Checkbox rawBtn;
    public Button sendBtn;

    public ScreenCiManager(MenuCiManager pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        imageWidth=256;
        this.oldScreen=Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();
        PacketDistributor.sendToServer(new PayloadGetModConfig("maxFileSize","java.lang.Integer",""));
        addRenderableWidget(Button.builder(Component.literal("X"),(button)->{
            ScreenCiManager.this.onClose();
        }).bounds(getGuiLeft()+getXSize()-30,getGuiTop()+7,15,15).build());
        addRenderableWidget(sendBtn=Button.builder(Component.translatable("gui.ci.send"),(button)->{
            try {
                sendMethod.invoke(oldScreen,textToSend.getValue());
            } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                this.defaultSend(textToSend.getValue());
            }
        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-30,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.clear"),(button)->{
            try {
                ChatImage.clearCache();
                getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("SUCCESS/完成"),Component.literal("OK")));
            } catch (Throwable e) {
                Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
            }

        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-60,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.upload"),(button)->{
            ScreenFileChooser fileChooser = new ScreenFileChooser(menu, getMinecraft().player.getInventory(), Component.literal("FileChooser"));
            fileChooser.setParent(this);
            fileChooser.setMaxFileSize(ServConfig.maxFileSize);
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
                        getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("SUCCESS/完成"),Component.literal("OK")));
                    });
                });
                thread.setUncaughtExceptionHandler((t,e)->{
                    Minecraft.getInstance().execute(()->{
                        Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"), Component.literal(e.toString())));
                    });
                });
                getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("WORKING/处理中"),Component.literal("DO NOT CLOSE THIS WINDOW/不要关闭本窗口")));
                thread.start();
            });


        }).bounds(getGuiLeft()+getXSize()-90,getGuiTop()+getYSize()-100,40,20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.ci.add"),(button)->{
            try {
                ChatImage chatImage = ChatImage.getChatImage(new ChatImage.ChatImageData(url.getValue(), Integer.parseInt(w.getValue()), Integer.parseInt(h.getValue()), info.getValue()).toCiCode());
                ChatImage.ChatImageData chatImageData = new ChatImage.ChatImageData(chatImage);
                if (rawBtn.selected()) {
                    button.active=false;
                    rawBtn.active=false;
                    getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("PROGRESSING/处理中"),Component.empty()));
                    new Thread(()->{
                        try {
                            chatImageData.setImageSizeRaw();
                            w.setValue(String.valueOf(chatImageData.w));
                            h.setValue(String.valueOf(chatImageData.h));
                            rawBtn.onPress();
                            getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("SUCCESS/完成"),Component.empty()));
                            getMinecraft().schedule(()->button.mouseClicked(button.getX(),button.getY(),0));
                        } catch (IOException e) {
                            ChatimageMod.logger.warn(e);
                            getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("ERROR/错误"),Component.literal(e.toString())));
                        }finally {
                            button.active=true;
                            rawBtn.active=true;
                        }
                    }).start();
                    return;
                }
                textToSend.setValue(textToSend.getValue()+chatImageData);

                url.setValue("");
                w.setValue("");
                h.setValue("");
                info.setValue("");
            }catch (Throwable e){
                ChatimageMod.logger.warn(e);
                getMinecraft().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE,Component.literal("ERROR/错误"),Component.literal(e.toString())));
            }
        }).bounds(getGuiLeft()+100,getGuiTop()+112,20,20).build());
        rawBtn = addRenderableWidget(Checkbox.builder(Component.literal("§2Raw"),font).onValueChange((checkbox, value) -> {
            this.w.setEditable(!value);
            this.h.setEditable(!value);
            if (value) {
                w.setValue("150");
                h.setValue("150");
            }
        }).pos(getGuiLeft()+50,getGuiTop()+70).build());
        textToSend = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+30,100,20,Component.translatable("gui.ci.gen")));
        url = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+90,100,20,Component.literal("URL")));
        w = addRenderableWidget(new EditBox(this.font,getGuiLeft()+20,getGuiTop()+112,30,20,Component.translatable("gui.ci.width")));
        h = addRenderableWidget(new EditBox(this.font,getGuiLeft()+60,getGuiTop()+112,30,20,Component.translatable("gui.ci.height")));
        info = addRenderableWidget(new EditBox(this.font,getGuiLeft()+10,getGuiTop()+142,100,20,Component.translatable("gui.ci.info")));
        url.setMaxLength(114514);
        textToSend.setMaxLength(114514);
    }
    @SuppressWarnings("unused")
    public void defaultSend(String s){
        this.getMinecraft().gui.getChat().addRecentChat(s);
        Objects.requireNonNull(this.getMinecraft().player).connection.sendChat(s);
        ScreenCiManager.this.onClose();
    }
    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(pKeyCode,pScanCode))
            return false;
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(RenderType::guiTextured,TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight,256,256);
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
    @Override
    public void setData(byte[] data) {
        ExtraData extraData = new Gson().fromJson(new String(data),ExtraData.class);
        if (extraData.action().equals("send_method")) {
            for (Method method : oldScreen.getClass().getMethods()) {
                if (method.getName().equals(extraData.value())){
                    this.sendMethod=method;
                    this.oldScreenName=extraData.extra();
                    break;
                }
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (oldScreen!=null){
            if (oldScreen.getClass().getName().equals(oldScreenName)){
                getMinecraft().setScreen(oldScreen);
            }
        }
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
