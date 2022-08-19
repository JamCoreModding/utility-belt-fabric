package io.github.jamalam360.tool.belt.mixin.client.tutorial;

import io.github.jamalam360.tool.belt.ToolBeltInit;
import io.github.jamalam360.tool.belt.tutorial.Tutorial;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Jamalam
 */

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    @Inject(
            method = "onAdvancements",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/network/packet/s2c/play/AdvancementUpdateS2CPacket;getAdvancementsToProgress()Ljava/util/Map;"
                    )
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void toolbelt$tryTriggerTutorial(AdvancementUpdateS2CPacket packet, CallbackInfo ci, Iterator<Map.Entry<Identifier, AdvancementProgress>> var2, Map.Entry<Identifier, AdvancementProgress> entry, Advancement advancement, AdvancementProgress advancementProgress) {
        if (advancement.getId().equals(ToolBeltInit.idOf("obtain_tool_belt")) && !packet.shouldClearCurrent() && advancementProgress.isDone()) {
            Tutorial.startTutorial();
        }
    }
}
