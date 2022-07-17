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

package io.github.jamalam360.tool.belt.item;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

/**
 * @author Jamalam
 */
public class ToolBeltItem extends TrinketItem {
    public ToolBeltItem(Settings settings) {
        super(settings);
    }

    public static void update(PlayerEntity player, SimplerInventory inventory, int selectedSlot) {
        Optional<TrinketComponent> trinket = TrinketsApi.getTrinketComponent(player);

        if (trinket.isEmpty() || player.world.isClient) return;

        ItemStack playerStack = player.getInventory().getStack(player.getInventory().selectedSlot);
        player.getInventory().setStack(player.getInventory().selectedSlot, inventory.getStack(selectedSlot));
        inventory.setStack(selectedSlot, playerStack);

        ItemStack stack = trinket.get().getEquipped(ToolBeltInit.TOOL_BELT).get(0).getRight();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("Inventory", inventory.toNbtList());
        nbt.putInt("SelectedSlot", selectedSlot);
        stack.setNbt(nbt);
    }

    public static SimplerInventory getInventory(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        SimplerInventory inventory = new SimplerInventory(3);
        inventory.readNbtList(nbt.getList("Inventory", 10));
        return inventory;
    }

    public static int getSelectedSlot(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        return nbt.getInt("SelectedSlot");
    }
}
