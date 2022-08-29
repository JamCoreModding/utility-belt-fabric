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

package io.github.jamalam360.utility.belt.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

/**
 * @author Jamalam
 */
public class SimplerInventory implements Inventory, Cloneable {
    private DefaultedList<ItemStack> stacks;
    private InventoryChangedListener listener;

    public SimplerInventory(int size) {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    public void registerListener(InventoryChangedListener listener) {
        this.listener = listener;
    }

    private void updateListener() {
        if (this.listener != null) {
            this.listener.onInventoryChanged(this);
        }
    }

    @Override
    public int size() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return this.stacks.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.stacks.get(slot);
    }

    public DefaultedList<ItemStack> getStacks() {
        return this.stacks;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(this.stacks, slot, amount);
        updateListener();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = this.stacks.set(slot, ItemStack.EMPTY);
        updateListener();
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
        updateListener();
    }

    @Override
    public void markDirty() {
        // NO-OP
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.stacks.clear();
        updateListener();
    }

    public void readNbtList(NbtList nbtList) {
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
            this.setStack(i, itemStack);
        }

        updateListener();
    }

    public NbtList toNbtList() {
        NbtList nbtList = new NbtList();

        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            nbtList.add(itemStack.writeNbt(new NbtCompound()));
        }

        return nbtList;
    }

    @Override
    public String toString() {
        return "SimplerInventory{" + "stacks=" + this.stacks + '}';
    }

    @Override
    public SimplerInventory clone() {
        try {
            SimplerInventory clone = (SimplerInventory) super.clone();
            clone.stacks = DefaultedList.copyOf(ItemStack.EMPTY, this.stacks.toArray(new ItemStack[0]));
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
