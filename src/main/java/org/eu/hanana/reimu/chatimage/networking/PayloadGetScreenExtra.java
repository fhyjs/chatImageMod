package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public class PayloadGetScreenExtra implements CustomPacketPayload {
    private final String clazz;
    private final byte[] extraData;
    private boolean setMode;

    public static final Type<PayloadGetScreenExtra> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "get_screen_extra")
    );

    public static final StreamCodec<ByteBuf, PayloadGetScreenExtra> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PayloadGetScreenExtra::getClazz,
            ByteBufCodecs.BYTE_ARRAY,
            PayloadGetScreenExtra::getExtraData,
            ByteBufCodecs.BOOL,
            PayloadGetScreenExtra::isSetMode,
            PayloadGetScreenExtra::new
    );

    private PayloadGetScreenExtra( String clazz,byte[] extraData,boolean setMode) {
        this.clazz=clazz;
        this.extraData=extraData;
        this.setMode=setMode;
    }
    public PayloadGetScreenExtra( String clazz) {
        this(clazz,new byte[0],false);
    }
    public PayloadGetScreenExtra( String clazz,byte[] extraData) {
        this(clazz,extraData,true);
    }
    public boolean isSetMode() {
        return setMode;
    }

    public String getClazz() {
        return clazz;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
