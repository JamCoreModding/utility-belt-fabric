package io.github.jamalam360.tool.belt.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.jamalam360.tool.belt.ToolBeltClientInit;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Jamalam
 */

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    public ClientPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable PlayerPublicKey playerPublicKey) {
        super(world, blockPos, f, gameProfile, playerPublicKey);
    }

    @Inject(
            method = "updatePostDeath",
            at = @At("HEAD")
    )
    private void toolbelt$switchBackToHotbar(CallbackInfo ci) {
        if (this.deathTime == 20) {
            ToolBeltClientInit.hasSwappedToToolBelt = false;
        }
    }
}
