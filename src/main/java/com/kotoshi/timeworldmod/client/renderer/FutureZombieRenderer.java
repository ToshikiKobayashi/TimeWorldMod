package com.kotoshi.timeworldmod.client.renderer;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.model.FutureZombieModel;
import com.kotoshi.timeworldmod.entity.FutureZombie;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FutureZombieRenderer extends MobRenderer<FutureZombie, FutureZombieRenderState, FutureZombieModel<FutureZombie>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.tryParse(TimeWorldMod.MODID + ":textures/entity/future_zombie.png");

    public FutureZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new FutureZombieModel(context.bakeLayer(FutureZombieModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(FutureZombieRenderState state) {
        return TEXTURE;
    }

    @Override
    public FutureZombieRenderState createRenderState() {
        return new FutureZombieRenderState();
    }
}
