package org.eu.hanana.reimu.mc.chatimage;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;
import java.util.List;

public class Caclmd5MessageHandler implements IMessageHandler<Caclmd5Message, IMessage> {

    @Override
    public IMessage onMessage(Caclmd5Message message, MessageContext ctx) {
        if (ctx.side.isServer()){
            List<File> files = Utils.traverseFolder(new File("chatimages/"));
            for (File file : files) {
                if (Utils.calcMD5(file).equals(message.md5))
                    return new Caclmd5Message(file.getAbsolutePath());
            }
            return new Caclmd5Message("$EMPTY$");
        }else {
            if (!message.md5.equals("$EMPTY$")){
                Utils.SvReply=message.md5;
            }
            Utils.OK=true;
            return null;
        }
    }

}
