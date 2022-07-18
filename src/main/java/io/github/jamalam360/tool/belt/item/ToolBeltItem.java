package io.github.jamalam360.tool.belt.item;

import dev.emi.trinkets.api.TrinketItem;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

/**
 * @author Jamalam
 */
public class ToolBeltItem extends TrinketItem {
    public ToolBeltItem(Settings settings) {
        super(settings);
    }

    public static void update(ItemStack stack, SimplerInventory inventory) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("Inventory", inventory.toNbtList());
        stack.setNbt(nbt);
    }

    public static SimplerInventory getInventory(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        SimplerInventory inventory = new SimplerInventory(4);

        if (nbt.contains("Inventory")) {
            inventory.readNbtList(nbt.getList("Inventory", 10));
        } else {
            nbt.put("Inventory", inventory.toNbtList());
            stack.setNbt(nbt);
        }

        return inventory;
    }

    public static boolean isValidItem(ItemStack stack) {
        return stack.getItem() instanceof ToolItem || stack.isEmpty();
    }
}
