package org.eu.hanana.reimu.mc.chatimage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.eu.hanana.reimu.mc.chatimage.Event.RenderTooltipImageEvent;
import org.eu.hanana.reimu.mc.chatimage.enums.Actions;
import org.eu.hanana.reimu.mc.chatimage.http.WsHandler;
import org.eu.hanana.reimu.mc.chatimage.http.apis.ws.WsApiBase;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect;

@Mod.EventBusSubscriber
public class EventHandler {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiMainMenu)
            ChatImage.clearCache();
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiChat){
            event.getButtonList().add(new GuiButton(event.getButtonList().size(),0,0,20,20,"+"){
                @Override
                public void playPressSound(SoundHandler soundHandlerIn) {
                    super.playPressSound(soundHandlerIn);
                    event.getGui().mc.displayGuiScreen(new ScreenCIChat());
                }
            });
        }
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        // 在这里执行你的自定义操作
        ITextComponent itextcomponent = event.getGui().mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());

        if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null)
        {
            this.handleComponentHover(event.getGui(),itextcomponent, event.getMouseX(), event.getMouseY());
        }

    }
    @SideOnly(Side.CLIENT)
    protected void handleComponentHover(GuiScreen gui, ITextComponent component, int x, int y){
        if (component != null && component.getStyle().getHoverEvent() != null)
        {
            HoverEvent hoverevent = component.getStyle().getHoverEvent();

            if (hoverevent.getAction() == Actions.SHOW_IMAGE) {
                ITextComponent value = hoverevent.getValue();
                ChatImage ci = null;
                try {
                    ci = ChatImage.getChatImage(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ci != null) {
                    drawHoveringImage(gui, ci, x, y, gui.width, gui.height, -1, gui.mc.fontRenderer);
                }
            }

            GlStateManager.disableLighting();
        }
    }
    @SideOnly(Side.CLIENT)
    public static void  drawHoveringImage(GuiScreen guiScreen, @NotNull ChatImage chatImage, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
    {
        if (chatImage!=null)
        {
            List<String> textLines = new ArrayList<>(chatImage.information);

            RenderTooltipImageEvent.Pre event = new RenderTooltipImageEvent.Pre(chatImage, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            GlStateManager.pushMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for (String textLine : textLines)
            {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth)
                {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                    {
                        tooltipTextWidth = mouseX - 12 - 8;
                    }
                    else
                    {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
            {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine)
                    {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth)
                        {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                }
                else
                {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12 - chatImage.height;
            int tooltipHeight = 8;

            if (textLines.size() > 1)
            {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4)
            {
                tooltipY = 4;
            }
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
            {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            int imageX=tooltipX+1;
            int imageY=tooltipY+1;
            tooltipTextWidth+= chatImage.width+1;
            tooltipHeight+= chatImage.height+1;

            final int zLevel = 300;
            int backgroundColor = 0xF0100010;
            int borderColorStart = 0x505000FF;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            RenderTooltipImageEvent.Color colorEvent = new RenderTooltipImageEvent.Color(chatImage, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
            GlStateManager.popMatrix();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipImageEvent.PostBackground(chatImage, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            int tooltipTop = tooltipY;

            // 获取渲染引擎
            Minecraft mc = Minecraft.getMinecraft();

            if (chatImage.status!= ChatImage.ImageStatus.OK&&chatImage.status!= ChatImage.ImageStatus.WAITING&&chatImage.status!= ChatImage.ImageStatus.ERROR){
                chatImage.getImage(chatImage.source);
            }
            if (chatImage.status == ChatImage.ImageStatus.OK) {

                ResourceLocation resourceLocation = chatImage.getImage();
                if (resourceLocation==null){
                    textLines.add("§4"+ net.minecraft.client.resources.I18n.format("image.fail"));
                }else {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(1F, 1F, 1.0F); // 缩放因子，根据需求调整
                    GlStateManager.color(1, 1, 1, 1);
                    mc.getTextureManager().bindTexture(resourceLocation); // 绑定材质
                    // 绘制材质
                    Gui.drawModalRectWithCustomSizedTexture(imageX, imageY, 0, 0, chatImage.width, chatImage.height, chatImage.width, chatImage.height);
                    GlStateManager.popMatrix();
                }
            }
            if (chatImage.status == ChatImage.ImageStatus.WAITING){
                textLines.add("§4"+ net.minecraft.client.resources.I18n.format("image.waiting"));
            }
            if (chatImage.status == ChatImage.ImageStatus.ERROR){
                textLines.add("§4"+ net.minecraft.client.resources.I18n.format("image.fail"));
            }

            //显示文字
            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }
            MinecraftForge.EVENT_BUS.post(new RenderTooltipImageEvent.PostText(chatImage, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
    // 创建一个方法来处理聊天事件
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) throws IOException {
        // 获取发送聊天消息的玩家
        EntityPlayer player = event.getPlayer();
        // 获取聊天消息内容
        String message = event.getMessage();
        if (message.startsWith("/")) return;

        for (WsHandler handler : WsHandler.HANDLERS)
            WsApiBase.sendStrMsg(handler.getSession(),message.startsWith("#")?message.substring(1):message);

        // 定义正则表达式模式，匹配CI{...}形式的内容
        String pattern = "(CI\\{.*?\\})";

        // 创建正则表达式模式对象
        Pattern regex = Pattern.compile(pattern);

        // 创建匹配器对象
        Matcher matcher = regex.matcher(message);

        // 创建一个List来存储提取的内容和原始内容
        List<String> extractedContents = new ArrayList<>();

        // 记录上一个匹配的结束位置
        int lastEnd = 0;

        // 使用匹配器查找匹配的子字符串
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // 添加原始内容（从上一个结束位置到当前匹配的开始位置）
            extractedContents.add(message.substring(lastEnd, start));

            // 添加提取的内容
            String jsonContent = matcher.group(1); // 获取匹配的内容，不包括CI{}
            extractedContents.add(jsonContent);

            // 更新上一个结束位置
            lastEnd = end;
        }

        // 添加剩余的原始内容（从最后一个匹配的结束位置到字符串末尾）
        if (lastEnd < message.length()) {
            extractedContents.add(message.substring(lastEnd));
        }
        ITextComponent textComponent = new TextComponentTranslation("chat.type.text", event.getPlayer().getName(), "");
        // 打印List中的内容
        for (String content : extractedContents) {
            if (content.startsWith("CI{")) {
                try {
                    ChatImage ci = ChatImage.getChatImage(content);
                    textComponent.appendSibling(new ChatImage.ChatImageData(ci).getChatMsg());
                } catch (MalformedURLException e) {
                    textComponent.appendSibling(new TextComponentString(e.toString()).setStyle(new Style().setColor(TextFormatting.RED)));
                    e.printStackTrace();
                }
            }
            else
                textComponent.appendSibling(new TextComponentString(content));
        }
        event.setComponent(textComponent);
    }
}
