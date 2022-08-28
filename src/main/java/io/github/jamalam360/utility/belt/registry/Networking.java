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

package io.github.jamalam360.utility.belt.registry;

import io.github.jamalam360.jamlib.network.JamLibS2CNetworkChannel;
import io.github.jamalam360.utility.belt.Ducks;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.screen.UtilityBeltScreenHandler;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * @author Jamalam
 */
public class Networking {

    public static final JamLibS2CNetworkChannel SWING_HAND = new JamLibS2CNetworkChannel(UtilityBeltInit.idOf("swing_hand"));
    public static final JamLibS2CNetworkChannel SET_UTILITY_BELT_SELECTED_SLOT = new JamLibS2CNetworkChannel(UtilityBeltInit.idOf("set_utility_belt_selected_slot"));
    public static final JamLibS2CNetworkChannel SYNC_UTILITY_BELT_INVENTORY = new JamLibS2CNetworkChannel(UtilityBeltInit.idOf("sync_utility_belt_inventory"));

    public static void setHandlers() {
        ClientNetworking.SET_UTILITY_BELT_SELECTED_SLOT.setHandler((server, player, handler, buf, responseSender) -> {
            UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(player, buf.readInt());
            ((Ducks.LivingEntity) player).updateEquipment();
        });

        ClientNetworking.SET_UTILITY_BELT_SELECTED.setHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToUtilityBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasUtilityBelt(player)) {
                    ItemStack utilityBelt = TrinketsUtil.getUtilityBelt(player);
                    SimplerInventory utilityBeltInventory = UtilityBeltItem.getInventory(utilityBelt);

                    if (hasSwappedToUtilityBelt) {
                        ItemStack heldItem = player.getStackInHand(Hand.MAIN_HAND);

                        if (!heldItem.isEmpty() && UtilityBeltItem.isValidItem(heldItem)) {
                            int utilityBeltSlot = 0;

                            for (int i = 0; i < utilityBeltInventory.size(); i++) {
                                if (utilityBeltInventory.getStack(i).isEmpty()) {
                                    utilityBeltSlot = i;
                                    break;
                                }
                            }

                            if (utilityBeltInventory.getStack(utilityBeltSlot).isEmpty()) {
                                utilityBeltInventory.setStack(utilityBeltSlot, heldItem);
                                player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                                UtilityBeltItem.update(utilityBelt, utilityBeltInventory);
                                UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(player, utilityBeltSlot);
                                final int finalUtilityBeltSlot = utilityBeltSlot;
                                SET_UTILITY_BELT_SELECTED_SLOT.send(player, (resBuf) -> resBuf.writeInt(finalUtilityBeltSlot));
                            }
                        }
                    } else {
                        int utilityBeltSlot = UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player, 0);

                        if (!utilityBeltInventory.getStack(utilityBeltSlot).isEmpty()) {
                            int playerSlot = player.getInventory().selectedSlot;

                            if (!player.getInventory().getStack(playerSlot).isEmpty()) {
                                playerSlot = player.getInventory().getEmptySlot();
                            }

                            if (player.getInventory().getStack(playerSlot).isEmpty()) {
                                player.getInventory().setStack(playerSlot, utilityBeltInventory.getStack(utilityBeltSlot));
                                utilityBeltInventory.setStack(utilityBeltSlot, ItemStack.EMPTY);
                                UtilityBeltItem.update(utilityBelt, utilityBeltInventory);
                            }
                        }
                    }
                }
            }

            UtilityBeltInit.UTILITY_BELT_SELECTED.put(player, hasSwappedToUtilityBelt);
            ((Ducks.LivingEntity) player).updateEquipment();
            SWING_HAND.send(player);
        });

        ClientNetworking.OPEN_SCREEN.setHandler((server, player, handler, buf, responseSender) -> {
            if (TrinketsUtil.hasUtilityBelt(player)) {
                player.openHandledScreen(UtilityBeltScreenHandler.Factory.INSTANCE);
            }
        });
    }
}
