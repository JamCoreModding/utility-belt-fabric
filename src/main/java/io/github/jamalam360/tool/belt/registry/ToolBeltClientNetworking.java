package io.github.jamalam360.tool.belt.registry;

import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;
import io.github.jamalam360.tool.belt.Ducks;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import net.minecraft.util.Hand;

/**
 * @author Jamalam
 */
public class ToolBeltClientNetworking {
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected"));
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED_SLOT = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected_slot"));

    public static void setHandlers() {
        ToolBeltNetworking.SWING_HAND.setHandler((client, handler, buf, responseSender) -> client.player.swingHand(Hand.MAIN_HAND));
    }
}
