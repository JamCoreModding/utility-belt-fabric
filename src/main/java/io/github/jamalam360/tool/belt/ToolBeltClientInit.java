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
import io.github.jamalam360.tool.belt.render.ToolBeltHotbarRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBind;
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

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(ToolBeltHotbarRenderer::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOOL_BELT_KEYBIND.wasPressed()) {
                hasSwappedToToolBelt = !hasSwappedToToolBelt;
                ToolBeltNetworking.SET_TOOL_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToToolBelt));
            }
        });
    }
}
