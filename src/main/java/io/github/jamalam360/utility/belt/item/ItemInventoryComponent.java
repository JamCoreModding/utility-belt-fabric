package io.github.jamalam360.utility.belt.item;

import dev.onyxstudios.cca.api.v3.item.ItemComponent;
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
            if (!this.hasTag(INVENTORY, NbtType.LIST)) this.putList(INVENTORY, new SimplerInventory(4).toNbtList());
            NbtList list = this.getList(INVENTORY, NbtType.COMPOUND);
            SimplerInventory inv = new SimplerInventory(4);
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
