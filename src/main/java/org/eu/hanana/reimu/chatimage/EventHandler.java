package org.eu.hanana.reimu.chatimage;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.*;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.UnicodeGlyphButton;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eu.hanana.reimu.chatimage.core.Actions;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.eu.hanana.reimu.chatimage.gui.MenuCiManager;
import org.eu.hanana.reimu.chatimage.networking.PayloadOpenGui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventHandler {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onScreenInit(ScreenEvent.Init.Pre event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Screen screen = event.getScreen();
        if (screen instanceof ChatScreen){
            Method method = Screen.class.getDeclaredMethod("addRenderableWidget", GuiEventListener.class);
            method.setAccessible(true);
            Button button;
            Field input = ChatScreen.class.getDeclaredField("input");
            input.setAccessible(true);
            button = Button.builder(Component.literal("+"),(pButton -> {
                PacketDistributor.sendToServer(new PayloadOpenGui(screen.getMinecraft().player.getId(),"chatimage:cim_menu"));
            })).bounds(0,0,25,25).build();
            method.invoke(screen, button);
        }
    }
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event){
        Component message = event.getMessage();
        String[] ciCodes = ChatImage.ChatImageData.getCiCodes(message.getString());
        if (ciCodes!=null) {
            String input = message.getString();
            for (String ciCode : ciCodes) {
                input = input.replaceFirst(escapeSpecialRegexChars(ciCode),"*#*#");
            }
            List<String> strings = splitWithDelimiter(input,"\\*#\\*#");
            int cp=0;
            MutableComponent result = Component.empty();
            for (int i = 0; i < strings.size(); i++) {
                String s = strings.get(i);
                if (s.equals("*#*#")){
                    try {
                        ChatImage.getChatImage(ciCodes[cp]);
                    } catch (Throwable e) {
                        result.append(Component.translatable("msg.ci.photo").setStyle(
                                Style.EMPTY
                                        .withColor(ChatFormatting.RED)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal(e.toString())))
                        ));
                        continue;
                    }
                    result.append(Component.translatable("msg.ci.photo").setStyle(
                            Style.EMPTY
                                    .withColor(ChatFormatting.GREEN)
                                    .withHoverEvent(new HoverEvent(Actions.SHOW_IMAGE,Component.literal(ciCodes[cp])))
                                    .withClickEvent(new ClickEvent(Actions.VIEW_IMAGE,ciCodes[cp]))
                    ));
                    cp++;
                }else {
                    result.append(s);
                }
            }
            event.setMessage(result);
        }
    }
    public static List<String> splitWithDelimiter(String input, String delimiter) {
        List<String> result = new ArrayList<>();
        // 正则表达式匹配分隔符或除分隔符外的任意字符序列
        Pattern pattern = Pattern.compile("(" + delimiter + "|[^" + delimiter + "]+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            result.add(matcher.group());
        }

        return result;
    }
    // 转义正则表达式特殊字符的方法
    private static String escapeSpecialRegexChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '.':
                case '(':
                case ')':
                case '[':
                case ']':
                case '{':
                case '}':
                case '?':
                case '*':
                case '+':
                case '^':
                case '$':
                case '\\':
                    sb.append('\\');
                    break;
                // 可以根据需要添加更多的特殊字符
                default:
                    // 字符不需要转义
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
