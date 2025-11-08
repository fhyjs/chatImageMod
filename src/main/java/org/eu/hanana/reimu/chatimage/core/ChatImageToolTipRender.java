package org.eu.hanana.reimu.chatimage.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

public class ChatImageToolTipRender {
    @Deprecated
    public static void renderTooltip(
            GuiGraphics guiGraphics,
            Font font,
            int x,
            int y,
            String ciCode
    ) {
        var positioner = DefaultTooltipPositioner.INSTANCE;
        List<ClientTooltipComponent> components = getClientTooltipComponents(ciCode);

        int i = 0;
        int j = components.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent clienttooltipcomponent : components) {
            int k = clienttooltipcomponent.getWidth(font);
            if (k > i) {
                i = k;
            }

            j += clienttooltipcomponent.getHeight(font);
        }

        int l1 = i;
        int i2 = j;
        Vector2ic vector2ic = positioner.positionTooltip(guiGraphics.guiWidth(), guiGraphics.guiHeight(), x, y, i, j);
        int l = vector2ic.x();
        int i1 = vector2ic.y();
        guiGraphics.pose().pushMatrix();
        TooltipRenderUtil.renderTooltipBackground(guiGraphics, l, i1, i, j, null);
        int j1 = i1;

        for (int k1 = 0; k1 < components.size(); k1++) {
            ClientTooltipComponent clienttooltipcomponent1 = components.get(k1);
            clienttooltipcomponent1.renderText(guiGraphics, font, l, j1);
            j1 += clienttooltipcomponent1.getHeight(font) + (k1 == 0 ? 2 : 0);
        }

        j1 = i1;

        for (int j2 = 0; j2 < components.size(); j2++) {
            ClientTooltipComponent clienttooltipcomponent2 = components.get(j2);
            clienttooltipcomponent2.renderImage(font, l, j1, l1, i2, guiGraphics);
            j1 += clienttooltipcomponent2.getHeight(font) + (j2 == 0 ? 2 : 0);
        }

        guiGraphics.pose().popMatrix();
    }

    public static @NotNull List<ClientTooltipComponent> getClientTooltipComponents(String ciCode) {
        List<ClientTooltipComponent> components = new ArrayList<>();
        ChatImage chatImage=null;
        try {
            chatImage = ChatImage.getChatImage(ciCode);
        } catch (Throwable e) {
            components.add(ClientTooltipComponent.create(Component.literal(e.toString()).getVisualOrderText()));
        }
        if (chatImage!=null){
            if (chatImage.status!=ImageStatus.OK) components.add(ClientTooltipComponent.create(Component.translatable("msg.ci."+chatImage.status).getVisualOrderText()));
            components.add(ClientTooltipComponent.create(Component.literal(chatImage.info).getVisualOrderText()));
            components.add(new ClientChatImageTooltip(chatImage));
        }
        return components;
    }
}
