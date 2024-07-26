package org.eu.hanana.reimu.chatimage.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.eu.hanana.reimu.chatimage.core.ChatImage;
import org.joml.Vector2ic;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class RenderCi {
    public static void render(Font pFont, String ciCode, GuiGraphics guiGraphics, int pMouseX, int pMouseY){
        if (!ciCode.isEmpty()) {
            ChatImage chatImage;
            try {
                chatImage = ChatImage.getChatImage(ciCode);
            } catch (Throwable e) {
                Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastId.PACK_LOAD_FAILURE, Component.literal("ERROR/错误"),Component.literal(e.toString())));
                return;
            }
            List<FormattedCharSequence> split = new ArrayList<>();
            split.addAll(pFont.split(Component.translatable("msg.ci.status."+chatImage.status.toString().toLowerCase()), Math.max(guiGraphics.guiWidth() / 2, 200)));
            split.addAll(pFont.split(Component.literal(chatImage.info), Math.max(guiGraphics.guiWidth() / 2, 200)));
            List<ClientTooltipComponent> pComponents = new ArrayList<>();
            for (FormattedCharSequence formattedCharSequence : split) {
                pComponents.add(ClientTooltipComponent.create(formattedCharSequence));
            }
            int i = 0;
            int j = pComponents.size() == 1 ? -2 : 0;

            for (ClientTooltipComponent clienttooltipcomponent : pComponents) {
                int k = clienttooltipcomponent.getWidth(pFont);
                if (k > i) {
                    i = k;
                }

                j += clienttooltipcomponent.getHeight();
            }

            int i2 = Math.max(i, chatImage.w);
            int j2 = j+chatImage.h;
            Vector2ic vector2ic = DefaultTooltipPositioner.INSTANCE.positionTooltip(guiGraphics.guiWidth(), guiGraphics.guiHeight(), pMouseX, pMouseY, i2, j2);
            int l = vector2ic.x();
            int i1 = vector2ic.y();
            guiGraphics.pose().pushPose();
            int j1 = 400;
            net.neoforged.neoforge.client.event.RenderTooltipEvent.Color colorEvent = net.neoforged.neoforge.client.ClientHooks.onRenderTooltipColor(ItemStack.EMPTY, guiGraphics, l, i1, pFont, pComponents);
            guiGraphics.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(guiGraphics, l, i1, i2, j2, 400, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd(), colorEvent.getBorderStart(), colorEvent.getBorderEnd()));
            ResourceLocation texture = chatImage.getTexture();

            guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
            if (texture!=null) {
                guiGraphics.blit(texture, l, i1+j, 0, 0, chatImage.w,chatImage.h,chatImage.w,chatImage.h);
            }
            //guiGraphics.fill(l,i1,i2+l,j2+i1,0xFFFFFFFF);
            int k1 = i1;

            for (int l1 = 0; l1 < pComponents.size(); l1++) {
                ClientTooltipComponent clienttooltipcomponent1 = pComponents.get(l1);
                clienttooltipcomponent1.renderText(pFont, l, k1, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
                k1 += clienttooltipcomponent1.getHeight() + (l1 == 0 ? 2 : 0);
            }

            k1 = i1;

            for (int k2 = 0; k2 < pComponents.size(); k2++) {
                ClientTooltipComponent clienttooltipcomponent2 = pComponents.get(k2);
                clienttooltipcomponent2.renderImage(pFont, l, k1, guiGraphics);
                k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
            }
            guiGraphics.pose().popPose();
        }
    }

}
