package io.github.jamalam360.utility.belt.mixin;

import io.github.jamalam360.utility.belt.client.tutorial.MineBlockUsingPickaxeInBeltStage;
import io.github.jamalam360.utility.belt.registry.UtilityBeltTutorial;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void utilitybelt$triggerPickaxeTutorialStage(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof PickaxeItem && UtilityBeltTutorial.TUTORIAL.getCurrentStage() instanceof MineBlockUsingPickaxeInBeltStage) {
            UtilityBeltTutorial.TUTORIAL.advanceStage();
        }
    }
}
