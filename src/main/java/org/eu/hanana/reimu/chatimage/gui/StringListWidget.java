package org.eu.hanana.reimu.chatimage.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.neoforged.fml.VersionChecker;
import net.neoforged.fml.i18n.MavenVersionTranslator;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.client.gui.widget.ModListWidget;

public class StringListWidget extends ObjectSelectionList<StringListWidget.StringEntry> {
    private final int listWidth;

    private Screen parent;
    public StringListWidget(Screen parent, int listWidth, int top, int bottom) {
        super(parent.getMinecraft(), listWidth, bottom - top, top, parent.getMinecraft().font.lineHeight * 2);
        this.parent = parent;
        this.listWidth = listWidth;
    }
    public void add(Object s){
        addEntry(new StringEntry(parent,s));
    }
    public class StringEntry extends ObjectSelectionList.Entry<StringEntry> {
        private final Screen parent;
        private final Object string;
        public StringEntry(Screen parent,Object string){
            this.string = string;
            this.parent = parent;
        }

        public Object getObj() {
            return string;
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", Component.literal(string.toString()));
        }
        @Override
        public void render(GuiGraphics guiGraphics, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            Component name = Component.literal(string.toString());
            Font font = this.parent.getMinecraft().font;
            guiGraphics.drawString(font, Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(name, listWidth))), left + 3, top + 2, 0xFFFFFF, false);
        }
        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            StringListWidget.this.setSelected(this);
            return false;
        }
    }
}
