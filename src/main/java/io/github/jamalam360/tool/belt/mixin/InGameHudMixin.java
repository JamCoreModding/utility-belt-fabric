package io.github.jamalam360.tool.belt.mixin;

import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Jamalam
 */

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    ordinal = 1
            )
    )
    public void toolbelt$disableHotbarHighlight(InGameHud instance, MatrixStack matrixStack, int a, int b, int c, int d, int e, int f) {
        if (!ToolBeltClientInit.hasSwappedToToolBelt) {
            instance.drawTexture(matrixStack, a, b, c, d, e, f);
        }
    }
}
