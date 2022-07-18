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

package io.github.jamalam360.tool.belt.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * @author Jamalam
 */
public class ToolBeltHotbarRenderer {
    private static final Identifier TOOL_BELT_WIDGET_TEXTURE = ToolBeltInit.idOf("textures/gui/tool_belt_widget.png");

    public static void render(MatrixStack matrices, float tickDelta){
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && TrinketsUtil.hasToolBelt(player)) {
            InGameHud hud = MinecraftClient.getInstance().inGameHud;
            int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            RenderSystem.setShaderTexture(0, TOOL_BELT_WIDGET_TEXTURE);
            hud.setZOffset(-90);
            hud.drawTexture(matrices, 2, scaledHeight / 2 - 44, 0, 0, 22, 88);

            if (ToolBeltClientInit.hasSwappedToToolBelt) {
                RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
                hud.drawTexture(matrices, 1, scaledHeight / 2 - 45 + ToolBeltClientInit.toolBeltSelectedSlot * 20, 0, 22, 24, 22);
            }

            TrinketsApi.getTrinketComponent(player).ifPresent((component) -> {
                hud.setZOffset(hud.getZOffset());
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int m = 1;

                SimplerInventory inv = ToolBeltItem.getInventory(component.getEquipped(ItemRegistry.TOOL_BELT).get(0).getRight());

                for (int n = 0; n < 4; ++n) {
                    renderHotbarItem(scaledHeight / 2 - 45 + n * 20 + 4, tickDelta, player, inv.getStack(n), m++);
                }

                RenderSystem.disableBlend();
            });
        }
    }

    private static void renderHotbarItem(int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            float f = (float) stack.getCooldown() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                matrixStack.push();
                matrixStack.translate(4 + 8, y + 12, 0.0);
                matrixStack.scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
                matrixStack.translate(-(4 + 8), -(y + 12), 0.0);
                RenderSystem.applyModelViewMatrix();
            }

            MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(player, stack, 4, y, seed);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            if (f > 0.0F) {
                matrixStack.pop();
                RenderSystem.applyModelViewMatrix();
            }

            MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, stack, 4, y);
        }
    }
}
