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

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.screen.ToolBeltScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class ToolBeltInit implements ModInitializer {
    public static final String MOD_ID = "toolbelt";
    private static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);

    public static final ScreenHandlerType<ToolBeltScreenHandler> TOOL_BELT_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(ToolBeltScreenHandler::new);
    public static final Identifier TOOL_BELT_SCREEN_NETWORK_ID = idOf("tool_belt_screen");
    public static final Identifier TOOL_BELT_SELECTED_SLOT = idOf("selected_slot");
    public static final Item TOOL_BELT = new ToolBeltItem(new Item.Settings().group(ItemGroup.TOOLS));

    @Override
    public void onInitialize() {
        LOGGER.logInitialize();

        Registry.register(Registry.SCREEN_HANDLER, idOf("tool_belt"), TOOL_BELT_SCREEN_HANDLER);
        Registry.register(Registry.ITEM, idOf("tool_belt"), TOOL_BELT);

        ServerPlayNetworking.registerGlobalReceiver(TOOL_BELT_SELECTED_SLOT, (((server, player, handler, buf, responseSender) -> {
            if (player.currentScreenHandler instanceof ToolBeltScreenHandler toolBeltScreenHandler) {
                toolBeltScreenHandler.selectedSlot = buf.readInt();
            }
        })));

        ServerPlayNetworking.registerGlobalReceiver(TOOL_BELT_SCREEN_NETWORK_ID, ((server, player, handler, buf, responseSender) -> {
            Optional<TrinketComponent> trinket = TrinketsApi.getTrinketComponent(player);

            if (trinket.isPresent() && trinket.get().isEquipped(TOOL_BELT)) {
                if (buf.readBoolean()) {
                    ItemStack stack = trinket.get().getEquipped(TOOL_BELT).get(0).getRight();

                    player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                        @Override
                        public Text getDisplayName() {
                            return stack.hasCustomName() ? stack.getName() : Text.literal("Tool Belt");
                        }

                        @Override
                        public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity player) {
                            return new ToolBeltScreenHandler(
                                    i,
                                    playerInventory,
                                    ToolBeltItem.getInventory(stack),
                                    ToolBeltItem.getSelectedSlot(stack)
                            );
                        }

                        @Override
                        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                            buf.writeInt(ToolBeltItem.getSelectedSlot(stack));
                        }
                    });
                } else {
                    player.closeHandledScreen();
                }
            }
        }));
    }

    public static Identifier idOf(String path) {
        return new Identifier(MOD_ID, path);
    }
}