package org.eu.hanana.reimu.chatimage.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.HoverEvent;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.mc.hnnlib.util.ActionEventUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

public class Actions {
    public static HoverEvent.Action SHOW_IMAGE;
    public static ClickEvent.Action VIEW_IMAGE;
    public static HoverEvent.Action getShowImage(){
        if (SHOW_IMAGE==null) {
            SHOW_IMAGE= ActionEventUtil.createOrGetHoverEventAction("show_image",ShowImage.CODEC);
        }
            //SHOW_IMAGE = new HoverEvent.Action("show_image", true, ComponentSerialization.CODEC, (p_329861_, p_329862_) -> DataResult.success(p_329861_));
        return SHOW_IMAGE;
    }

    public static ClickEvent.Action getViewImage() {
        if (VIEW_IMAGE==null){
            VIEW_IMAGE=ActionEventUtil.createOrGetClickEventAction("view_image",ViewImage.CODEC);
        }
        return VIEW_IMAGE;
    }
    public record ShowImage(Component component) implements HoverEvent {
        public static MapCodec<ShowImage> CODEC = RecordCodecBuilder.mapCodec(
                p_393562_ -> p_393562_.group(ComponentSerialization.CODEC.fieldOf("value").forGetter(ShowImage::component))
                        .apply(p_393562_, ShowImage::new)
        );
        @Override
        public Action action() {
            return SHOW_IMAGE;
        }
    }
    public record ViewImage(String value) implements ClickEvent {
        public static MapCodec<ViewImage> CODEC = RecordCodecBuilder.mapCodec(
                p_393562_ -> p_393562_.group(Codec.STRING.fieldOf("value").forGetter(ViewImage::value))
                        .apply(p_393562_, ViewImage::new)
        );
               @Override
        public Action action() {
            return VIEW_IMAGE;
        }
    }
}
