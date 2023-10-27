package org.eu.hanana.reimu.mc.chatimage;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

public class DownloadMessageHandler implements IMessageHandler<DownloadMessage, IMessage> {

    @Override
    public IMessage onMessage(DownloadMessage message, MessageContext ctx) {
        if (ctx.side.isServer()){
            if (new File("chatimages/"+message.i).exists()){
                byte[] b = Utils.ReadFile(new File("chatimages/"+message.i));
                return new DownloadMessage(b,1);
            }

            return new DownloadMessage(new byte[0],0);
        }else {
            if (message.i==1){
                Utils.SvReply=message.a;
            }
            Utils.OK=true;
        }
        return null;
    }

}
