package org.eu.hanana.reimu.chatimage.core;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import org.eu.hanana.reimu.mc.hnnlib.api.IActionEventRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActionEventRegisterImpl implements IActionEventRegister {
    @Override
    public @Nullable List<HoverEvent.Action> getHoverActions() {
        return List.of(Actions.getShowImage());
    }

    @Override
    public @Nullable List<ClickEvent.Action> getClickActions() {
        return List.of(Actions.getViewImage());
    }
}
