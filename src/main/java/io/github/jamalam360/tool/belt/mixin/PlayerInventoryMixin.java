package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam
 */

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
    @Shadow @Final public PlayerEntity player;

    @Inject(
            method = "getBlockBreakingSpeed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void toolbelt$getBlockBreakingSpeedWithToolBeltItem(BlockState state, CallbackInfoReturnable<Float> cir) {
        boolean selected = false;
        int selectedSlot = 0;

        if (this.player.world.isClient) {
            if (ToolBeltClientInit.hasSwappedToToolBelt) {
                selected = true;
                selectedSlot = ToolBeltClientInit.toolBeltSelectedSlot;
            }
        } else {
            if (ToolBeltInit.TOOL_BELT_SELECTED.getOrDefault(this.player, false)) {
                selected = true;
                selectedSlot = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(this.player, 0);
            }
        }

        if (selected && TrinketsUtil.hasToolBelt(player)) {
            ItemStack stack = TrinketsUtil.getToolBelt(player);
            cir.setReturnValue(ToolBeltItem.getInventory(stack).getStack(selectedSlot).getMiningSpeedMultiplier(state));
        }
    }
}
