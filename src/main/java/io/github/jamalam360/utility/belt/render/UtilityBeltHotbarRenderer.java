/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.utility.belt.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.utility.belt.UtilityBeltClientInit;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.config.UtilityBeltConfig;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.registry.ItemRegistry;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * @author Jamalam
 */
public class UtilityBeltHotbarRenderer {

    private static final Identifier UTILITY_BELT_WIDGET_TEXTURE = UtilityBeltInit
          .idOf("textures/gui/utility_belt_widget.png");

    public static void render(GuiGraphics graphics, float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && TrinketsUtil.hasUtilityBelt(player) && (UtilityBeltClientInit.hasSwappedToUtilityBelt
                                                                      || UtilityBeltConfig.displayUtilityBeltWhenNotSelected)) {
            int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            graphics.drawTexture(UTILITY_BELT_WIDGET_TEXTURE, 2, scaledHeight / 2 - 44, 0, 0, 22, 88);

            if (UtilityBeltClientInit.hasSwappedToUtilityBelt) {
                graphics.drawTexture(ClickableWidget.WIDGETS_TEXTURE, 1, scaledHeight / 2 - 45 + UtilityBeltClientInit.utilityBeltSelectedSlot * 20,
                      0, 22, 24, 22);
            }

            TrinketsApi.getTrinketComponent(player).ifPresent((component) -> {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int m = 1;

                SimplerInventory inv = UtilityBeltItem
                      .getInventory(component.getEquipped(ItemRegistry.UTILITY_BELT).get(0).getRight());

                for (int n = 0; n < UtilityBeltInit.UTILITY_BELT_SIZE; ++n) {
                    renderHotbarItem(graphics, scaledHeight / 2 - 45 + n * 20 + 4, tickDelta, player, inv.getStack(n), m++);
                }

                RenderSystem.disableBlend();
            });
        }
    }

    private static void renderHotbarItem(GuiGraphics graphics, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            float f = (float)stack.getCooldown() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                graphics.getMatrices().push();
                graphics.getMatrices().translate(12, y + 12, 0);
                graphics.getMatrices().scale(1.0F / g, (g + 1.0F) / 2.0F, 1);
                graphics.getMatrices().translate(-12, -(y + 12), 0);
            }

            graphics.drawItem(player, stack, 4, y, seed);
            if (f > 0.0F) {
                graphics.getMatrices().pop();
            }

            graphics.drawItemInSlot(MinecraftClient.getInstance().textRenderer, stack, 4, y);
        }
    }
}
