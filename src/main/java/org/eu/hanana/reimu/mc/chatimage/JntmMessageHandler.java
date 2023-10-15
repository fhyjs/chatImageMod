package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class JntmMessageHandler implements IMessageHandler<JntmMessage, IMessage> {
    private static ItemStack item1;
    private static NBTTagCompound nbtTagCompoundl =new NBTTagCompound();
    /*
    0:关闭当前GUI
    1:获取帮助手册
    2:jvav2
    3:jvav3
    4:GET:Jvav
    */
    @Override
    public IMessage onMessage(JntmMessage message, MessageContext ctx) {
        System.out.println(message.a);
        if (message.a==0) {
            ctx.getServerHandler().player.closeContainer();
            ctx.getServerHandler().player.closeScreen();
        }
        return null;
    }

}
