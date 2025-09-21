package com.kotoshi.timeworldmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class NormalClock extends Item {
    public NormalClock(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            if (level instanceof ServerLevel serverLevel) {
                double centerX = player.getX();
                double centerY = player.getY() + 0.1; // 少し浮かせる
                double centerZ = player.getZ();
            
                double radius = 3.0; // 円の半径
                int points = 240;     // パーティクルの数
            
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points; // ラジアンに変換
                    double x = centerX + Math.cos(angle) * radius;
                    double z = centerZ + Math.sin(angle) * radius;
            
                    serverLevel.sendParticles(
                        ParticleTypes.ENCHANT, // 魔法っぽいエフェクト
                        x, centerY, z,
                        1, // 1つずつ
                        0, 0, 0, // 広がりなし
                        0        // 速度なし
                    );
                }
            }
            int radius = 3; // 効果範囲
            int duration = 200; // 効果時間 (tick) → 10秒
            
            List<LivingEntity> mobs = level.getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                player.getBoundingBox().inflate(radius),
                e -> !(e instanceof Player)
            ); // プレイヤー以外

            int affectedMobs = 0;
            for (LivingEntity mob : mobs) {
                // 速度低下の AttributeModifier
                ResourceLocation slowIdRL = ResourceLocation.fromNamespaceAndPath("timeworldmod", "clock_speed");
                AttributeModifier slow = new AttributeModifier(
                    slowIdRL,
                    0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );
    
                var attr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
                
                if (attr != null) {
                    // 既存のモディファイアがあれば削除
                    if (attr.hasModifier(slowIdRL)) {
                        attr.removeModifier(slowIdRL);
                    }
                    
                    // 新しいモディファイアを追加
                    attr.addTransientModifier(slow);
                    affectedMobs++;
    
                    // 一定時間後に解除
                    ((ServerLevel) level).scheduleTick(mob.blockPosition(), level.getBlockState(mob.blockPosition()).getBlock(), duration);
                    // ↑ここは自前のTickカウンタやイベントで管理する方が確実
                }
            }
            
            // プレイヤーにメッセージを表示
            player.displayClientMessage(
                Component.literal("§6モブ速度リセット時計使用！ モブの速度が元に戻った"),
                true
            );
        }
        
        return InteractionResult.SUCCESS;
    }
}
