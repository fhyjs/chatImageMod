package org.eu.hanana.reimu.chatimage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public record FileRequestPayload(String name,String key) implements CustomPacketPayload {

    public static final Type<FileRequestPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ChatimageMod.MOD_ID, "frp"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, FileRequestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            FileRequestPayload::name,
            ByteBufCodecs.STRING_UTF8,
            FileRequestPayload::key,
            FileRequestPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}