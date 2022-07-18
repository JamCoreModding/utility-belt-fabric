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
