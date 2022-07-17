package io.github.jamalam360.tool.belt.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

/**
 * @author Jamalam
 */
public class SimplerInventory implements Inventory {
    private final DefaultedList<ItemStack> stacks;

    public SimplerInventory(int size) {
        this.stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
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

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.stacks, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.stacks.remove(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.stacks.set(slot, stack);
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
    }

    public void readNbtList(NbtList nbtList) {
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
            this.setStack(i, itemStack);
        }
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
}
