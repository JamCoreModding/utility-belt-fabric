package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Jamalam
 */

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "getStackInHand",
            at = @At("HEAD"),
            cancellable = true
    )
    private void toolbelt$useToolBeltStack(Hand hand, CallbackInfoReturnable<ItemStack> cir) {
        if (hand == Hand.MAIN_HAND && ((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            ItemStack stack = this.toolbelt$getSelectedToolBeltStack(player);

            if (stack != null) {
                cir.setReturnValue(stack);
            }
        }
    }

    @Inject(
            method = "getMainHandStack",
            at = @At("HEAD"),
            cancellable = true
    )
    private void toolbelt$useToolBeltStack2(CallbackInfoReturnable<ItemStack> cir) {
        if (((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            ItemStack stack = this.toolbelt$getSelectedToolBeltStack(player);

            if (stack != null) {
                cir.setReturnValue(stack);
            }
        }
    }

    @Inject(
            method = "setStackInHand",
            at = @At("HEAD"),
            cancellable = true
    )
    private void toolbelt$setStackInHandToolBelt(Hand hand, ItemStack stack, CallbackInfo ci) {
        if (hand == Hand.MAIN_HAND && ((LivingEntity) (Object) this) instanceof PlayerEntity player) {
            boolean selected = false;
            int selectedSlot = 0;

            if (player.world.isClient) {
                if (ToolBeltClientInit.hasSwappedToToolBelt) {
                    selected = true;
                    selectedSlot = ToolBeltClientInit.toolBeltSelectedSlot;
                }
            } else {
                if (ToolBeltInit.TOOL_BELT_SELECTED.getOrDefault(player, false)) {
                    selected = true;
                    selectedSlot = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);
                }
            }

            if (selected) {
                ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
                SimplerInventory inv = ToolBeltItem.getInventory(toolBelt);
                inv.setStack(selectedSlot, stack);
                ToolBeltItem.update(toolBelt, inv);
                ci.cancel();
            }
        }
    }

    @Unique
    private ItemStack toolbelt$getSelectedToolBeltStack(PlayerEntity player) {
        boolean selected = false;
        int selectedSlot = 0;

        if (player.world.isClient) {
            if (ToolBeltClientInit.hasSwappedToToolBelt) {
                selected = true;
                selectedSlot = ToolBeltClientInit.toolBeltSelectedSlot;
            }
        } else {
            if (ToolBeltInit.TOOL_BELT_SELECTED.getOrDefault(player, false)) {
                selected = true;
                selectedSlot = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);
            }
        }

        if (selected && TrinketsUtil.hasToolBelt(player)) {
            ItemStack stack = TrinketsUtil.getToolBelt(player);
            return ToolBeltItem.getInventory(stack).getStack(selectedSlot);
        }

        return null;
    }
}
