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

import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * @author Jamalam
 */
public class ToolBeltNetworking {
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected"));
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED_SLOT = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected_slot"));

    public static void registerHandlers() {
        ToolBeltNetworking.SET_TOOL_BELT_SELECTED_SLOT.registerHandler((server, player, handler, buf, responseSender) -> ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.put(player, buf.readInt()));

        ToolBeltNetworking.SET_TOOL_BELT_SELECTED.registerHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToToolBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasToolBelt(player)) {
                    ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
                    SimplerInventory inventory = ToolBeltItem.getInventory(toolBelt);

                    ItemStack playerStack = player.getEquippedStack(EquipmentSlot.MAINHAND);

                    if (!ToolBeltItem.isValidItem(playerStack)) return;

                    int index = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);

                    if (hasSwappedToToolBelt && !inventory.getStack(index).isEmpty()) {
                        for (int i = 0; i < inventory.size(); i++) {
                            if (inventory.getStack(i).isEmpty()) {
                                index = i;
                                break;
                            }
                        }
                    }

                    ItemStack toolBeltStack = inventory.getStack(index);

                    inventory.setStack(index, playerStack);
                    player.getInventory().main.set(player.getInventory().selectedSlot, toolBeltStack);

                    ToolBeltItem.update(toolBelt, inventory);
                }

            }

            ToolBeltInit.TOOL_BELT_SELECTED.put(player, hasSwappedToToolBelt);
        });
    }
}
