package com.kotoshi.timeworldmod.world;

import com.kotoshi.timeworldmod.TimeWorldMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.nbt.CompoundTag;

@Mod.EventBusSubscriber(modid = TimeWorldMod.MODID)
public class PlayerWeakenScaler {
	private static final ResourceLocation SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "player_speed_weaken");
	private static final ResourceLocation DAMAGE_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "player_damage_weaken");
	private static final ResourceLocation HEALTH_MOD_ID = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "player_health_weaken");

	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		Level level = event.level;
		if (level.isClientSide()) return;

		// Check toggle
		CommandStorage storage = level.getServer().getCommandStorage();
		ResourceLocation key = ResourceLocation.fromNamespaceAndPath(TimeWorldMod.MODID, "weaken_clock");
		CompoundTag data = storage.get(key);
		boolean weakeningEnabled = false;
		if (data != null) {
			weakeningEnabled = data.getBoolean("weakeningEnabled").orElse(false);
		}
		if (!weakeningEnabled) return;

		long day = level.getDayTime() / 24000L;
		// Start weakening from day 1; cap at -80%
		double scale = Math.max(1.0 - Math.min(day * 0.05, 0.8), 0.2); // 5% per day down to 20%
		double addMult = scale - 1.0; // negative for weakening

		for (Player player : level.players()) {
			applyOrUpdateMultiplier(player, Attributes.MOVEMENT_SPEED, SPEED_MOD_ID, addMult);
			applyOrUpdateMultiplier(player, Attributes.ATTACK_DAMAGE, DAMAGE_MOD_ID, addMult);
			applyOrUpdateMultiplier(player, Attributes.MAX_HEALTH, HEALTH_MOD_ID, addMult);
			// Ensure current health not above new max
			player.setHealth(player.getHealth() > player.getMaxHealth() ? player.getMaxHealth() : player.getHealth());
		}
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



