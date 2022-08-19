package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltInit;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam
 */

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(
            method = "remove",
            at = @At("HEAD")
    )
    private void toolbelt$switchBackToHotbar(CallbackInfo ci) {
        ToolBeltInit.TOOL_BELT_SELECTED.put((PlayerEntity) (Object) this, false);
    }
}
