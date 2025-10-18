package com.kotoshi.timeworldmod.client.renderer;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.model.FutureDragonModel;
import com.kotoshi.timeworldmod.entity.FutureDragonEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FutureDragonRenderer extends MobRenderer<FutureDragonEntity, FutureDragonRenderState, FutureDragonModel<FutureDragonEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "textures/entity/future_dragon.png");

    public FutureDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new FutureDragonModel(context.bakeLayer(FutureDragonModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(FutureDragonRenderState state) {
        return TEXTURE;
    }

    @Override
    public FutureDragonRenderState createRenderState() {
        return new FutureDragonRenderState();
    }
}
