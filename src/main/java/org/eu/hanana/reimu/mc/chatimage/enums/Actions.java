package org.eu.hanana.reimu.mc.chatimage.enums;

import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.util.EnumHelper;

public class Actions {
    public static HoverEvent.Action SHOW_IMAGE = EnumHelper.addEnum(HoverEvent.Action.class,"SHOW_IMAGE",new Class[]{String.class,boolean.class},"show_image",true);
    public static ClickEvent.Action VIEW_IMAGE = EnumHelper.addEnum(ClickEvent.Action.class,"VIEW_IMAGE",new Class[]{String.class,boolean.class},"view_image",true);
}
