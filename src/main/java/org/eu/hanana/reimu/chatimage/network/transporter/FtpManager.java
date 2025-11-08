package org.eu.hanana.reimu.chatimage.network.transporter;

import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FtpManager {
    public static final Map<String, PendingData> ftpUploadPendingData = new HashMap<>();
    public static final Map<String, PendingData> ftpDownloadPendingData = new HashMap<>();
    public static final Map<String, Consumer<Tuple<String,FtpInputStream>>> ftpDownloadPendingCallback = new HashMap<>();
}
