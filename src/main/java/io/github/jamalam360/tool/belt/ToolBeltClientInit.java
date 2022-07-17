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
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.tool.belt.config.ToolBeltConfig;
import io.github.jamalam360.tool.belt.screen.ToolBeltScreen;
import io.github.jamalam360.tool.belt.util.Ducks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBind;
import net.minecraft.network.PacketByteBuf;
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

    @Override
    public void onInitializeClient() {
        JamLibConfig.init("toolbelt", ToolBeltConfig.class);

        HandledScreens.register(ToolBeltInit.TOOL_BELT_SCREEN_HANDLER, ToolBeltScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen instanceof ToolBeltScreen) {
                if (((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getType() == InputUtil.Type.KEYSYM) {
                    TOOL_BELT_KEYBIND.setPressed(InputUtil.isKeyPressed(client.getWindow().getHandle(), ((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getKeyCode()));
                } else if (((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getType() == InputUtil.Type.MOUSE) {
                    TOOL_BELT_KEYBIND.setPressed(GLFW.glfwGetMouseButton(client.getWindow().getHandle(), ((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getKeyCode()) == 1);
                }
            }

            if (TOOL_BELT_KEYBIND.isPressed() && client.currentScreen == null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(ToolBeltInit.TOOL_BELT_SCREEN_NETWORK_ID, buf);
            } else if (!TOOL_BELT_KEYBIND.isPressed() && client.currentScreen instanceof ToolBeltScreen) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(ToolBeltInit.TOOL_BELT_SCREEN_NETWORK_ID, buf);
            }
        });
    }
}
