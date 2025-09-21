package com.kotoshi.timeworldmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import javax.annotation.Nonnull;

public class WorldStopClock extends Item {
    public WorldStopClock(Properties props) {
        super(props);
    }
 
    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true; // 常にエンチャントグロウ効果を表示
    }

    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
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

                boolean daylightOn = serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
                serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(!daylightOn, serverLevel.getServer());

                if (daylightOn) {
                    player.displayClientMessage(Component.literal("§1ワールド時間を停止しました (doDaylightCycle: OFF)"), true);
                } else {
                    player.displayClientMessage(Component.literal("§1ワールド時間を再開しました (doDaylightCycle: ON)"), true);
                }
            }
        }
        
        return InteractionResult.SUCCESS;
    }
}
