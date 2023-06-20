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

import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;
import io.github.jamalam360.jamlib.network.JamLibS2CNetworkChannel;
import io.github.jamalam360.utility.belt.Ducks;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.screen.UtilityBeltScreenHandler;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

/**
 * @author Jamalam
 */
public class Networking {

    public static final JamLibS2CNetworkChannel SWING_HAND = new JamLibS2CNetworkChannel(
          UtilityBeltInit.idOf("swing_hand"));
    public static final JamLibS2CNetworkChannel SET_UTILITY_BELT_SELECTED_S2C = new JamLibS2CNetworkChannel(
          UtilityBeltInit.idOf("set_utility_belt_selected_s2c"));

    public static final JamLibS2CNetworkChannel SET_UTILITY_BELT_SELECTED_SLOT_S2C = new JamLibS2CNetworkChannel(
          UtilityBeltInit.idOf("set_utility_belt_selected_slot_s2c"));
    public static final JamLibS2CNetworkChannel SYNC_UTILITY_BELT_INVENTORY = new JamLibS2CNetworkChannel(
          UtilityBeltInit.idOf("sync_utility_belt_inventory"));
    public static final JamLibS2CNetworkChannel ON_MOVE_PICKAXE_TO_BELT = new JamLibS2CNetworkChannel(
          UtilityBeltInit.idOf("on_move_pickaxe_to_belt"));
    public static final JamLibC2SNetworkChannel SET_UTILITY_BELT_SELECTED_C2S = new JamLibC2SNetworkChannel(
          UtilityBeltInit.idOf("set_utility_belt_selected_c2s"));
    public static final JamLibC2SNetworkChannel SET_UTILITY_BELT_SELECTED_SLOT_C2S = new JamLibC2SNetworkChannel(
          UtilityBeltInit.idOf("set_utility_belt_selected_slot_c2s"));
    public static final JamLibC2SNetworkChannel OPEN_SCREEN = new JamLibC2SNetworkChannel(
          UtilityBeltInit.idOf("open_screen"));

    /*
     * Called server side
     */
    public static void setHandlers() {
        SET_UTILITY_BELT_SELECTED_SLOT_C2S.setHandler((server, player, handler, buf, responseSender) -> {
            int slot = buf.readInt();

            if (slot >= 0 && slot < UtilityBeltInit.UTILITY_BELT_SIZE) {
                UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(player.getUuid(), slot);
                ((Ducks.LivingEntity) player).updateEquipment();
            }
        });

        SET_UTILITY_BELT_SELECTED_C2S.setHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToUtilityBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasUtilityBelt(player)) {
                    ItemStack utilityBelt = TrinketsUtil.getUtilityBelt(player);
                    SimplerInventory utilityBeltInventory = UtilityBeltItem.getInventory(utilityBelt);

                    if (hasSwappedToUtilityBelt) {
                        // We use this rather than getStackInHand since at this point, the map already
                        // states that the player is swapped to the utility belt, so getStackInHand
                        // returns
                        // whatever is in there.
                        ItemStack heldItem = player.getInventory().getStack(player.getInventory().selectedSlot);

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
                                // Same reason as above.
                                player.getInventory().setStack(player.getInventory().selectedSlot, ItemStack.EMPTY);
                                UtilityBeltItem.update(utilityBelt, utilityBeltInventory);
                                UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.put(player.getUuid(), utilityBeltSlot);
                                final int finalUtilityBeltSlot = utilityBeltSlot;
                                SET_UTILITY_BELT_SELECTED_SLOT_S2C.send(player,
                                      (resBuf) -> resBuf.writeInt(finalUtilityBeltSlot));

                                if (utilityBeltInventory.getStack(utilityBeltSlot).getItem() instanceof PickaxeItem) {
                                    ON_MOVE_PICKAXE_TO_BELT.send(player);
                                }
                            }
                        }
                    } else {
                        int utilityBeltSlot = UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player.getUuid(),
                              0);

                        if (!utilityBeltInventory.getStack(utilityBeltSlot).isEmpty()) {
                            int playerSlot = player.getInventory().selectedSlot;

                            if (!player.getInventory().getStack(playerSlot).isEmpty()) {
                                playerSlot = player.getInventory().getEmptySlot();
                            }

                            if (playerSlot != -1 && player.getInventory().getStack(playerSlot).isEmpty()) {
                                player.getInventory().setStack(playerSlot,
                                      utilityBeltInventory.getStack(utilityBeltSlot));
                                utilityBeltInventory.setStack(utilityBeltSlot, ItemStack.EMPTY);
                                UtilityBeltItem.update(utilityBelt, utilityBeltInventory);
                            }
                        }
                    }
                }
            }

            UtilityBeltInit.UTILITY_BELT_SELECTED.put(player.getUuid(), hasSwappedToUtilityBelt);
            ((Ducks.LivingEntity) player).updateEquipment();
            SWING_HAND.send(player);
        });

        OPEN_SCREEN.setHandler((server, player, handler, buf, responseSender) -> {
            if (TrinketsUtil.hasUtilityBelt(player)) {
                player.openHandledScreen(UtilityBeltScreenHandler.Factory.INSTANCE);
            }
        });
    }
}
