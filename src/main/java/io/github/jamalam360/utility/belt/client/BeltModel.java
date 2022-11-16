package io.github.jamalam360.utility.belt.client;

// Made with Blockbench 4.3.1
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;

public class BeltModel extends BipedEntityModel<LivingEntity> {
    public BeltModel(ModelPart root) {
        super(root);
        this.setVisible(false);
        this.body.visible = true;
    }

    public static TexturedModelData createTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 11.0F, -3.0F, 10.0F, 2.0F, 6.0F, new Dilation(0.0F))
                        .uv(0, 8).cuboid(-4.0F, 12.0F, -4.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
                        .uv(10, 8).cuboid(1.0F, 12.0F, -4.0F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, 0.0F, 0.0F));


        root.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);
        root.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create(), ModelTransform.NONE);

//        ModelPartData belt = body.addChild("belt", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 11.0F, -3.0F, 10.0F, 2.0F, 6.0F, new Dilation(0.0F))
//                .uv(0, 8).cuboid(-4.0F, 12.0F, -4.0F, 4.0F, 4.0F, 1.0F, new Dilation(0.0F))
//                .uv(10, 8).cuboid(1.0F, 12.0F, -4.0F, 3.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.rotation(0.0F, 0.0F, 0.0F));

        body.addChild("pouch", ModelPartBuilder.create().uv(18, 8).cuboid(0.0F, -12.0F, -6.0F, 2.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 24.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }
}
