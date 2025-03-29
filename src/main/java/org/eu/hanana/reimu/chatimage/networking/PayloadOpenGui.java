package org.eu.hanana.reimu.chatimage.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eu.hanana.reimu.chatimage.ChatimageMod;

public class PayloadOpenGui implements CustomPacketPayload {
    private final int player_id;
    private final String gui;
    private final byte[] extraData;

    public static final CustomPacketPayload.Type<PayloadOpenGui> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ChatimageMod.MOD_ID, "open_gui")
    );

    public static final StreamCodec<ByteBuf, PayloadOpenGui> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            PayloadOpenGui::getPlayerId,
            ByteBufCodecs.STRING_UTF8,
            PayloadOpenGui::getGui,
            ByteBufCodecs.BYTE_ARRAY,
            PayloadOpenGui::getExtraData,
            PayloadOpenGui::new
    );

    public PayloadOpenGui(int player_id, String gui, byte[] extraData) {
        this.player_id = player_id;
        this.gui = gui;
        this.extraData = extraData;
    }
    public PayloadOpenGui(int player_id, String gui) {
        this(player_id,gui,new byte[0]);
    }
    public int getPlayerId() {
        return player_id;
    }

    public String getGui() {
        return gui;
    }

    public byte[] getExtraData() {
        return extraData;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
