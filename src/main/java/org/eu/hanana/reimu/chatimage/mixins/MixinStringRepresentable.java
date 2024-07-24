package org.eu.hanana.reimu.chatimage.mixins;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.StringRepresentable;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static net.minecraft.util.StringRepresentable.createNameLookup;
import static net.minecraft.util.StringRepresentable.fromEnumWithMapping;

@Mixin(StringRepresentable.class)
public interface MixinStringRepresentable{
    @Inject(method = "fromValues",at = @At("HEAD"),cancellable = true)
    private static void fromValues(Supplier<? extends StringRepresentable[]> pValuesSupplier, CallbackInfoReturnable<Codec<? extends StringRepresentable>> callbackInfo){
        if (pValuesSupplier.get() instanceof  HoverEvent.Action[]){
            ChatimageMod.logger.info("Patching HoverEvent...");
            List<HoverEvent.Action<?>> atL = new java.util.ArrayList<>(List.of((HoverEvent.Action<?>[]) pValuesSupplier.get()));
            atL.add(Actions.getShowImage());
            HoverEvent.Action<?>[] at = atL.toArray(new HoverEvent.Action[0]);
            Function<String, HoverEvent.Action<?>> function = createNameLookup(at, p_304333_ -> p_304333_);
            ToIntFunction<HoverEvent.Action<?>> tointfunction = Util.createIndexLookup(Arrays.asList(at));
            StringRepresentable.StringRepresentableCodec<HoverEvent.Action<?>> actionStringRepresentableCodec = new StringRepresentable.StringRepresentableCodec<>(at, function, tointfunction);
            System.out.println(Arrays.toString(at));
            callbackInfo.setReturnValue(actionStringRepresentableCodec);
            callbackInfo.cancel();
        }

    }
    @Inject(method = "fromEnum",at = @At("HEAD"),cancellable = true)
    private static void fromEnum(Supplier<? extends Enum<?>[] > pValuesSupplier, CallbackInfoReturnable<StringRepresentable.EnumCodec<?>> callbackInfo){
        if (pValuesSupplier.get() instanceof ClickEvent.Action[] actions){
            List<ClickEvent.Action> pL= new ArrayList<>(List.of(actions));
            pL.add(Actions.getViewImage());
            callbackInfo.setReturnValue(fromEnumWithMapping(() -> pL.toArray(new ClickEvent.Action[0]), p_304817_ -> p_304817_));
            callbackInfo.cancel();
        }
    }
}
