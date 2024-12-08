package org.eu.hanana.reimu.chatimage.networking;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.eu.hanana.reimu.chatimage.ChatimageMod;
import org.eu.hanana.reimu.chatimage.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HandlerDownloadSv {
    public final static Map<Player, List<byte[]>> UPLOAD_BUFFER = new HashMap<>();
    public static void handleData(final PayloadDownload data, final IPayloadContext context) {
        String opt = data.opt();
        if (opt.equals("START")) {
            File file = new File("./chatimage", new String(data.bytes()));
            byte[] bytes;
            try {
                bytes = Util.readFileToByteArray(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            List<byte[]> split = Util.splitByteArray(bytes, 500);
            UPLOAD_BUFFER.put(context.player(),split);
            context.reply(new PayloadDownload("R-START", split.size(), new byte[0]));
        }
        if (opt.equals("DL")){
            List<byte[]> bytes1 = UPLOAD_BUFFER.get(context.player());
            for (int i = 0; i < bytes1.size(); i++) {
                context.reply(new PayloadDownload("DL",i,bytes1.get(i)));
            }
        }
    }
}
