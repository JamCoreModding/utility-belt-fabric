package io.github.jamalam360.tool.belt.registry;

import io.github.jamalam360.jamlib.registry.annotation.ContentRegistry;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.screen.ToolBeltScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * @author Jamalam
 */

@ContentRegistry(ToolBeltInit.MOD_ID)
public class ScreenHandlerRegistry {
    public static final ScreenHandlerType<ToolBeltScreenHandler> SCREEN_HANDLER = new ScreenHandlerType<>(ToolBeltScreenHandler::new);
}
