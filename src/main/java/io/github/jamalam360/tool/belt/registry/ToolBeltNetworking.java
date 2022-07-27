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

package io.github.jamalam360.tool.belt.registry;

import io.github.jamalam360.jamlib.network.JamLibS2CNetworkChannel;
import io.github.jamalam360.tool.belt.Ducks;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * @author Jamalam
 */
public class ToolBeltNetworking {

    public static final JamLibS2CNetworkChannel SWING_HAND = new JamLibS2CNetworkChannel(ToolBeltInit.idOf("swing_hand"));

    public static void setHandlers() {
        ToolBeltClientNetworking.SET_TOOL_BELT_SELECTED_SLOT.setHandler((server, player, handler, buf, responseSender) -> {
            ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.put(player, buf.readInt());
            ((Ducks.LivingEntity) player).updateEquipment();
        });

        ToolBeltClientNetworking.SET_TOOL_BELT_SELECTED.setHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToToolBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasToolBelt(player)) {
                    ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
                    SimplerInventory toolBeltInventory = ToolBeltItem.getInventory(toolBelt);

                    if (hasSwappedToToolBelt) {
                        ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);

                        if (!heldItem.isEmpty() && ToolBeltItem.isValidItem(heldItem)) {
                            int toolBeltSlot = 0;

                            for (int i = 0; i < toolBeltInventory.size(); i++) {
                                if (toolBeltInventory.getStack(i).isEmpty()) {
                                    toolBeltSlot = i;
                                    break;
                                }
                            }

                            toolBeltInventory.setStack(toolBeltSlot, heldItem);
                            player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                            ToolBeltItem.update(toolBelt, toolBeltInventory);
                        }
                    } else {
                        int toolBeltSlot = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);

                        if (!toolBeltInventory.getStack(toolBeltSlot).isEmpty()) {
                            int playerSlot = player.getInventory().selectedSlot;

                            if (!player.getInventory().getStack(playerSlot).isEmpty()) {
                                playerSlot = player.getInventory().getEmptySlot();
                            }

                            player.getInventory().setStack(playerSlot, toolBeltInventory.getStack(toolBeltSlot));
                            toolBeltInventory.setStack(toolBeltSlot, ItemStack.EMPTY);
                            ToolBeltItem.update(toolBelt, toolBeltInventory);
                        }
                    }
                }
            }

            ToolBeltInit.TOOL_BELT_SELECTED.put(player, hasSwappedToToolBelt);
            ((Ducks.LivingEntity) player).updateEquipment();
            SWING_HAND.send(player);
        });
    }
}
