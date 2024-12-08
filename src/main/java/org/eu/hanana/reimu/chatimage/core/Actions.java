package org.eu.hanana.reimu.chatimage.core;

import com.mojang.serialization.DataResult;
import cpw.mods.modlauncher.EnumerationHelper;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class Actions {
    public static HoverEvent.Action<Component> SHOW_IMAGE;
    public static ClickEvent.Action VIEW_IMAGE;
    public static HoverEvent.Action<Component> getShowImage(){
        if (SHOW_IMAGE==null)
            SHOW_IMAGE = new HoverEvent.Action<>(
                    "show_image", true, ComponentSerialization.CODEC, (p_329861_, p_329862_) -> DataResult.success(p_329861_)
            );
        return SHOW_IMAGE;
    }

    public static ClickEvent.Action getViewImage() {
        if (VIEW_IMAGE==null){
            try {
            /*
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            ClickEvent.Action action = (ClickEvent.Action) unsafe.allocateInstance(ClickEvent.Action.class);
            Field allowFromServer = ClickEvent.Action.class.getDeclaredField("allowFromServer");
            allowFromServer.setAccessible(true);
            allowFromServer.set(action,true);
            ClickEvent.Action.class.getDeclaredField("");
             */
                Constructor<?> c = ClickEvent.Action.class.getDeclaredConstructors()[0];
                c.setAccessible(true);
                MethodHandle h = MethodHandles.lookup().unreflectConstructor(c);
                int ordinal=0;
                for (ClickEvent.Action value : ClickEvent.Action.values()) {
                    if (value.ordinal()>ordinal) ordinal=value.ordinal();
                }
                ordinal++;
                VIEW_IMAGE = (ClickEvent.Action) h.invokeExact("VIEW_IMAGE", ordinal,"view_image",true);
            } catch (Throwable e) {
                ChatimageMod.logger.error("Can not init ClickAction with MethodHandle! Stop the ClickEventAction register.");
            }
        }
        return VIEW_IMAGE;
    }
}
