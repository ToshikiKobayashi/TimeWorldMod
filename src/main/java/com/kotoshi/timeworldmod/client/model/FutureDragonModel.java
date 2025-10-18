package com.kotoshi.timeworldmod.client.model;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.renderer.FutureDragonRenderState;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class FutureDragonModel<T extends Entity> extends EntityModel<FutureDragonRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.tryParse(TimeWorldMod.MODID + ":entity/future_dragon"), "main");
    private static final int NECK_PART_COUNT = 5;
    private static final int TAIL_PART_COUNT = 12;
    private final ModelPart head;
    private final ModelPart[] neckParts = new ModelPart[5];
    private final ModelPart[] tailParts = new ModelPart[12];
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart leftWingTip;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftFrontLegTip;
    private final ModelPart leftFrontFoot;
    private final ModelPart leftRearLeg;
    private final ModelPart leftRearLegTip;
    private final ModelPart leftRearFoot;
    private final ModelPart rightWing;
    private final ModelPart rightWingTip;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightFrontLegTip;
    private final ModelPart rightFrontFoot;
    private final ModelPart rightRearLeg;
    private final ModelPart rightRearLegTip;
    private final ModelPart rightRearFoot;

    private static String neckName(int p_367970_) {
        return "neck" + p_367970_;
    }

    private static String tailName(int p_361223_) {
        return "tail" + p_361223_;
    }

    public FutureDragonModel(ModelPart p_364243_) {
        super(p_364243_);
        this.head = p_364243_.getChild("head");
        this.jaw = this.head.getChild("jaw");

        for (int i = 0; i < this.neckParts.length; i++) {
            this.neckParts[i] = p_364243_.getChild(neckName(i));
        }

        for (int j = 0; j < this.tailParts.length; j++) {
            this.tailParts[j] = p_364243_.getChild(tailName(j));
        }

        this.body = p_364243_.getChild("body");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
        this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
        this.leftRearLeg = this.body.getChild("left_hind_leg");
        this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
        this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
        this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
        this.rightRearLeg = this.body.getChild("right_hind_leg");
        this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
        this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        float f = -16.0F;
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
                .addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
                .addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
                .mirror()
                .addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
                .addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
                .mirror()
                .addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
                .addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
            PartPose.offset(0.0F, 20.0F, -62.0F)
        );
        partdefinition1.addOrReplaceChild(
            "jaw", CubeListBuilder.create().addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65), PartPose.offset(0.0F, 4.0F, -8.0F)
        );
        CubeListBuilder cubelistbuilder = CubeListBuilder.create()
            .addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104)
            .addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0);

        for (int i = 0; i < 5; i++) {
            partdefinition.addOrReplaceChild(neckName(i), cubelistbuilder, PartPose.offset(0.0F, 20.0F, -12.0F - i * 10.0F));
        }

        for (int j = 0; j < 12; j++) {
            partdefinition.addOrReplaceChild(tailName(j), cubelistbuilder, PartPose.offset(0.0F, 10.0F, 60.0F + j * 10.0F));
        }

        PartDefinition partdefinition12 = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
                .addBox("body", -12.0F, 1.0F, -16.0F, 24, 24, 64, 0, 0)
                .addBox("scale", -1.0F, -5.0F, -10.0F, 2, 6, 12, 220, 53)
                .addBox("scale", -1.0F, -5.0F, 10.0F, 2, 6, 12, 220, 53)
                .addBox("scale", -1.0F, -5.0F, 30.0F, 2, 6, 12, 220, 53),
            PartPose.offset(0.0F, 3.0F, 8.0F)
        );
        PartDefinition partdefinition2 = partdefinition12.addOrReplaceChild(
            "left_wing",
            CubeListBuilder.create()
                .mirror()
                .addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88)
                .addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
            PartPose.offset(12.0F, 2.0F, -6.0F)
        );
        partdefinition2.addOrReplaceChild(
            "left_wing_tip",
            CubeListBuilder.create()
                .mirror()
                .addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136)
                .addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
            PartPose.offset(56.0F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition3 = partdefinition12.addOrReplaceChild(
            "left_front_leg",
            CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
            PartPose.offsetAndRotation(12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition4 = partdefinition3.addOrReplaceChild(
            "left_front_leg_tip",
            CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
            PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F)
        );
        partdefinition4.addOrReplaceChild(
            "left_front_foot",
            CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
            PartPose.offsetAndRotation(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition5 = partdefinition12.addOrReplaceChild(
            "left_hind_leg",
            CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
            PartPose.offsetAndRotation(16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition6 = partdefinition5.addOrReplaceChild(
            "left_hind_leg_tip",
            CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
            PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F)
        );
        partdefinition6.addOrReplaceChild(
            "left_hind_foot",
            CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
            PartPose.offsetAndRotation(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition7 = partdefinition12.addOrReplaceChild(
            "right_wing",
            CubeListBuilder.create().addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
            PartPose.offset(-12.0F, 2.0F, -6.0F)
        );
        partdefinition7.addOrReplaceChild(
            "right_wing_tip",
            CubeListBuilder.create().addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
            PartPose.offset(-56.0F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition8 = partdefinition12.addOrReplaceChild(
            "right_front_leg",
            CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
            PartPose.offsetAndRotation(-12.0F, 17.0F, -6.0F, 1.3F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition9 = partdefinition8.addOrReplaceChild(
            "right_front_leg_tip",
            CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
            PartPose.offsetAndRotation(0.0F, 20.0F, -1.0F, -0.5F, 0.0F, 0.0F)
        );
        partdefinition9.addOrReplaceChild(
            "right_front_foot",
            CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
            PartPose.offsetAndRotation(0.0F, 23.0F, 0.0F, 0.75F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition10 = partdefinition12.addOrReplaceChild(
            "right_hind_leg",
            CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
            PartPose.offsetAndRotation(-16.0F, 13.0F, 34.0F, 1.0F, 0.0F, 0.0F)
        );
        PartDefinition partdefinition11 = partdefinition10.addOrReplaceChild(
            "right_hind_leg_tip",
            CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
            PartPose.offsetAndRotation(0.0F, 32.0F, -4.0F, 0.5F, 0.0F, 0.0F)
        );
        partdefinition11.addOrReplaceChild(
            "right_hind_foot",
            CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
            PartPose.offsetAndRotation(0.0F, 31.0F, 4.0F, 0.75F, 0.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(FutureDragonRenderState p_369164_) {
        float f = p_369164_.ageInTicks * 0.2F; // アニメーションタイム
        float flap = (float)Math.sin(f) * 0.2F;
    
        // 顎を開閉
        this.jaw.xRot = flap;
    
        // 首を揺らす
        for (int i = 0; i < neckParts.length; i++) {
            neckParts[i].xRot = flap * 0.5F + i * 0.05F;
            neckParts[i].yRot = (float)Math.sin(f + i) * 0.1F;
            neckParts[i].zRot = (float)Math.cos(f + i) * 0.05F;
        }
    
        // 尾を揺らす
        float tailSwing = 0.0F;
        for (int i = 0; i < tailParts.length; i++) {
            tailSwing += Math.sin(f + i * 0.2F) * 0.05F;
            tailParts[i].xRot = tailSwing;
            tailParts[i].yRot = (float) (Math.sin(f + i * 0.1F) * 0.2F);
            tailParts[i].zRot = (float) Math.cos(f + i * 0.1F) * 0.1F;
        }
    
        // 翼を羽ばたかせる
        leftWing.xRot = 0.125F - flap;
        leftWing.yRot = -0.25F;
        leftWing.zRot = -flap * 2.0F;
        leftWingTip.zRot = flap * 1.5F;
    
        rightWing.xRot = leftWing.xRot;
        rightWing.yRot = -leftWing.yRot;
        rightWing.zRot = -leftWing.zRot;
        rightWingTip.zRot = -leftWingTip.zRot;
    
        // 脚を少し動かす
        poseLimb(leftFrontLeg, leftFrontLegTip, leftFrontFoot, flap);
        poseLimb(rightFrontLeg, rightFrontLegTip, rightFrontFoot, flap);
        poseLimb(leftRearLeg, leftRearLegTip, leftRearFoot, flap);
        poseLimb(rightRearLeg, rightRearLegTip, rightRearFoot, flap);
    }
    
    // 脚アニメーション簡易版
    private void poseLimb(ModelPart upper, ModelPart lower, ModelPart foot, float anim) {
        upper.xRot = 1.0F + anim * 0.1F;
        lower.xRot = 0.5F + anim * 0.1F;
        foot.xRot = 0.75F + anim * 0.1F;
    }
}