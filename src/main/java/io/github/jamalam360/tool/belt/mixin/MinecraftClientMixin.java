package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam
 */

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(
            method = "doItemPick",
            at = @At("HEAD"),
            cancellable = true
    )
    public void toolbelt$disableMiddleClick(CallbackInfo ci) {
        if (ToolBeltClientInit.hasSwappedToToolBelt) {
            ci.cancel();
        }
    }
}
