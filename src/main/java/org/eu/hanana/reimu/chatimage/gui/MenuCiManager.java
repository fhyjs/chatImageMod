package org.eu.hanana.reimu.chatimage.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.jetbrains.annotations.Nullable;

public class MenuCiManager extends AbstractContainerMenu implements IHasData{
    public byte[] data;

    public MenuCiManager(int containerId, Inventory playerInv) {
        this(ChatimageMod.CI_MANAGER_MENU.get(), containerId);
        // ...
    }

    protected MenuCiManager(@Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    public void setData(byte[] data) {
        this.data=data;
    }
}
