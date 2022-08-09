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

import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Jamalam
 */
public class TrinketsBehaviours {
    public static void registerEvents() {
        TrinketsApi.registerTrinketPredicate(ToolBeltInit.idOf("only_one_tool_belt"), (stack, slot, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem && entity instanceof PlayerEntity player) {
                return TrinketsUtil.hasToolBelt(player) ? TriState.FALSE : TriState.TRUE;
            } else {
                return TriState.DEFAULT;
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem && entity instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
                SimplerInventory inv = ToolBeltItem.getInventory(stack);

                for (int i = 0; i < inv.size(); i++) {
                    if (!inv.getStack(i).isEmpty()) {
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
    }
}
