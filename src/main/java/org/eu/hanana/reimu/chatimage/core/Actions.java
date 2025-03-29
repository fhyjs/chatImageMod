package org.eu.hanana.reimu.chatimage.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cpw.mods.modlauncher.EnumerationHelper;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.ExtraCodecs;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URI;

public class Actions {
    public static HoverEvent.Action SHOW_IMAGE;
    public static ClickEvent.Action VIEW_IMAGE;
    public static HoverEvent.Action getShowImage(){
        if (SHOW_IMAGE==null) {
            Constructor<?> c = HoverEvent.Action.class.getDeclaredConstructors()[0];
            c.setAccessible(true);
            MethodHandle h = null;
            try {
                h = MethodHandles.lookup().unreflectConstructor(c);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            int ordinal=0;
            for (HoverEvent.Action value : HoverEvent.Action.values()) {
                if (value.ordinal()>ordinal) ordinal=value.ordinal();
            }
            ordinal++;
            try {
                SHOW_IMAGE = (HoverEvent.Action) h.invokeExact("SHOW_IMAGE", ordinal,"show_image",true,ShowImage.CODEC);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
            //SHOW_IMAGE = new HoverEvent.Action("show_image", true, ComponentSerialization.CODEC, (p_329861_, p_329862_) -> DataResult.success(p_329861_));
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
                VIEW_IMAGE = (ClickEvent.Action) h.invokeExact("VIEW_IMAGE", ordinal,"view_image",true, ViewImage.CODEC);
            } catch (Throwable e) {
                ChatimageMod.logger.error("Can not init ClickAction with MethodHandle! Stop the ClickEventAction register.");
            }
        }
        return VIEW_IMAGE;
    }
    public record ShowImage(Component component) implements HoverEvent {
        public static MapCodec<ShowImage> CODEC = RecordCodecBuilder.mapCodec(
                p_393562_ -> p_393562_.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(ShowImage::component))
                        .apply(p_393562_, ShowImage::new)
        );
        @Override
        public HoverEvent.Action action() {
            return SHOW_IMAGE;
        }
    }
    public record ViewImage(String value) implements ClickEvent {
        public static MapCodec<ViewImage> CODEC = RecordCodecBuilder.mapCodec(
                p_393562_ -> p_393562_.group(Codec.STRING.fieldOf("value").forGetter(ViewImage::value))
                        .apply(p_393562_, ViewImage::new)
        );
               @Override
        public ClickEvent.Action action() {
            return VIEW_IMAGE;
        }
    }
}
