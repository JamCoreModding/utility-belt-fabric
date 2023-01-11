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

package io.github.jamalam360.utility.belt.item;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Jamalam
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemInventoryComponent extends ItemComponent implements InventoryComponent<SimplerInventory> {

    public static final String INVENTORY = "Inventory";
    private SimplerInventory cachedValue;
    private SimplerInventory lastSynced;

    public ItemInventoryComponent(ItemStack stack) {
        super(stack);
    }

    @Override
    public SimplerInventory getInventory() {
        if (cachedValue == null) {
            if (!this.hasTag(INVENTORY, NbtType.LIST)) {
                this.putList(INVENTORY, new SimplerInventory(UtilityBeltInit.UTILITY_BELT_SIZE).toNbtList());
            }
            NbtList list = this.getList(INVENTORY, NbtType.COMPOUND);
            SimplerInventory inv = new SimplerInventory(UtilityBeltInit.UTILITY_BELT_SIZE);
            inv.readNbtList(list);
            cachedValue = inv;
            inv.registerListener(this::onInventoryChanged);
        }

        return cachedValue;
    }

    public void onInventoryChanged(Inventory inventory) {
        this.putList(INVENTORY, ((SimplerInventory) inventory).toNbtList());
        this.onTagInvalidated();
        this.cachedValue = (SimplerInventory) inventory;
    }

    public void syncToClientIfDirty(ServerPlayerEntity player) {
        boolean mustSync = false;

        if (this.cachedValue == null) {
            this.getInventory();
        }

        if (lastSynced == null) {
            mustSync = true;
        } else {
            for (int i = 0; i < lastSynced.size(); i++) {
                ItemStack one = lastSynced.getStack(i);
                ItemStack two = cachedValue.getStack(i);

                if (!ItemStack.areEqual(one, two)) {
                    mustSync = true;
                    break;
                }
            }
        }

        if (mustSync) {
            NbtCompound comp = new NbtCompound();
            comp.put(INVENTORY, this.cachedValue.toNbtList());
            Networking.SYNC_UTILITY_BELT_INVENTORY.send(player, (buf) -> buf.writeNbt(comp));

            lastSynced = cachedValue.clone();
        }
    }
}
