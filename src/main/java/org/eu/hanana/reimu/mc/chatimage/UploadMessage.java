package org.eu.hanana.reimu.mc.chatimage;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UploadMessage implements IMessage {
    public byte[] a;
    public int i;
    public UploadMessage() {}
    public UploadMessage(byte[] a,int i) {
        this.a = a;
        this.i = i;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(a.length);
        buf.writeInt(i);
        buf.writeBytes(a);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        a=new byte[buf.readInt()];
        i=buf.readInt();
        buf.readBytes(a);
    }
}
