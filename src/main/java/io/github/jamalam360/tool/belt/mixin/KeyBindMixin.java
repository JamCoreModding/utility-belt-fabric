package io.github.jamalam360.tool.belt.mixin;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.jamalam360.tool.belt.util.Ducks;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Jamalam
 */

@Mixin(KeyBind.class)
public class KeyBindMixin implements Ducks.KeyBind {
    @Shadow
    private InputUtil.Key boundKey;

    @Override
    public InputUtil.Key getBoundKey() {
        return this.boundKey;
    }
}
