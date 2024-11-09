package org.eu.hanana.reimu.chatimage.mixins;

import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.eu.hanana.reimu.chatimage.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Inject(method = {"signBook"},at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    public void signBook(FilteredText title, List<FilteredText> pages, int index, CallbackInfo ci, ItemStack itemstack,ItemStack itemstack1) {
        Util.signBook((ServerGamePacketListenerImpl)((Object) this),title, pages, index, ci,itemstack1);
    }
}
