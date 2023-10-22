package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class UploadMessageHandler implements IMessageHandler<UploadMessage, IMessage> {

    @Override
    public IMessage onMessage(UploadMessage message, MessageContext ctx) {
        if (ctx.side.isServer()){
            UploadManager.UpLoads.get(ctx.getServerHandler().player.getEntityId()).put(message.i, message.a);
            return new UploadMMessage(0,"ok");
        }
        return null;
    }

}
