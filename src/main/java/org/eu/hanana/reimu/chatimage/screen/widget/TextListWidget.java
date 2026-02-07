package org.eu.hanana.reimu.chatimage.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class TextListWidget extends ObjectSelectionList<TextListWidget.TextEntry> {

    private final int lw;
    protected Screen parent;
    public TextListWidget(Screen parent, int listWidth, int top, int bottom) {
        super(parent.getMinecraft(),listWidth,bottom - top, top, parent.getFont().lineHeight + 5);
        this.parent=parent;
        this.lw=listWidth;
    }
    public void onSelect(TextEntry entry){}
    @Override
    public void setScrollAmount(double scrollAmount) {
        super.setScrollAmount(scrollAmount);
    }

    //@Override
    protected TextEntry getEntry(int index) {
        return children().get(index);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    @Override
    protected int scrollBarX() {
        return this.lw+getX();
    }
    @Override
    public int addEntry(@NotNull TextEntry entry) {
        return super.addEntry(entry);
    }
    public TextEntry newEntry(String s){
        return new TextEntry(s);
    }
    public class TextEntry extends ObjectSelectionList.Entry<TextEntry>{
        public String text;
        public Identifier icon;

        public TextEntry(String s) {
            text=s;
            if (text==null) text="";
        }

        @Override
        public @NotNull Component getNarration() {
            return Component.translatable("narrator.select", text);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (focused){
                TextListWidget.this.onSelect(this);
            }
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            if (icon!=null){
                guiGraphics.blitInscribed(icon,getX(),getY(),11,11,11,11);
            }
            guiGraphics.drawString(parent.getFont(),text,getX()+12,getY()+1,0xffffffff);
        }

    }
}
