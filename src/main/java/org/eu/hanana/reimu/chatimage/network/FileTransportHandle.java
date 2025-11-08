package org.eu.hanana.reimu.chatimage.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.eu.hanana.reimu.chatimage.network.transporter.FtpInputStream;
import org.eu.hanana.reimu.chatimage.network.transporter.FtpManager;
import org.eu.hanana.reimu.chatimage.network.transporter.PendingData;
import org.jetbrains.annotations.NotNull;

public class FileTransportHandle implements IPayloadHandler<FileTransportPayload> {
    @Override
    public void handle(@NotNull FileTransportPayload payload, @NotNull IPayloadContext context) {
        if (payload.op().equals(FtpInputStream.TR_START)) {
            if (!FtpManager.ftpDownloadPendingData.containsKey(payload.key())){
                PendingData pendingData = new PendingData();
                pendingData.key= payload.key();
                pendingData.send=false;
                pendingData.startTime=System.nanoTime();
                pendingData.ftpInputStream=new FtpInputStream(pendingData);
                if (!context.player().isLocalPlayer()){
                    pendingData.ftpInputStream.player= (net.minecraft.server.level.ServerPlayer) context.player();
                }
                FtpManager.ftpDownloadPendingData.put(payload.key(), pendingData);
            }
        }
        try {
            if (payload.op().startsWith(FtpInputStream.TR)) {
                FtpManager.ftpDownloadPendingData.get(payload.key()).ftpInputStream.receivedPacket(payload, context);
            }else if (payload.op().startsWith(FtpInputStream.RT)) {
                FtpManager.ftpUploadPendingData.get(payload.key()).ftpInputStream.receivedPacket(payload, context);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
