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

package io.github.jamalam360.utility.belt;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.jamalam360.jamlib.keybind.JamLibKeybinds;
import io.github.jamalam360.jamlib.network.JamLibClientNetworking;
import io.github.jamalam360.utility.belt.registry.ClientNetworking;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.registry.ScreenHandlerRegistry;
import io.github.jamalam360.utility.belt.render.UtilityBeltHotbarRenderer;
import io.github.jamalam360.utility.belt.screen.UtilityBeltScreen;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBind;

/**
 * @author Jamalam
 */
public class UtilityBeltClientInit implements ClientModInitializer {
    public static boolean hasSwappedToUtilityBelt = false;
    public static int utilityBeltSelectedSlot = 0;
    public static KeyBind SWAP_KEYBIND_TOGGLE;
    public static KeyBind SWAP_KEYBIND_HOLD;
    public static KeyBind OPEN_SCREEN_KEYBIND;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(UtilityBeltHotbarRenderer::render);
        HandledScreens.register(ScreenHandlerRegistry.SCREEN_HANDLER, UtilityBeltScreen::new);

        SWAP_KEYBIND_TOGGLE = JamLibKeybinds.register(new JamLibKeybinds.JamLibKeybind(
                UtilityBeltInit.MOD_ID,
                "quick_switch_toggle",
                InputUtil.KEY_B_CODE,
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        Networking.SET_UTILITY_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                    }
                }
        ));

        SWAP_KEYBIND_HOLD = JamLibKeybinds.register(new JamLibKeybinds.JamLibHoldKeybind(
                UtilityBeltInit.MOD_ID,
                "quick_switch_hold",
                InputUtil.KEY_N_CODE,
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        Networking.SET_UTILITY_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                    }
                },
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        Networking.SET_UTILITY_BELT_SELECTED.send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                    }
                }
        ));

        OPEN_SCREEN_KEYBIND = JamLibKeybinds.register(new JamLibKeybinds.JamLibKeybind(
                UtilityBeltInit.MOD_ID,
                "open_screen",
                InputUtil.KEY_APOSTROPHE_CODE,
                (client) -> Networking.OPEN_SCREEN.send()
        ));

        ClientNetworking.setHandlers();
        JamLibClientNetworking.registerHandlers(UtilityBeltInit.MOD_ID);
    }
}
