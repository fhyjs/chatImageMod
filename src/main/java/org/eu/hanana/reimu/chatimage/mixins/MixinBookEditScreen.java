package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.gui.ExtraData;
import org.eu.hanana.reimu.chatimage.gui.ScreenCiManager;
import org.eu.hanana.reimu.chatimage.networking.PayloadOpenGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen extends Screen {
    @Shadow @Final private TextFieldHelper pageEdit;
    private static final Logger log = LogManager.getLogger(MixinBookEditScreen.class);

    protected MixinBookEditScreen(Component title) {
        super(title);
    }

    @Inject(method = {"init"},at=@At("HEAD"))
    public void init(CallbackInfo ci){
        var chatImage$parseBtn=Button.builder(Component.translatable("gui.ci.parse_helper"),(pButton -> {
            PacketDistributor.sendToServer(new PayloadOpenGui(this.getMinecraft().player.getId(),"chatimage:cim_menu",
                    new ExtraData("send_data", new ExtraData("send_method", "ci$appendText",BookEditScreen.class.getName()).toString(),"not_used").toString().getBytes()
            ));
        })).bounds(10,10,50,25).build();
        addRenderableWidget(chatImage$parseBtn);
    }
    @SuppressWarnings("unused")
    @Unique
    public void ci$appendText(String s){
        log.info("Add \"{}\" to book.",s);
        getMinecraft().screen.onClose();
        pageEdit.insertText(s);
    }
}
