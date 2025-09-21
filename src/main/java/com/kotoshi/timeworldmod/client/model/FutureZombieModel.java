package com.kotoshi.timeworldmod.client.model;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.renderer.FutureZombieRenderState;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class FutureZombieModel<T extends Entity> extends EntityModel<FutureZombieRenderState> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryParse(TimeWorldMod.MODID + ":future_zombie"), "main");
    private final ModelPart bipedHead;
    private final ModelPart bipedBody;
    private final ModelPart bipedRightArm;
    private final ModelPart bipedLeftArm;
    private final ModelPart bipedRightLeg;
    private final ModelPart bipedLeftLeg;

    public FutureZombieModel(ModelPart root) {
        super(root);
        this.bipedHead = root.getChild("bipedHead");
        this.bipedBody = root.getChild("bipedBody");
        this.bipedRightArm = root.getChild("bipedRightArm");
        this.bipedLeftArm = root.getChild("bipedLeftArm");
        this.bipedRightLeg = root.getChild("bipedRightLeg");
        this.bipedLeftLeg = root.getChild("bipedLeftLeg");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bipedHead = partdefinition.addOrReplaceChild("bipedHead", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bipedBody = partdefinition.addOrReplaceChild("bipedBody", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition bipedRightArm = partdefinition.addOrReplaceChild("bipedRightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition bipedLeftArm = partdefinition.addOrReplaceChild("bipedLeftArm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition bipedRightLeg = partdefinition.addOrReplaceChild("bipedRightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition bipedLeftLeg = partdefinition.addOrReplaceChild("bipedLeftLeg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(FutureZombieRenderState state) {
        float headRotationSmoothing = 0.5F;

        // 基本の頭回転
        float baseXRot = state.xRot * headRotationSmoothing;
        float baseYRot = state.yRot * 0.75F;
    
        // ageInTicks を使った自然な微揺れ（小さく制限）
        float headBobSpeed = 20.0F;       // 揺れ周期
        float headBobAmplitudeX = 0.02F;  // 上下の揺れ幅
        float headBobAmplitudeY = 0.015F; // 左右の揺れ幅
    
        float bobX = Mth.cos(state.ageInTicks / headBobSpeed) * headBobAmplitudeX;
        float bobY = Mth.cos(state.ageInTicks / headBobSpeed + (float)Math.PI / 2) * headBobAmplitudeY;
    
        // 最終角度（累積ではなく clamp で制限も可能）
        this.bipedHead.xRot = baseXRot + bobX;
        this.bipedHead.yRot = baseYRot + bobY;
    
        // optional: 角度制限
        this.bipedHead.xRot = Mth.clamp(this.bipedHead.xRot, -0.5F, 0.5F); // 上下約±28°
        this.bipedHead.yRot = Mth.clamp(this.bipedHead.yRot, -0.5F, 0.5F); // 左右約±28°

        // 腕と脚の歩行アニメーション
        float swing = state.walkAnimationPos;
        float swingAmount = state.walkAnimationSpeed;
    
        this.bipedRightArm.xRot = Mth.cos(swing * 0.6662F + (float)Math.PI) * 2.0F * swingAmount * 0.5F;
        this.bipedLeftArm.xRot  = Mth.cos(swing * 0.6662F) * 2.0F * swingAmount * 0.5F;
    
        this.bipedRightLeg.xRot = Mth.cos(swing * 0.6662F) * 1.4F * swingAmount;
        this.bipedLeftLeg.xRot  = Mth.cos(swing * 0.6662F + (float)Math.PI) * 1.4F * swingAmount;
    }
}