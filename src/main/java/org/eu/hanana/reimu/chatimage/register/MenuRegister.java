package org.eu.hanana.reimu.chatimage.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.menu.EmptyMenu;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class MenuRegister {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(
            // The registry we want to use.
            // Minecraft's registries can be found in BuiltInRegistries, NeoForge's registries can be found in NeoForgeRegistries.
            // Mods may also add their own registries, refer to the individual mod's documentation or source code for where to find them.
            BuiltInRegistries.MENU,
            // Our mod id.
            ChatimageMod.MOD_ID
    );
    public static final Supplier<MenuType<EmptyMenu>> EMPTY_MENU = REGISTER.register("empty", () -> new MenuType<>(new MenuType.MenuSupplier<>() {
        @Override
        public @NotNull EmptyMenu create(int containerId, @NotNull Inventory playerInventory) {
            return new EmptyMenu(EMPTY_MENU.get(),containerId);
        }
    }, FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus b){
        REGISTER.register(b);
    }
}
