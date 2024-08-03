package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.gui.IHasData;

public class HandlerGetScreenExtra {
    public static void handleData(final PayloadGetScreenExtra data, final IPayloadContext context) {
        Player player = context.player();
        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu.getClass().getName().equals(data.getClazz())){
            if (containerMenu instanceof IHasData iHasData){
                if (data.isSetMode()){
                    iHasData.setData(data.getExtraData());
                }else {
                    PayloadGetScreenExtra payloadGetScreenExtra = new PayloadGetScreenExtra(data.getClazz(), iHasData.getData());
                    context.reply(payloadGetScreenExtra);
                }
            }else {
                ChatimageMod.logger.warn("Container Menu doesn't has data.Is the Container changed?");
            }
        }else {
            ChatimageMod.logger.warn("Container Menu doesn't match.Is the Container changed?");
        }
    }
}
