package org.eu.hanana.reimu.chatimage.gui;

import net.neoforged.neoforge.network.PacketDistributor;
import org.eu.hanana.reimu.chatimage.networking.PayloadGetScreenExtra;

public interface IHasData {
    void setData(byte[] data);
    byte[] getData();
    default void sync(){
        PacketDistributor.sendToServer(new PayloadGetScreenExtra(this.getClass().getName()));
    }
}
