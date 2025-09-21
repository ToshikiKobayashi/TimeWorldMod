package com.kotoshi.timeworldmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class WeakenClock extends Item {
	public WeakenClock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			CommandStorage storage = serverLevel.getServer().getCommandStorage();
			ResourceLocation key = ResourceLocation.fromNamespaceAndPath("timeworldmod", "weaken_clock");
			CompoundTag data = storage.get(key);
			boolean weakeningEnabled = false;
			if (data != null) {
				weakeningEnabled = data.getBoolean("weakeningEnabled").orElse(false);
			} else {
				data = new CompoundTag();
			}
			if (weakeningEnabled) {
				data.putBoolean("weakeningEnabled", false);
				player.displayClientMessage(
					Component.literal("弱体化が停止しました"), true
				);
			} else {
				data.putBoolean("weakeningEnabled", true);
				long currentDay = serverLevel.getDayTime() / 24000L;
				player.displayClientMessage(
					Component.literal("弱体化が開始しました（開始時間：" + currentDay + "日）"), true
				);
			}
			storage.set(key, data);
		}
		return InteractionResult.SUCCESS;
	}
}



