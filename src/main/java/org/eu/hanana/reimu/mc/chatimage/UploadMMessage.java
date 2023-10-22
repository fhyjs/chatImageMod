package org.eu.hanana.reimu.mc.chatimage;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class UploadMMessage implements IMessage {
    public int a;
    public String o;
    public UploadMMessage() {}
    public UploadMMessage(int a,String o) {
        this.a = a;
        this.o=o;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(a);
        buf.writeInt(o.getBytes().length);
        buf.writeBytes(o.getBytes());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        a=buf.readInt();
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        o=new String(data);
    }
}
