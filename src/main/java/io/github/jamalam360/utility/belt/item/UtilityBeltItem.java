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

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.github.jamalam360.utility.belt.UtilityBeltInit;
import io.github.jamalam360.utility.belt.registry.Networking;
import io.github.jamalam360.utility.belt.util.SimplerInventory;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.SpyglassItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * @author Jamalam
 */
public class UtilityBeltItem extends TrinketItem {
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

    public UtilityBeltItem(Settings settings) {
        super(settings);
    }

    public static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    public static void update(ItemStack stack, SimplerInventory inventory) {
        ((ItemInventoryComponent) UtilityBeltInit.INVENTORY.get(stack)).onInventoryChanged(inventory);
    }

    public static SimplerInventory getInventory(ItemStack stack) {
        return ((ItemInventoryComponent) UtilityBeltInit.INVENTORY.get(stack)).getInventory();
    }

    public static boolean isValidItem(ItemStack stack) {
        return stack.getItem() instanceof ToolItem || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof SpyglassItem
                || stack.getItem() instanceof TridentItem || stack.getItem() instanceof FlintAndSteelItem
                || stack.getItem() instanceof ShearsItem || stack.isEmpty()
                || stack.isIn(UtilityBeltInit.ALLOWED_IN_UTILITY_BELT);
    }

    public static ItemStack getSelectedUtilityBeltStack(PlayerEntity player) {
        int slot;

        if (UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(player.getUuid(), false)) {
            slot = UtilityBeltInit.UTILITY_BELT_SELECTED_SLOTS.getOrDefault(player.getUuid(), 0);

            if (TrinketsUtil.hasUtilityBelt(player)) {
                return getInventory(TrinketsUtil.getUtilityBelt(player)).getStack(slot);
            }
        }

        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        SimplerInventory inv = getInventory(stack);

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack1 = inv.getStack(i);
            if (!stack1.isEmpty()) {
                tooltip.add(Text.literal("- ").append(stack1.getName()));
            }
        }
    }

    @Override
    public boolean onClickedOnOther(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack slotStack = slot.getStack();
            SimplerInventory inv = getInventory(stack);

            if (slotStack.isEmpty()) {
                playInsertSound(player);

                boolean inserted = false;

                for (int i = 0; i < inv.size(); i++) {
                    if (!inv.getStack(i).isEmpty()) {
                        ItemStack removed = inv.removeStack(i);
                        slot.insertStack(removed);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted)
                    return false;
            } else if (isValidItem(slotStack)) {
                boolean inserted = false;

                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStack(i).isEmpty()) {
                        inv.setStack(i, slotStack);
                        slot.setStack(ItemStack.EMPTY);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted)
                    return false;
            }

            playInsertSound(player);
            update(stack, inv);
            return true;
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player,
            StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            SimplerInventory inv = getInventory(stack);

            if (otherStack.isEmpty()) {
                boolean inserted = false;

                for (int i = 0; i < inv.size(); i++) {
                    if (!inv.getStack(i).isEmpty()) {
                        ItemStack removed = inv.removeStack(i);
                        cursorStackReference.set(removed);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted)
                    return false;
            } else {
                if (!isValidItem(otherStack))
                    return false;

                boolean inserted = false;

                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStack(i).isEmpty()) {
                        inv.setStack(i, otherStack);
                        cursorStackReference.set(ItemStack.EMPTY);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted)
                    return false;
            }

            playInsertSound(player);
            update(stack, inv);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.world.isClient && entity instanceof PlayerEntity player) {
            UtilityBeltInit.UTILITY_BELT_SELECTED.put(player.getUuid(), false);
            Networking.SET_UTILITY_BELT_SELECTED_S2C.send((ServerPlayerEntity) player,
                    (buf) -> buf.writeBoolean(false));
        }
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getInventory(entity.getStack()).getStacks().stream());
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getInventory(stack).getStacks().stream().anyMatch((s) -> !s.isEmpty());
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        long size = getInventory(stack).getStacks().stream().filter((s) -> !s.isEmpty()).count();

        return size == 4L ? 13 : (int) (size * 3);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }
}
