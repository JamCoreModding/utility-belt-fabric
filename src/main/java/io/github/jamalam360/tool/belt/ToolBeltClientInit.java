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

package io.github.jamalam360.tool.belt;

import com.mojang.blaze3d.platform.InputUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.tool.belt.config.ToolBeltConfig;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * @author Jamalam
 */
public class ToolBeltClientInit implements ClientModInitializer {
    public static final KeyBind TOOL_BELT_KEYBIND = KeyBindingHelper.registerKeyBinding(
            new KeyBind(
                    "key.toolbelt.tool_belt",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_T,
                    "category.toolbelt.tool_belt"
            )
    );

    public static boolean hasSwappedToToolBelt = false;
    public static int toolBeltSelectedSlot = 0;
    private static final Identifier TOOL_BELT_WIDGET_TEXTURE = ToolBeltInit.idOf("textures/gui/tool_belt_widget.png");

    @Override
    public void onInitializeClient() {
        JamLibConfig.init("toolbelt", ToolBeltConfig.class);

        HudRenderCallback.EVENT.register(this::renderToolBeltHotbar);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOOL_BELT_KEYBIND.wasPressed()) {
                hasSwappedToToolBelt = !hasSwappedToToolBelt;

                ToolBeltNetworking.SET_TOOL_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToToolBelt));
            }
        });
    }

    private void renderToolBeltHotbar(MatrixStack matrices, float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null && TrinketsUtil.hasToolBelt(player)) {
            InGameHud hud = MinecraftClient.getInstance().inGameHud;
            int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            RenderSystem.setShaderTexture(0, TOOL_BELT_WIDGET_TEXTURE);
            hud.setZOffset(-90);
            hud.drawTexture(matrices, 2, scaledHeight / 2 - 44, 0, 0, 22, 88);

            if (hasSwappedToToolBelt) {
                RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
                hud.drawTexture(matrices, 1, scaledHeight / 2 - 45 + toolBeltSelectedSlot * 20, 0, 22, 24, 22);
            }

            TrinketsApi.getTrinketComponent(player).ifPresent((component) -> {
                hud.setZOffset(hud.getZOffset());
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int m = 1;

                SimplerInventory inv = ToolBeltItem.getInventory(component.getEquipped(ItemRegistry.TOOL_BELT).get(0).getRight());

                for (int n = 0; n < 4; ++n) {
                    this.renderHotbarItem(2 + 2, scaledHeight / 2 - 45 + n * 20 + 4, tickDelta, player, inv.getStack(n), m++);
                }

                RenderSystem.disableBlend();
            });
        }
    }

    private void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed) {
        if (!stack.isEmpty()) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            float f = (float) stack.getCooldown() - tickDelta;
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                matrixStack.push();
                matrixStack.translate(x + 8, y + 12, 0.0);
                matrixStack.scale(1.0F / g, (g + 1.0F) / 2.0F, 1.0F);
                matrixStack.translate(-(x + 8), -(y + 12), 0.0);
                RenderSystem.applyModelViewMatrix();
            }

            MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(player, stack, x, y, seed);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            if (f > 0.0F) {
                matrixStack.pop();
                RenderSystem.applyModelViewMatrix();
            }

            MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, stack, x, y);
        }
    }
}
