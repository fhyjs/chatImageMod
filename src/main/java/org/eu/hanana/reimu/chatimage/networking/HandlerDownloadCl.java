package org.eu.hanana.reimu.chatimage.networking;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.eu.hanana.reimu.chatimage.Util;

public class HandlerDownloadCl {
    public static PayloadDownload payloadUpload;
    public static void handleData(final PayloadDownload data, final IPayloadContext context) {
        Util.reply= data.opt();
        payloadUpload=data;
        if (data.opt().equals("DL")){
            Util.DOWNLOAD_BUFFER[data.pos()] = data.bytes();
        }
    }
}
