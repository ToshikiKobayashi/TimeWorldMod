package com.kotoshi.timeworldmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.CommandStorage;

import com.kotoshi.timeworldmod.TimeWorldMod;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class EvolutionClock extends Item {
	public EvolutionClock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			CommandStorage storage = serverLevel.getServer().getCommandStorage();
			ResourceLocation key = ResourceLocation.fromNamespaceAndPath("timeworldmod", "evolution_clock");
			CompoundTag data = storage.get(key);
			Integer evolutionType = 0;
			if (data != null) {
				evolutionType = data.getInt("evolutionType").orElse(TimeWorldMod.TIMEWORLD_TYPE_NORMAL);
			} else {
				data = new CompoundTag();
			}
			if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_NORMAL)) {
				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_PAST);
				player.displayClientMessage(
					Component.literal("過去の世界になりました"), true
				);
			} else if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_PAST)) {
				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_FUTURE);
				player.displayClientMessage(
					Component.literal("未来の世界になりました"), true
				);
			} else {
				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_NORMAL);
				player.displayClientMessage(
					Component.literal("世界が戻りました"), true
				);
			}
			storage.set(key, data);
		}
		return InteractionResult.SUCCESS;
	}
}



