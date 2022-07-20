package io.github.jamalam360.tool.belt.gametest;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
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
            ItemStack stack = ItemRegistry.TOOL_BELT.getDefaultStack();

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

        if (!TrinketsApi.getTrinketComponent(player).get().isEquipped(ItemRegistry.TOOL_BELT)) {
            throw new IllegalStateException("Failed to equip toolbelt.");
        }

        FabricGameTest.super.invokeTestMethod(context, method);

        player = null;
    }

    @GameTest(structureName = FabricGameTest.EMPTY_STRUCTURE)
    public void testSwapIntoToolBelt(TestContext context) {
        assert player != null;

        PlayerInventory inv = player.getInventory();
        ItemStack stack = Items.DIAMOND_AXE.getDefaultStack();

        inv.setStack(0, stack);

        TestUtil.pressKeyBind(ToolBeltClientInit.SWAP_KEYBIND);

        context.addInstantFinalTask(() -> {
            TestUtil.assertEquals(ItemStack.EMPTY, inv.getStack(0));
            TestUtil.assertEquals(stack, ToolBeltItem.getInventory(TrinketsUtil.getToolBelt(player)).getStack(0));
        });
    }
}
