package org.eu.hanana.reimu.chatimage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public record FileTransportPayload(String op, String key, int index, byte[] payload,String extra) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FileTransportPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "ftp"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, FileTransportPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            FileTransportPayload::op,
            ByteBufCodecs.STRING_UTF8,
            FileTransportPayload::key,
            ByteBufCodecs.VAR_INT,
            FileTransportPayload::index,
            ByteBufCodecs.BYTE_ARRAY,
            FileTransportPayload::payload,
            ByteBufCodecs.STRING_UTF8,
            FileTransportPayload::extra,
            FileTransportPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}