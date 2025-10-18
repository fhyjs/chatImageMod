package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.network.chat.ClickEvent;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClickEvent.Action.class)
public class MixinClickEvent {
    @Unique
    private static boolean chatimage$internal;
    @Unique
    private static ClickEvent.Action[] chatimage$cache;
    @Inject(method = {"values"},at=@At("RETURN"), cancellable = true)
    private static void values(CallbackInfoReturnable<ClickEvent.Action[]> cir){
        if (chatimage$internal) return;
        if (chatimage$cache!=null) {
            cir.setReturnValue(chatimage$cache);
            return;
        }
        chatimage$internal=true;
        ArrayList<ClickEvent.Action> actionsHoverEvent = new ArrayList<>(List.of(cir.getReturnValue()));
        actionsHoverEvent.add(Actions.getViewImage());
        chatimage$cache=actionsHoverEvent.toArray(new ClickEvent.Action[0]);
        cir.setReturnValue(chatimage$cache);
        for (ClickEvent.Action action : actionsHoverEvent) {
            ChatimageMod.logger.info(action);
        }
        chatimage$internal=false;
    }
}
