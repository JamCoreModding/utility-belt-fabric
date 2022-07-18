package io.github.jamalam360.tool.belt.registry;

import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.item.ToolBeltItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

/**
 * @author Jamalam
 */

@ContentRegistry(ToolBeltInit.MOD_ID)
public class ItemRegistry {
    public static final Item TOOL_BELT = new ToolBeltItem(new Item.Settings().group(ItemGroup.TOOLS).maxCount(1));
}
