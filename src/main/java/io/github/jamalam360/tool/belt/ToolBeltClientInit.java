package io.github.jamalam360.tool.belt;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.jamalam360.jamlib.config.JamLibConfig;
import io.github.jamalam360.tool.belt.config.ToolBeltConfig;
import io.github.jamalam360.tool.belt.screen.ToolBeltScreen;
import io.github.jamalam360.tool.belt.util.Ducks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBind;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

/**
 * @author Jamalam
 */
public class ToolBeltClientInit implements ClientModInitializer {
    public static final KeyBind TOOL_BELT_KEYBIND = KeyBindingHelper.registerKeyBinding(
            new KeyBind(
                    "key.toolbelt.tool_belt",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_T,
                    "category.toolbelt.tool_belt"
            )
    );

    @Override
    public void onInitializeClient() {
        JamLibConfig.init("toolbelt", ToolBeltConfig.class);

        HandledScreens.register(ToolBeltInit.TOOL_BELT_SCREEN_HANDLER, ToolBeltScreen::new);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen instanceof ToolBeltScreen) {
                if (((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getType() == InputUtil.Type.KEYSYM) {
                    TOOL_BELT_KEYBIND.setPressed(InputUtil.isKeyPressed(client.getWindow().getHandle(), ((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getKeyCode()));
                } else if (((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getType() == InputUtil.Type.MOUSE) {
                    TOOL_BELT_KEYBIND.setPressed(GLFW.glfwGetMouseButton(client.getWindow().getHandle(), ((Ducks.KeyBind) TOOL_BELT_KEYBIND).getBoundKey().getKeyCode()) == 1);
                }
            }

            if (TOOL_BELT_KEYBIND.isPressed() && client.currentScreen == null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(true);
                ClientPlayNetworking.send(ToolBeltInit.TOOL_BELT_SCREEN_NETWORK_ID, buf);
            } else if (!TOOL_BELT_KEYBIND.isPressed() && client.currentScreen instanceof ToolBeltScreen) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBoolean(false);
                ClientPlayNetworking.send(ToolBeltInit.TOOL_BELT_SCREEN_NETWORK_ID, buf);
            }
        });
    }
}
