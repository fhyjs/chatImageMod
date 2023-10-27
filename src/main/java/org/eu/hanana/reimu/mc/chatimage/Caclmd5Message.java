package org.eu.hanana.reimu.mc.chatimage;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class Caclmd5Message implements IMessage {
    public String md5;
    public Caclmd5Message() {}
    public Caclmd5Message(String md5) {
        this.md5=md5;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(md5.getBytes().length);
        buf.writeBytes(md5.getBytes());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        byte[] data = new byte[buf.readInt()];
        buf.readBytes(data);
        md5=new String(data);
    }
}
