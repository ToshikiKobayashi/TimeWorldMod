package com.kotoshi.timeworldmod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class PastZombie extends Zombie {
    public PastZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, EntitySpawnReason spawnType) {
        // スーパークラスの条件をまず確認（通常のゾンビ条件）
        if (!super.checkSpawnRules(level, spawnType)) {
            return false;
        }

        BlockPos pos = this.blockPosition();
        BlockState blockBelow = level.getBlockState(pos.below());

        // 空中ならNG
        if (level.isEmptyBlock(pos.below())) {
            return false;
        }

        // マグマや溶岩ブロックの上ならNG
        if (blockBelow.getBlock() == Blocks.LAVA || blockBelow.getBlock() == Blocks.MAGMA_BLOCK) {
            return false;
        }

        // 水中スポーンも避けたい場合
        if (blockBelow.getFluidState().is(Fluids.WATER)) {
            return false;
        }

        // 天井が低すぎる場所も避ける
        if (!level.isEmptyBlock(pos.above())) {
            return false;
        }

        return true;
    }
} 