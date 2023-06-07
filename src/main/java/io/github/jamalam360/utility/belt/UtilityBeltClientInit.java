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
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.github.jamalam360.jamlib.event.client.MouseScrollCallback;
import io.github.jamalam360.jamlib.keybind.JamLibKeybinds;
import io.github.jamalam360.jamlib.network.JamLibClientNetworking;
import io.github.jamalam360.utility.belt.client.BeltModel;
import io.github.jamalam360.utility.belt.client.BeltRenderer;
import io.github.jamalam360.utility.belt.config.UtilityBeltConfig;
import io.github.jamalam360.utility.belt.registry.ClientNetworking;
import io.github.jamalam360.utility.belt.registry.ItemRegistry;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.registry.ScreenHandlerRegistry;
import io.github.jamalam360.utility.belt.render.UtilityBeltHotbarRenderer;
import io.github.jamalam360.utility.belt.screen.UtilityBeltScreen;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/**
 * @author Jamalam
 */
public class UtilityBeltClientInit implements ClientModInitializer {
    public static boolean hasSwappedToUtilityBelt = false;
    public static int utilityBeltSelectedSlot = 0;
    public static KeyBind SWAP_KEYBIND_TOGGLE;
    public static KeyBind SWAP_KEYBIND_HOLD;
    public static KeyBind OPEN_SCREEN_KEYBIND;
    public static final EntityModelLayer BELT_LAYER = new EntityModelLayer(UtilityBeltInit.idOf("belt"), "main");

    @Override
    public void onInitializeClient() {
        TrinketRendererRegistry.registerRenderer(ItemRegistry.UTILITY_BELT, new BeltRenderer());
        EntityModelLayerRegistry.registerModelLayer(BELT_LAYER, BeltModel::createTexturedModelData);
        HudRenderCallback.EVENT.register(UtilityBeltHotbarRenderer::render);
        HandledScreens.register(ScreenHandlerRegistry.SCREEN_HANDLER, UtilityBeltScreen::new);

        SWAP_KEYBIND_TOGGLE = JamLibKeybinds.register(new JamLibKeybinds.JamLibKeybind(
                UtilityBeltInit.MOD_ID,
                "quick_switch_toggle",
                InputUtil.KEY_B_CODE,
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        UtilityBeltInit.UTILITY_BELT_SELECTED.put(client.player.getUuid(), hasSwappedToUtilityBelt);
                        Networking.SET_UTILITY_BELT_SELECTED_C2S
                                .send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                        playSwapNoise();
                    }
                }));

        SWAP_KEYBIND_HOLD = JamLibKeybinds.register(new JamLibKeybinds.JamLibHoldKeybind(
                UtilityBeltInit.MOD_ID,
                "quick_switch_hold",
                InputUtil.KEY_N_CODE,
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        UtilityBeltInit.UTILITY_BELT_SELECTED.put(client.player.getUuid(), hasSwappedToUtilityBelt);
                        Networking.SET_UTILITY_BELT_SELECTED_C2S
                                .send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                        playSwapNoise();
                    }
                },
                (client) -> {
                    if (TrinketsUtil.hasUtilityBelt(client.player)) {
                        hasSwappedToUtilityBelt = !hasSwappedToUtilityBelt;
                        UtilityBeltInit.UTILITY_BELT_SELECTED.put(client.player.getUuid(), hasSwappedToUtilityBelt);
                        Networking.SET_UTILITY_BELT_SELECTED_C2S
                                .send((buf) -> buf.writeBoolean(hasSwappedToUtilityBelt));
                        playSwapNoise();
                    }
                }));

        OPEN_SCREEN_KEYBIND = JamLibKeybinds.register(new JamLibKeybinds.JamLibKeybind(
                UtilityBeltInit.MOD_ID,
                "open_screen",
                InputUtil.KEY_APOSTROPHE_CODE,
                (client) -> Networking.OPEN_SCREEN.send()));

        MouseScrollCallback.EVENT.register((mouseX, mouseY, amount) -> {
            if (UtilityBeltClientInit.hasSwappedToUtilityBelt) {
                if (amount > 0) {
                    if (!UtilityBeltConfig.isScrollingInverted) {
                        onMouseScrollInUtilityBelt(1);
                    } else {
                        onMouseScrollInUtilityBelt(-1);
                    }
                } else if (amount < 0) {
                    if (!UtilityBeltConfig.isScrollingInverted) {
                        onMouseScrollInUtilityBelt(-1);
                    } else {
                        onMouseScrollInUtilityBelt(1);
                    }
                }

                if (amount != 0) {
                    Networking.SET_UTILITY_BELT_SELECTED_SLOT_C2S
                            .send((buf) -> buf.writeInt(UtilityBeltClientInit.utilityBeltSelectedSlot));
                    UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(MinecraftClient.getInstance().player.getUuid(),
                            UtilityBeltClientInit.utilityBeltSelectedSlot);
                    playSwapNoise();
                }

                return true;
            }

            return false;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Networking.SET_UTILITY_BELT_SELECTED_SLOT_C2S
                    .send((buf) -> buf.writeInt(UtilityBeltClientInit.utilityBeltSelectedSlot));
            UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(MinecraftClient.getInstance().player.getUuid(),
                    UtilityBeltClientInit.utilityBeltSelectedSlot);
                
            Networking.SET_UTILITY_BELT_SELECTED_C2S
                    .send((buf) -> buf.writeBoolean(UtilityBeltClientInit.hasSwappedToUtilityBelt));
            UtilityBeltInit.UTILITY_BELT_SELECTED.put(MinecraftClient.getInstance().player.getUuid(), UtilityBeltClientInit.hasSwappedToUtilityBelt);
        });

        ClientNetworking.setHandlers();
        JamLibClientNetworking.registerHandlers(UtilityBeltInit.MOD_ID);

        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, ctx) -> {
                dispatcher.register(
                        ClientCommandManager.literal("utilitybelt_client")
                                .then(ClientCommandManager.literal("selected_slots")
                                        .executes(context -> {
                                            context.getSource().getPlayer().sendMessage(
                                                    Text.literal(UtilityBeltInit.UTILITY_BELT_SELECTED.toString()),
                                                    false);

                                            context.getSource().getPlayer().sendMessage(
                                                    Text.literal(
                                                            UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.toString()),
                                                    false);

                                            context.getSource().getPlayer().sendMessage(
                                                    Text.literal(Boolean
                                                            .toString(UtilityBeltClientInit.hasSwappedToUtilityBelt)),
                                                    false);

                                            context.getSource().getPlayer().sendMessage(
                                                    Text.literal(Integer
                                                            .toString(UtilityBeltClientInit.utilityBeltSelectedSlot)),
                                                    false);

                                            return 1;
                                        })));
            });
        }
    }

    private static void onMouseScrollInUtilityBelt(int direction) {
        if (direction == 1) {
            UtilityBeltClientInit.utilityBeltSelectedSlot--;
            if (UtilityBeltClientInit.utilityBeltSelectedSlot < 0) {
                UtilityBeltClientInit.utilityBeltSelectedSlot = UtilityBeltInit.UTILITY_BELT_SIZE - 1;
            }
        } else if (direction == -1) {
            UtilityBeltClientInit.utilityBeltSelectedSlot++;
            if (UtilityBeltClientInit.utilityBeltSelectedSlot >= UtilityBeltInit.UTILITY_BELT_SIZE) {
                UtilityBeltClientInit.utilityBeltSelectedSlot = 0;
            }
        }
    }

    private static void playSwapNoise() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(
                SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, MinecraftClient.getInstance().world.random.nextFloat() + 0.50f));
    }
}
