package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.List;

public class ConfigCore {
    public static final String root = "root";
    public static final String general = root+".general";
    public static final String client = root+".client";
    public static Configuration cfg;
    public static boolean isenabledTelnet=true;
    public static int telnetPort=23;

    public static void loadConfig(FMLPreInitializationEvent event) {
        // net.minecraftforge.common.config.Configurationのインスタンスを生成する。
        cfg = new Configuration(event.getSuggestedConfigurationFile(), ChatImageMod.VERSION, true);
        // 初期化する。
        initConfig();
        // コンフィグファイルの内容を変数と同期させる。
        syncConfig();
    }


    /** コンフィグを初期化する。 */
    private static void initConfig() {
        // カテゴリのコメントなどを設定する。
        // General
        cfg.addCustomCategoryComment(root, "鸡你太美mod配置.");
        cfg.setCategoryLanguageKey(root, "config.jntm.category.root");
        cfg.addCustomCategoryComment(client, "鸡你太美mod客户端配置.");
        cfg.setCategoryLanguageKey(client, "config.jntm.category.client");
        cfg.addCustomCategoryComment(general, "鸡你太美mod通用设置.");
        cfg.setCategoryLanguageKey(general, "config.jntm.category.general");
        // Difficulty
        //cfg.addCustomCategoryComment(DIFFICULTY, "The settings of difficulty.");
        //cfg.setCategoryLanguageKey(DIFFICULTY, "config.aluminium.category.difficulty");
        //cfg.setCategoryRequiresMcRestart(DIFFICULTY, true);
    }


    /** コンフィグを同期する。 */
    public static void syncConfig() {
        ChatImageMod.logger.log(Level.INFO,"Syncing config");
        // 各項目の設定値を反映させる。
        isenabledTelnet = cfg.getBoolean("isenabledTelnet", general, isenabledTelnet, "启动远程访问mod.(重启生效)", "config.jntm.prop.isenabledTelnet");
        telnetPort = cfg.getInt("telnetPort", general, telnetPort,0,32767, "远程访问端口", "远程访问端口");
        // Difficulty
        //amountSmelting = (byte) cfg.getInt("amountSmelting", DIFFICULTY, amountSmelting, 1, Byte.MAX_VALUE, "Smelting amount of Aluminium Ingot from Aluminium Ore.", "config.jntm.prop.amountSmelting");
        // 設定内容をコンフィグファイルに保存する。
        cfg.save();
    }
}
