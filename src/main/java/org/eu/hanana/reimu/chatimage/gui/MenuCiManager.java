package org.eu.hanana.reimu.chatimage.gui;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;

public class MenuCiManager extends AbstractContainerMenu implements IHasData{
    public String data;
    public Player player;
    public MenuCiManager(int containerId, Inventory playerInv) {
        this(ChatimageMod.CI_MANAGER_MENU.get(), containerId);
        this.player=playerInv.player;
        if (player.level().isClientSide()) {
            sync();
        }
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
        this.data=new String(data);
        ExtraData extraData = new Gson().fromJson(this.data,ExtraData.class);
        if (extraData!=null) {
            if (extraData.action().equals("open_gui")) {
                if (player.level().isClientSide()) {
                    Object newScreen = null;
                    try {
                        Class<?> screenClass = Class.forName(extraData.value());
                        Constructor<?> declaredConstructor = screenClass.getDeclaredConstructor(this.getClass(), player.getInventory().getClass(), Component.class);
                        newScreen = declaredConstructor.newInstance(this, player.getInventory(), Component.literal("New Screen"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Minecraft.getInstance().setScreen((Screen) newScreen);
                }
            }
        }
    }

    @Override
    public byte[] getData() {
        return data.getBytes(StandardCharsets.UTF_8);
    }
}
