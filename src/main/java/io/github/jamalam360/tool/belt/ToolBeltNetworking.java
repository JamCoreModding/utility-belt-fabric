package io.github.jamalam360.tool.belt;

import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import io.github.jamalam360.tool.belt.util.SimplerInventory;
import io.github.jamalam360.tool.belt.util.TrinketsUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * @author Jamalam
 */
public class ToolBeltNetworking {
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected"));
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED_SLOT = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected_slot"));

    public static void registerHandlers() {
        ToolBeltNetworking.SET_TOOL_BELT_SELECTED_SLOT.registerHandler((server, player, handler, buf, responseSender) -> ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.put(player, buf.readInt()));

        ToolBeltNetworking.SET_TOOL_BELT_SELECTED.registerHandler((server, player, handler, buf, responseSender) -> {
            boolean hasSwappedToToolBelt = buf.readBoolean();

            if (player.isSneaking()) {
                if (TrinketsUtil.hasToolBelt(player)) {
                    ItemStack toolBelt = TrinketsUtil.getToolBelt(player);
                    SimplerInventory inventory = ToolBeltItem.getInventory(toolBelt);

                    ItemStack playerStack = player.getEquippedStack(EquipmentSlot.MAINHAND);

                    if (!ToolBeltItem.isValidItem(playerStack)) return;

                    int index = ToolBeltInit.TOOL_BELT_SELECTED_SLOTS.getOrDefault(player, 0);

                    if (hasSwappedToToolBelt && !inventory.getStack(index).isEmpty()) {
                        for (int i = 0; i < inventory.size(); i++) {
                            if (inventory.getStack(i).isEmpty()) {
                                index = i;
                                break;
                            }
                        }
                    }

                    ItemStack toolBeltStack = inventory.getStack(index);

                    inventory.setStack(index, playerStack);
                    player.getInventory().main.set(player.getInventory().selectedSlot, toolBeltStack);

                    ToolBeltItem.update(toolBelt, inventory);
                }

            }

            ToolBeltInit.TOOL_BELT_SELECTED.put(player, hasSwappedToToolBelt);
        });
    }
}
