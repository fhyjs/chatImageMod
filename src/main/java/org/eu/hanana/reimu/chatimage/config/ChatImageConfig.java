package org.eu.hanana.reimu.chatimage.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatImageConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue REMOVE_UPLOADS = BUILDER
            .comment("Delete all uploaded image when server restart.")
            .translation("cfg.ci.remove_all")
            .define("remove_uploads", true);

    private static final ModConfigSpec.IntValue MAX_FILE_SIZE = BUILDER
            .comment("The max file size of upload image (byte).")
            .translation("cfg.ci.maxsize")
            .defineInRange("maxsize", 3145728,0,20971520);
    public static final ModConfigSpec SPEC = BUILDER.build();

    public static Boolean remove_all;
    public static Integer maxFileSize;

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event)
    {
        remove_all=REMOVE_UPLOADS.get();
        maxFileSize=MAX_FILE_SIZE.get();
    }
}
