package com.kotoshi.timeworldmod.client.renderer;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.model.PastZombieModel;
import com.kotoshi.timeworldmod.entity.PastZombie;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PastZombieRenderer extends MobRenderer<PastZombie, PastZombieRenderState, PastZombieModel<PastZombie>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.tryParse(TimeWorldMod.MODID + ":textures/entity/past_zombie.png");

    public PastZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new PastZombieModel(context.bakeLayer(PastZombieModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PastZombieRenderState state) {
        return TEXTURE;
    }

    @Override
    public PastZombieRenderState createRenderState() {
        return new PastZombieRenderState();
    }
}
