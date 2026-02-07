package org.eu.hanana.reimu.chatimage;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClientUtil {
    public static Identifier uploadBufferedImage(BufferedImage image, String id) throws IOException {
        // 1. 将 BufferedImage 写入字节流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] bytes = baos.toByteArray();

        // 2. 分配堆外内存
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        buffer.put(bytes);
        buffer.flip(); // 准备读取

        NativeImage nativeImage;
        try {
            // 3. 从内存中读取 PNG → NativeImage
            nativeImage = NativeImage.read(buffer);
        } catch (IOException e) {
            throw new IOException("无法读取NativeImage", e);
        } finally {
            MemoryUtil.memFree(buffer);
        }

        // 4. 创建资源位置
        Identifier location = Identifier.fromNamespaceAndPath("chatimagemod", "dynamic/icon/" + id);

        // 5. 必须在主线程执行注册
        AtomicBoolean done = new AtomicBoolean(false);
        AtomicReference<Throwable> error = new AtomicReference<>(null);

        Minecraft.getInstance().schedule(() -> {
            try {
                DynamicTexture texture = new DynamicTexture(null,nativeImage);
                Minecraft.getInstance().getTextureManager().register(location, texture);
            } catch (Throwable t) {
                error.set(t);
            } finally {
                done.set(true);
            }
        });

        // （可选）等待完成
        while (!done.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
        }

        if (error.get() != null) {
            throw new IOException("注册动态纹理失败", error.get());
        }

        return location;
    }
}
