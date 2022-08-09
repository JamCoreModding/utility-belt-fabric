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
            this.addSlot(new Slot(inventory, l, 62 + l * 18, 17));
        }

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
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
