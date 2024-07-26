package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public record PayloadDownload(String opt, int pos, byte[] bytes) implements CustomPacketPayload {

    public static final Type<PayloadDownload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "download"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, PayloadDownload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            PayloadDownload::opt,
            ByteBufCodecs.VAR_INT,
            PayloadDownload::pos,
            ByteBufCodecs.BYTE_ARRAY,
            PayloadDownload::bytes,
            PayloadDownload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
