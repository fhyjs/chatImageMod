package org.eu.hanana.reimu.chatimage.networking;

import com.google.gson.Gson;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;
import org.eu.hanana.reimu.chatimage.client.ServConfig;
import org.eu.hanana.reimu.chatimage.config.ChatImageConfig;

import java.lang.reflect.Field;

public class HandlerGetModConfigure {
    public static void handleData(final PayloadGetModConfig data, final IPayloadContext context) {
        var isLocal = context.player().isLocalPlayer();
        if (isLocal){
            try {
                Field field = ServConfig.class.getField(data.name());
                if (!field.getType().getName().equals(data.fType())) {
                    throw new IllegalArgumentException(field.getType().getName()+" cannot get as "+data.fType());
                }
                field.set(null,new Gson().fromJson(data.val(), Class.forName(data.fType())));
                ChatimageMod.logger.info("Received server configure {} with value {}.",data.name(),data.val());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            try {
                Field field = ChatImageConfig.class.getField(data.name());
                if (!field.getType().getName().equals(data.fType())) {
                    throw new IllegalArgumentException(field.getType().getName()+" cannot get as "+data.fType());
                }
                context.reply(new PayloadGetModConfig(data.name(), data.fType(), new Gson().toJson(field.get(null))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
