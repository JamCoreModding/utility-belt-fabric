package io.github.jamalam360.tool.belt.util;

import dev.emi.trinkets.api.TrinketsApi;
import io.github.jamalam360.tool.belt.registry.ItemRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * @author Jamalam
 */
public class TrinketsUtil {
    public static boolean hasToolBelt(PlayerEntity player) {
        final boolean[] result = new boolean[1];

        TrinketsApi.getTrinketComponent(player).ifPresentOrElse(
                component -> result[0] = component.getEquipped(ItemRegistry.TOOL_BELT).size() > 0,
                () -> result[0] = false
        );

        return result[0];
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static ItemStack getToolBelt(PlayerEntity player) {
        return TrinketsApi.getTrinketComponent(player).get().getEquipped(ItemRegistry.TOOL_BELT).get(0).getRight();
    }
}
