package org.eu.hanana.reimu.chatimage.core;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.BlockableEventLoop;
import net.neoforged.fml.loading.FMLEnvironment;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.eu.hanana.reimu.chatimage.core.ChatImage.BufferedTexture;

public class ClientReflect {
    public static void sendToast(Component title,Component text){
        Minecraft.getInstance().getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, title,text));
    }
    public static void closeTexture(ResourceLocation texId,String texid){
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(texId);
        if (texture instanceof DynamicTexture dynamicTexture) dynamicTexture.close();
        BufferedTexture.remove(texid);
    }
    public static void clearCache() {
        for (ResourceLocation value : BufferedTexture.values()) {
            if (FMLEnvironment.dist.isClient()){
                AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(value);
                if (texture instanceof DynamicTexture dynamicTexture){
                    dynamicTexture.close();
                }
            }
        }
    }
    public static void registerDynamicTexture(ResourceLocation name ,NativeImage nativeImage){
        Minecraft.getInstance().getTextureManager().register(name, new DynamicTexture(null, nativeImage));
    }
    public static BlockableEventLoop<Runnable> getClientBlockableEventLoop(){
        return Minecraft.getInstance();
    }
}
