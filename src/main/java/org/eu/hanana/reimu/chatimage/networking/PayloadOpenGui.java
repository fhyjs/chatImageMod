package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public record PayloadOpenGui(int player_id, String gui) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PayloadOpenGui> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "open_gui"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // 'name' will be encoded and decoded as a string
    // 'age' will be encoded and decoded as an integer
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, PayloadOpenGui> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PayloadOpenGui::player_id,
            ByteBufCodecs.STRING_UTF8,
            PayloadOpenGui::gui,
            PayloadOpenGui::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
