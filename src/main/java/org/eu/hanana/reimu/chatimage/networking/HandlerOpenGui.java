package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HandlerOpenGui {
    public static void handleData(final PayloadOpenGui data, final IPayloadContext context) {
        if (!context.player().level().isClientSide()) {
            Entity entity = context.player().level().getEntity(data.getPlayerId());
            if (entity instanceof Player player){
                player.openMenu(new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> {
                    MenuType<?> menuType = BuiltInRegistries.MENU.get(ResourceLocation.parse(data.getGui()));
                    final var extra = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data.getExtraData()),player.registryAccess(),context.listener().getConnectionType());
                    return menuType.create(pContainerId,pPlayerInventory,extra);
                }, Component.translatable("attribute.modifier.equals.1")));
            }
        }
    }
}
