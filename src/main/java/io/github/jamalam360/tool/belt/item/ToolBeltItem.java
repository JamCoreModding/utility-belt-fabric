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

package io.github.jamalam360.tool.belt.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Jamalam
 */
public class ToolBeltItem extends TrinketItem {
    public ToolBeltItem(Settings settings) {
        super(settings);
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
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
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

                if (!inserted) return false;
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

                if (!inserted) return false;
            }

            playInsertSound(player);
            update(stack, inv);
            return true;
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
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

                if (!inserted) return false;
            } else {
                if (!isValidItem(otherStack)) return false;

                boolean inserted = false;

                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStack(i).isEmpty()) {
                        inv.setStack(i, otherStack);
                        cursorStackReference.set(ItemStack.EMPTY);
                        inserted = true;
                        break;
                    }
                }

                if (!inserted) return false;
            }

            playInsertSound(player);
            update(stack, inv);
            return true;
        } else {
            return false;
        }
    }

    public static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
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
        return stack.getItem() instanceof ToolItem || stack.getItem() instanceof RangedWeaponItem || stack.getItem() instanceof FishingRodItem || stack.getItem() instanceof SpyglassItem || stack.getItem() instanceof TridentItem || stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof ShearsItem || stack.isEmpty() || stack.isIn(ToolBeltInit.ALLOWED_IN_TOOL_BELT);
    }

    public static ItemStack getSelectedToolBeltStack(PlayerEntity player) {
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

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof ClientPlayerEntity) {
            ToolBeltClientInit.hasSwappedToToolBelt = false;
        } else if (entity instanceof PlayerEntity player) {
            ToolBeltInit.TOOL_BELT_SELECTED.put(player, false);
        }
    }
}
