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

package io.github.jamalam360.utility.belt.mixin;

import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: Try patch PlayerInventory.combinedInventories to include Utility Belt inventory.

/**
 * @author Jamalam
 */
@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    public int selectedSlot;

    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$getBlockBreakingSpeedWithUtilityBeltItem(BlockState state,
          CallbackInfoReturnable<Float> cir) {
        int slot;

        if (UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(player.getUuid(), false)) {
            slot = UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player.getUuid(), 0);

            if (TrinketsUtil.hasUtilityBelt(player)) {
                ItemStack stack = TrinketsUtil.getUtilityBelt(player);
                cir.setReturnValue(UtilityBeltItem.getInventory(stack).getStack(slot).getMiningSpeedMultiplier(state));
            }
        }
    }

    @Inject(method = "getMainHandStack", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$useUtilityBeltStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = UtilityBeltItem.getSelectedUtilityBeltStack(player);

        if (stack != null) {
            cir.setReturnValue(stack);
        }
    }

    /**
     * This fixes cases like tridents not being removed from the belt when thrown
     */
    @Inject(method = "removeOne", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchRemoveOneForHeldItems(ItemStack stack, CallbackInfo ci) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);
            int found = -1;

            for (int i = 0; i < inv.size(); i++) {
                if (ItemStack.areEqual(stack, inv.getStack(i))) {
                    found = i;
                    break;
                }
            }

            if (found != -1) {
                inv.setStack(found, ItemStack.EMPTY);
                UtilityBeltItem.update(belt, inv);
                ci.cancel();
            }
        }
    }

    @Inject(method = "populateRecipeFinder", at = @At("HEAD"))
    private void utilitybelt$recipeFinderPatch(RecipeMatcher finder, CallbackInfo ci) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            for (int i = 0; i < inv.size(); i++) {
                finder.addUnenchantedInput(inv.getStack(i));
            }
        }
    }

    @ModifyArg(method = "dropSelectedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;removeStack(II)Lnet/minecraft/item/ItemStack;"), index = 0)
    private int utilitybelt$dropSelectedUtilityBeltItem(int slot) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            return UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(player.getUuid(), false)
                   ? UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player.getUuid(), 0)
                   : slot;
        }

        return slot;
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$dropStackIfUsingUtilityBelt(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            if (UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(player.getUuid(), false)) {
                int slot = UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player.getUuid(), 0);
                ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
                SimplerInventory inv = UtilityBeltItem.getInventory(belt);
                ItemStack removed = inv.removeStack(slot);
                UtilityBeltItem.update(belt, inv);

                cir.setReturnValue(removed);
            }
        }
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void utilitybelt$clearUtilityBelt(CallbackInfo ci) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);
            inv.clear();
            UtilityBeltItem.update(belt, inv);
        }
    }

    @Inject(method = "contains(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchContainsStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            for (int i = 0; i < inv.size(); i++) {
                if (!inv.getStack(i).isEmpty() && inv.getStack(i).isItemEqualIgnoreDamage(stack)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "contains(Lnet/minecraft/registry/tag/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchContainsStack(TagKey<Item> key, CallbackInfoReturnable<Boolean> cir) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            for (int i = 0; i < inv.size(); i++) {
                if (!inv.getStack(i).isEmpty() && inv.getStack(i).isIn(key)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "dropAll", at = @At("HEAD"))
    private void utilitybelt$dropAllFromUtilityBelt(CallbackInfo ci) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            for (int i = 0; i < inv.size(); i++) {
                ItemStack itemStack = inv.getStack(i);
                if (!itemStack.isEmpty()) {
                    this.player.dropItem(itemStack, true, false);
                    inv.setStack(i, ItemStack.EMPTY);
                }
            }

            UtilityBeltItem.update(belt, inv);
        }
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    private void utilitybelt$patchIsEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            for (int i = 0; i < inv.size(); i++) {
                if (!inv.getStack(i).isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void utilitybelt$doInventoryTick(CallbackInfo ci) {
        if (TrinketsUtil.hasUtilityBelt(this.player)) {
            ItemStack belt = TrinketsUtil.getUtilityBelt(this.player);
            SimplerInventory inv = UtilityBeltItem.getInventory(belt);

            int selected = UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(this.player.getUuid(), false)
                           ? UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(this.player.getUuid(), 0)
                           : selectedSlot;

            for (int i = 0; i < inv.size(); i++) {
                ItemStack itemStack = inv.getStack(i);
                if (!itemStack.isEmpty()) {
                    itemStack.inventoryTick(this.player.world, this.player, i, selected == i);
                }
            }
        }
    }
}
