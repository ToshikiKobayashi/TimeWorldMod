package com.kotoshi.timeworldmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.CommandStorage;

import java.util.EnumSet;
import java.util.Set;

import com.kotoshi.timeworldmod.TimeWorldMod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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
			MinecraftServer server = serverLevel.getServer();
			ResourceKey<Level> targetDim;
			ServerLevel targetLevel;

			if (data != null) {
				evolutionType = data.getInt("evolutionType").orElse(TimeWorldMod.TIMEWORLD_TYPE_NORMAL);
			} else {
				data = new CompoundTag();
			}
			playPortalEffect(level, player.blockPosition());
			if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_NORMAL)) {
				targetDim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("timeworldmod", "future_dimension"));
        		targetLevel = server.getLevel(targetDim);
			
				if (targetLevel != null) {
					// プレイヤーがサーバー側のインスタンスなら転送可能
					if (player instanceof ServerPlayer serverPlayer) {
						BlockPos spawnPos = new BlockPos(serverPlayer.getBlockX(), 65, serverPlayer.getBlockZ());
						BlockPos safePos = findSafeLocation(targetLevel, spawnPos);
						Set<Relative> relatives = EnumSet.noneOf(Relative.class);

						if (safePos != null) {
							// ディメンション間テレポート（1.21対応）
							serverPlayer.teleportTo(
								targetLevel,
								safePos.getX() + 0.5,
								safePos.getY(),
								safePos.getZ() + 0.5,
								relatives,
								serverPlayer.getYRot(),
								serverPlayer.getXRot(),
								true
							);
						}
						playPortalEffect(targetLevel, safePos);
					}
				}

				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_PAST);
				player.displayClientMessage(
					Component.literal("未来の世界になりました"), true
				);
			} else if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_PAST)) {
				targetDim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("timeworldmod", "past_dimension"));
        		targetLevel = server.getLevel(targetDim);

				if (targetLevel != null) {
					// プレイヤーがサーバー側のインスタンスなら転送可能
					if (player instanceof ServerPlayer serverPlayer) {
						BlockPos spawnPos = new BlockPos(serverPlayer.getBlockX(), 80, serverPlayer.getBlockZ());
						BlockPos safePos = findSafeLocation(targetLevel, spawnPos);
						Set<Relative> relatives = EnumSet.noneOf(Relative.class);

						if (safePos != null) {
							// ディメンション間テレポート（1.21対応）
							serverPlayer.teleportTo(
								targetLevel,
								safePos.getX() + 0.5,
								safePos.getY(),
								safePos.getZ() + 0.5,
								relatives,
								serverPlayer.getYRot(),
								serverPlayer.getXRot(),
								true
							);
						}
						playPortalEffect(targetLevel, safePos);
					}
				}

				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_FUTURE);
				player.displayClientMessage(
					Component.literal("過去の世界になりました"), true
				);
			} else {
				targetDim = Level.OVERWORLD;
				targetLevel = server.getLevel(targetDim);
				if (targetLevel != null) {
					
					// プレイヤーがサーバー側のインスタンスなら転送可能
					if (player instanceof ServerPlayer serverPlayer) {
						BlockPos spawnPos = new BlockPos(serverPlayer.getBlockX(), 80, serverPlayer.getBlockZ());
						BlockPos safePos = findSafeLocation(targetLevel, spawnPos);
						Set<Relative> relatives = EnumSet.noneOf(Relative.class);

						if (safePos != null) {
							// ディメンション間テレポート（1.21対応）
							serverPlayer.teleportTo(
								targetLevel,
								safePos.getX() + 0.5,
								safePos.getY(),
								safePos.getZ() + 0.5,
								relatives,
								serverPlayer.getYRot(),
								serverPlayer.getXRot(),
								true
							);
						}
						playPortalEffect(targetLevel, safePos);
					}
				}
				data.putInt("evolutionType", TimeWorldMod.TIMEWORLD_TYPE_NORMAL);
				player.displayClientMessage(
					Component.literal("オーバーワールドに戻りました"), true
				);
			}
			storage.set(key, data);
		}
		return InteractionResult.SUCCESS;
	}

    /** 安全な場所を探索して返す */
    private BlockPos findSafeLocation(ServerLevel level, BlockPos targetPos) {
        int searchRadius = 15;

        for (int dy = -searchRadius; dy <= searchRadius; dy++) {
			for (int dx = -searchRadius; dx <= searchRadius; dx++) {
				BlockPos checkPos = targetPos.offset(dx * 10, dy, 0);
				BlockState below = level.getBlockState(checkPos.below());
				BlockState at = level.getBlockState(checkPos);
				BlockState above = level.getBlockState(checkPos.above());

				// 空中はNG
				if (level.isEmptyBlock(checkPos.below())) continue;

				// 溶岩や水の上もNG
				if (below.is(Blocks.LAVA) || below.is(Blocks.MAGMA_BLOCK) ||
					below.getFluidState().is(Fluids.LAVA) || below.getFluidState().is(Fluids.WATER))
					continue;

				// 埋まる・天井が低い場所はNG
				if (!level.isEmptyBlock(checkPos) || !level.isEmptyBlock(checkPos.above()))
					continue;

				return checkPos;
			}
        }

        return null;
    }

	/**
	 * ネザーゲート風のパーティクルと音を出す
	 */
	private void playPortalEffect(Level level, BlockPos pos) {
		if (level.isClientSide) return;

		// ネザーのポータル音
		level.playSound(null, pos, SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0F, 1.0F);

		// 紫もやを強化（通常より密度高め）
		if (level instanceof ServerLevel serverLevel) {
			for (int i = 0; i < 200; i++) { // 数を増やすと濃くなる
				double offsetX = (serverLevel.random.nextDouble() - 0.5) * 4.0;
				double offsetY = (serverLevel.random.nextDouble() - 0.5) * 4.0;
				double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 4.0;
				serverLevel.sendParticles(
					ParticleTypes.REVERSE_PORTAL, // ネザーより幻想的なもや
					pos.getX() + 0.5 + offsetX,
					pos.getY() + 1.0 + offsetY,
					pos.getZ() + 0.5 + offsetZ,
					1, 0, 0, 0, 0
				);
			}
		}
	}
}



