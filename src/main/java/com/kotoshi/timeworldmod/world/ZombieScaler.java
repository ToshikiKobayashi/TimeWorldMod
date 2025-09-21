package com.kotoshi.timeworldmod.world;

import com.kotoshi.timeworldmod.TimeWorldMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.nbt.CompoundTag;

@Mod.EventBusSubscriber(modid = TimeWorldMod.MODID)
public class ZombieScaler {
    private static final ResourceLocation SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "zombie_speed_scale");
    private static final ResourceLocation DAMAGE_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "zombie_damage_scale");
    private static final ResourceLocation HEALTH_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "zombie_health_scale");

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (!(entity instanceof Zombie zombie)) return;

        // Check power stone usage gate
        var storage = event.getLevel().getServer().getCommandStorage();
        var key = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "power_stone");
        CompoundTag state = storage.get(key);
        boolean powerUsed = false;
        if (state != null) {
            powerUsed = state.getBoolean("powerUsed").orElse(false);
        }
        if (!powerUsed) return;

        long day = event.getLevel().getDayTime() / 24000L;
        if (day <= 0) return;

        double scale = 1.0 + Math.min(day * 0.05, 4.0); // +5%/day, cap at +400%
        double addMult = scale - 1.0; // for ADD_MULTIPLIED_BASE

        applyOrUpdateMultiplier(zombie, Attributes.MOVEMENT_SPEED, SPEED_MOD_ID, addMult);
        applyOrUpdateMultiplier(zombie, Attributes.ATTACK_DAMAGE, DAMAGE_MOD_ID, addMult);
        applyOrUpdateMultiplier(zombie, Attributes.MAX_HEALTH, HEALTH_MOD_ID, addMult);

        // Ensure current health does not exceed new max; then heal to max for immediate effect
        LivingEntity le = zombie;
        le.setHealth(le.getMaxHealth());
    }

    private static void applyOrUpdateMultiplier(LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, ResourceLocation id, double addMult) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;

        if (instance.hasModifier(id)) {
            instance.removeModifier(id);
        }

        if (addMult != 0.0) {
            AttributeModifier modifier = new AttributeModifier(id, addMult, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            instance.addTransientModifier(modifier);
        }
    }
}


