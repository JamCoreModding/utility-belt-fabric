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

package io.github.jamalam360.tool.belt.screen;

import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.registry.ScreenHandlerRegistry;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * @author Jamalam
 */

public class ToolBeltScreenHandler extends ScreenHandler {
    private final SimplerInventory inventory;

    public ToolBeltScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimplerInventory(4));
    }

    public ToolBeltScreenHandler(int syncId, PlayerInventory playerInventory, SimplerInventory inventory) {
        super(ScreenHandlerRegistry.SCREEN_HANDLER, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;

        int m;
        int l;

        for (l = 0; l < 4; ++l) {
            this.addSlot(new Slot(inventory, l, 53 + l * 18, 17) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    return ToolBeltItem.isValidItem(stack);
                }
            });
        }

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 48 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 106));
        }
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        if (slot.getIndex() < this.inventory.size()) {
            return ToolBeltItem.isValidItem(stack);
        }

        return super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);

        if (slotIndex < this.inventory.size()) {
            ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
            ToolBeltItem.update(toolBelt, this.inventory);
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return TrinketsUtil.hasToolBelt(player);
    }

    @Override
    public void close(PlayerEntity player) {
        ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
        ToolBeltItem.update(toolBelt, this.inventory);

        super.close(player);
    }
}