package io.github.jamalam360.tool.belt.screen;

import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * @author Jamalam
 */
public class ToolBeltScreenHandler extends ScreenHandler {
    public int selectedSlot;
    private final SimplerInventory inventory;

    public ToolBeltScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimplerInventory(3), 0);
        this.selectedSlot = buf.readInt();
    }

    public ToolBeltScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, int selectedSlot) {
        super(ToolBeltInit.TOOL_BELT_SCREEN_HANDLER, syncId);
        this.selectedSlot = selectedSlot;
        checkSize(inventory, 3);
        this.inventory = (SimplerInventory) inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        for (m = 0; m < 1; ++m) {
            for (l = 0; l < 3; ++l) {
                this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 76 + m * 18));
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
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
    public void close(PlayerEntity player) {
        ToolBeltItem.update(player, this.inventory, this.selectedSlot);
        super.close(player);
    }
}
