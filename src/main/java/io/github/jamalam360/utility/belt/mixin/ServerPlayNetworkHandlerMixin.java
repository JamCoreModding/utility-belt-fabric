package io.github.jamalam360.utility.belt.mixin;

import io.github.jamalam360.utility.belt.UtilityBeltInit;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow public ServerPlayerEntity player;

	@Inject(
			method = "onPlayerAction",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	private void utilitybelt$preventOffhandSwapping(PlayerActionC2SPacket packet, CallbackInfo ci) {
		if (UtilityBeltInit.UTILITY_BELT_SELECTED.getOrDefault(this.player.getUuid(), false)) {
			ci.cancel();
		}
	}
}
