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
