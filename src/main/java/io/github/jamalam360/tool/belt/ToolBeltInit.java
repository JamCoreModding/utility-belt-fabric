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

import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import io.github.jamalam360.jamlib.log.JamLibLogger;
import io.github.jamalam360.jamlib.registry.JamLibRegistry;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ToolBeltInit implements ModInitializer {
    public static final String MOD_ID = "toolbelt";
    private static final JamLibLogger LOGGER = JamLibLogger.getLogger(MOD_ID);

    public static final Map<PlayerEntity, Boolean> TOOL_BELT_SELECTED = new Object2BooleanArrayMap<>();
    public static final Map<PlayerEntity, Integer> TOOL_BELT_SELECTED_SLOTS = new Object2IntArrayMap<>();

    @Override
    public void onInitialize() {
        JamLibRegistry.register(ItemRegistry.class);

        ToolBeltNetworking.SET_TOOL_BELT_SELECTED_SLOT.registerHandler((server, player, handler, buf, responseSender) -> TOOL_BELT_SELECTED_SLOTS.put(player, buf.readInt()));

        ToolBeltNetworking.SET_TOOL_BELT_SELECTED.registerHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToToolBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasToolBelt(player)) {
                    ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
                    SimplerInventory inventory = ToolBeltItem.getInventory(toolBelt);

                    ItemStack playerStack = player.getEquippedStack(EquipmentSlot.MAINHAND);

                    if (!ToolBeltItem.isValidItem(playerStack)) return;

                    int index = TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);

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

            TOOL_BELT_SELECTED.put(player, hasSwappedToToolBelt);
        });

        TrinketsApi.registerTrinketPredicate(idOf("only_one_tool_belt"), (stack, slot, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem && entity instanceof PlayerEntity player) {
                return TrinketsUtil.hasToolBelt(player) ? TriState.FALSE : TriState.TRUE;
            } else {
                return TriState.DEFAULT;
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem) {
                SimplerInventory inv = ToolBeltItem.getInventory(stack);

                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStack(i).isEmpty()) {
                        ItemEntity item = EntityType.ITEM.create(entity.world);
                        item.refreshPositionAfterTeleport(entity.getPos());
                        item.setStack(inv.getStack(i));
                        entity.world.spawnEntity(item);
                    }
                }

                inv.clear();
                ToolBeltItem.update(stack, inv);

                return TrinketEnums.DropRule.DROP;
            } else {
                return TrinketEnums.DropRule.DEFAULT;
            }
        });

        LOGGER.logInitialize();
    }

    public static Identifier idOf(String path) {
        return new Identifier(MOD_ID, path);
    }
}