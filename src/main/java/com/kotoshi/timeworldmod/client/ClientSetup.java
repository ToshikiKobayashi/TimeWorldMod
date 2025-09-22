package com.kotoshi.timeworldmod.client;

import com.kotoshi.timeworldmod.TimeWorldMod;
import com.kotoshi.timeworldmod.client.model.FutureZombieModel;
import com.kotoshi.timeworldmod.client.renderer.FutureZombieRenderer;
import com.kotoshi.timeworldmod.client.model.PastZombieModel;
import com.kotoshi.timeworldmod.client.renderer.PastZombieRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TimeWorldMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(TimeWorldMod.FUTURE_ZOMBIE.get(), FutureZombieRenderer::new);
        EntityRenderers.register(TimeWorldMod.PAST_ZOMBIE.get(), PastZombieRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FutureZombieModel.LAYER_LOCATION, () -> FutureZombieModel.createBodyLayer());
        event.registerLayerDefinition(PastZombieModel.LAYER_LOCATION, () -> PastZombieModel.createBodyLayer());
    }
} 