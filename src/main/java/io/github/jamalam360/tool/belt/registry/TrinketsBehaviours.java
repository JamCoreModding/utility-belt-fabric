package io.github.jamalam360.tool.belt.registry;

import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author Jamalam
 */
public class TrinketsBehaviours {
    public static void registerEvents() {
        TrinketsApi.registerTrinketPredicate(ToolBeltInit.idOf("only_one_tool_belt"), (stack, slot, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem && entity instanceof PlayerEntity player) {
                return TrinketsUtil.hasToolBelt(player) ? TriState.FALSE : TriState.TRUE;
            } else {
                return TriState.DEFAULT;
            }
        });

        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (stack.getItem() instanceof ToolBeltItem) {
                SimplerInventory inv = ToolBeltItem.getInventory(stack);

                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStack(i).isEmpty()) {
                        ItemEntity item = EntityType.ITEM.create(entity.world);
                        item.refreshPositionAfterTeleport(entity.getPos());
                        item.setStack(inv.getStack(i));
                        entity.world.spawnEntity(item);
                    }
                }

                inv.clear();
                ToolBeltItem.update(stack, inv);

                return TrinketEnums.DropRule.DROP;
            } else {
                return TrinketEnums.DropRule.DEFAULT;
            }
        });
    }
}
