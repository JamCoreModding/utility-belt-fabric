package io.github.jamalam360.tool.belt;

import io.github.jamalam360.jamlib.network.JamLibC2SNetworkChannel;

/**
 * @author Jamalam
 */
public class ToolBeltNetworking {
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected"));
    public static final JamLibC2SNetworkChannel SET_TOOL_BELT_SELECTED_SLOT = new JamLibC2SNetworkChannel(ToolBeltInit.idOf("set_tool_belt_selected_slot"));
}
