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

package io.github.jamalam360.utility.belt.gametest;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.utility.belt.UtilityBeltClientInit;
import io.github.jamalam360.utility.belt.item.UtilityBeltItem;
import io.github.jamalam360.utility.belt.registry.ItemRegistry;
import io.github.jamalam360.utility.belt.util.TrinketsUtil;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.lang.reflect.Method;

/**
 * @author Jamalam
 */
public class SlotSwapTests implements FabricGameTest {
    private PlayerEntity player;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public void invokeTestMethod(TestContext context, Method method) {
        player = context.createMockPlayer();
        TrinketsApi.getTrinketComponent(player).ifPresentOrElse((component) -> {
            ItemStack stack = ItemRegistry.UTILITY_BELT.getDefaultStack();

            for (var group : component.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (inv.getStack(i).isEmpty()) {
                            SlotReference ref = new SlotReference(inv, i);
                            if (TrinketSlot.canInsert(stack, ref, player)) {
                                ItemStack newStack = stack.copy();
                                inv.setStack(i, newStack);
                                stack.setCount(0);
                            }
                        }
                    }
                }
            }
        }, () -> {
            throw new IllegalStateException("Player has no trinket component.");
        });

        if (!TrinketsApi.getTrinketComponent(player).get().isEquipped(ItemRegistry.UTILITY_BELT)) {
            throw new IllegalStateException("Failed to equip utility belt.");
        }

        FabricGameTest.super.invokeTestMethod(context, method);

        player = null;
    }

    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSwapIntoUtilityBelt(TestContext context) {
        assert player != null;

        PlayerInventory inv = player.getInventory();
        ItemStack stack = Items.DIAMOND_AXE.getDefaultStack();

        inv.setStack(0, stack);

        TestUtil.pressKeyBind(UtilityBeltClientInit.SWAP_KEYBIND_TOGGLE);

        context.addInstantFinalTask(() -> {
            TestUtil.assertEquals(ItemStack.EMPTY, inv.getStack(0));
            TestUtil.assertEquals(stack, UtilityBeltItem.getInventory(TrinketsUtil.getUtilityBelt(player)).getStack(0));
        });
    }
}
