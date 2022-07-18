package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import io.github.jamalam360.tool.belt.ToolBeltInit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Mouse;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author Jamalam
 */

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            cancellable = true
    )
    private void toolbelt$beforeMouseScrollEvent(long window, double scrollDeltaX, double scrollDeltaY, CallbackInfo ci, double amount) {
        if (ToolBeltClientInit.hasSwappedToToolBelt) {
            if (amount > 0) {
                ToolBeltClientInit.toolBeltSelectedSlot--;
                if (ToolBeltClientInit.toolBeltSelectedSlot < 0) {
                    ToolBeltClientInit.toolBeltSelectedSlot = 3;
                }
            } else if (amount < 0) {
                ToolBeltClientInit.toolBeltSelectedSlot++;
                if (ToolBeltClientInit.toolBeltSelectedSlot >= 4) {
                    ToolBeltClientInit.toolBeltSelectedSlot = 0;
                }
            }

            if (amount != 0) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(ToolBeltClientInit.toolBeltSelectedSlot);
                ClientPlayNetworking.send(ToolBeltInit.SYNC_SELECTED_SLOT, buf);
            }

            ci.cancel();
        }
    }
}
