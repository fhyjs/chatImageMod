package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.network.chat.HoverEvent;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(HoverEvent.Action.class)
public class MixinHoverEvent {
    @Unique
    private static boolean chatimage$internal;
    @Unique
    private static HoverEvent.Action[] chatimage$cache;
    @Inject(method = {"values"},at=@At("RETURN"), cancellable = true)
    private static void values(CallbackInfoReturnable<HoverEvent.Action[]> cir){
        if (chatimage$internal) return;
        if (chatimage$cache!=null) {
            cir.setReturnValue(chatimage$cache);
            return;
        }
        chatimage$internal=true;
        ArrayList<HoverEvent.Action> actionsHoverEvent = new ArrayList<>(List.of(cir.getReturnValue()));
        actionsHoverEvent.add(Actions.getShowImage());
        chatimage$cache=actionsHoverEvent.toArray(new HoverEvent.Action[0]);
        cir.setReturnValue(chatimage$cache);
        for (HoverEvent.Action action : actionsHoverEvent) {
            ChatimageMod.logger.info(action);
        }
        chatimage$internal=false;
    }
}
