package org.eu.hanana.reimu.chatimage.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ChatImageConfig {

    public static Boolean remove_all;
    public static Boolean copy_base64;
    public static Integer maxFileSize;
    public static Integer maxPvWidth;
    public static Integer maxPvHeight;
    public static Boolean autoViewRaw;

    public static class Common{
        Common(ModConfigSpec.Builder builder) {
            MAX_FILE_SIZE = builder
                    .comment("The max file size of upload image (byte).")
                    .translation("cfg.ci.maxsize")
                    .defineInRange("maxsize", 3145728,0,20971520);
            REMOVE_UPLOADS = builder
                    .comment("Delete all uploaded image when server restart.")
                    .translation("cfg.ci.remove_all")
                    .define("remove_uploads", true);
        }
        public final ModConfigSpec.BooleanValue REMOVE_UPLOADS;
        public final ModConfigSpec.IntValue MAX_FILE_SIZE;
    }
    public static final ModConfigSpec commonSpec;
    public static final Common COMMON;
    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Client{
        Client(ModConfigSpec.Builder builder) {

            COPY_BASE64 = builder
                    .comment("[CLIENT] Copy base64 data instead of ci code.")
                    .translation("cfg.ci.copy_base64")
                    .define("copy_base64", false);
            MAX_PV_W = builder
                    .comment("[CLIENT] max preview width, 0 means infinty.")
                    .translation("cfg.ci.maxpvw")
                    .defineInRange("max_pv_w", 200,0,1000);
            MAX_PV_H = builder
                    .comment("[CLIENT] max preview height, 0 means infinty.")
                    .translation("cfg.ci.maxpvh")
                    .defineInRange("max_pv_h", 200,0,1000);
            AUTO_VIEW_RAW = builder
                    .comment("[CLIENT] Enable auto view raw in image view screen,.")
                    .translation("cfg.ci.autopv")
                    .define("auto_view_raw", true);
        }
        public final ModConfigSpec.BooleanValue COPY_BASE64;
        public final ModConfigSpec.BooleanValue AUTO_VIEW_RAW;
        public final ModConfigSpec.IntValue MAX_PV_W;
        public final ModConfigSpec.IntValue MAX_PV_H;
    }
    public static final ModConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getType()== ModConfig.Type.CLIENT) {
            copy_base64 = CLIENT.COPY_BASE64.get();
            maxPvWidth = CLIENT.MAX_PV_W.get();
            maxPvHeight = CLIENT.MAX_PV_H.get();
            autoViewRaw = CLIENT.AUTO_VIEW_RAW.get();
        }
        if (event.getConfig().getType()== ModConfig.Type.COMMON) {
            remove_all=COMMON.REMOVE_UPLOADS.get();
            maxFileSize=COMMON.MAX_FILE_SIZE.get();
        }
    }
}
