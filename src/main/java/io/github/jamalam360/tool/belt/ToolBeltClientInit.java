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
import io.github.jamalam360.jamlib.keybind.JamLibKeybinds;
import io.github.jamalam360.tool.belt.render.ToolBeltHotbarRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBind;

/**
 * @author Jamalam
 */
public class ToolBeltClientInit implements ClientModInitializer {
    public static boolean hasSwappedToToolBelt = false;
    public static int toolBeltSelectedSlot = 0;
    public static KeyBind SWAP_KEYBIND;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(ToolBeltHotbarRenderer::render);

        SWAP_KEYBIND = JamLibKeybinds.register(new JamLibKeybinds.JamLibKeybind(
                ToolBeltInit.MOD_ID,
                "tool_belt",
                InputUtil.KEY_T_CODE,
                (client) -> {
                    hasSwappedToToolBelt = !hasSwappedToToolBelt;
                    ToolBeltNetworking.SET_TOOL_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToToolBelt));
                }
        ));
    }
}
