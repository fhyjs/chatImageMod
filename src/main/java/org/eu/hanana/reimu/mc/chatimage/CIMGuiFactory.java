package org.eu.hanana.reimu.mc.chatimage;


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Set;

public class CIMGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
    }
    @Override
    public boolean hasConfigGui() {
        return true;
    }
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new  ConfigGuiScreen(parentScreen,(new ConfigElement(ConfigCore.cfg.getCategory(ConfigCore.root))));
    }
    private static final Set<RuntimeOptionCategoryElement> fmlCategories = ImmutableSet.of(new RuntimeOptionCategoryElement("HELP", "FML"));
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return fmlCategories;
    }
    public static class ConfigGuiScreen extends GuiConfig
    {
        public ConfigGuiScreen(GuiScreen parent,ConfigElement ce)
        {
            super(parent, ce.getChildElements(), ChatImageMod.MODID, false, false, ChatImageMod.MODID);
        }
    }
}
