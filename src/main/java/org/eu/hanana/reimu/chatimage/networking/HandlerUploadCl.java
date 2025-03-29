package org.eu.hanana.reimu.chatimage.networking;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.eu.hanana.reimu.chatimage.Util;

public class HandlerUploadCl {
    public static PayloadUpload payloadUpload;
    public static void handleData(final PayloadUpload data, final IPayloadContext context) {
        Util.reply= data.opt();
        payloadUpload=data;
    }
}
