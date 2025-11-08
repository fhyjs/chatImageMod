package org.eu.hanana.reimu.chatimage.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import org.eu.hanana.reimu.chatimage.network.transporter.FtpInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileRequestHandle implements IPayloadHandler<FileRequestPayload> {
    @Override
    public void handle(@NotNull FileRequestPayload payload, @NotNull IPayloadContext context) {
        FtpInputStream ftpInputStream = null;
        try {
            ftpInputStream = new FtpInputStream(new FileInputStream(new File("chatimage",payload.name())).readAllBytes(), (ServerPlayer) context.player(),payload.key());
            new Thread(ftpInputStream).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
