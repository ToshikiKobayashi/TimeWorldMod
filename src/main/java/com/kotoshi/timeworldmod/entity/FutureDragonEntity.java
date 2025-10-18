package com.kotoshi.timeworldmod.entity;

import java.util.EnumSet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class FutureDragonEntity extends Monster {
    private Vec3 orbitCenter;
    private float orbitAngle = 0;

    // ボスバー定義
    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.literal("§c炎のドラゴン"),
                    BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);
    public final AnimationState animation = new AnimationState();

    public FutureDragonEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    // プレイヤーが近づいたらバーを表示
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    // プレイヤーが離れたらバーを非表示
    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.MOVEMENT_SPEED, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        bossEvent.removeAllPlayers();
    }

    @Override
    public boolean shouldShowName() {
        return false; // 名前非表示
    }

    public boolean isBoss() {
        return true; // ボス扱い
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DragonFlyPatternGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 64.0F));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    static class DragonFlyPatternGoal extends Goal {
        private final FutureDragonEntity dragon;

        public DragonFlyPatternGoal(FutureDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (this.dragon.orbitCenter == null) {
                // 飛行の中心点を設定（スポーン地点）
                this.dragon.orbitCenter = this.dragon.position();
            }

            // 円運動
            this.dragon.orbitAngle += 0.02F; // 回転速度
            double radius = 40.0D;
            double height = 15.0D * Math.sin(this.dragon.tickCount / 40.0);

            double targetX = this.dragon.orbitCenter.x + Math.cos(this.dragon.orbitAngle) * radius;
            double targetY = this.dragon.orbitCenter.y + 10.0D + height;
            double targetZ = this.dragon.orbitCenter.z + Math.sin(this.dragon.orbitAngle) * radius;

            this.dragon.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 1.8D);

            // ゆるやかに向きを変える
            Vec3 moveVec = new Vec3(targetX - this.dragon.getX(), targetY - this.dragon.getY(), targetZ - this.dragon.getZ());
            float yaw = (float) (Math.atan2(moveVec.z, moveVec.x) * (180 / Math.PI)) - 90F;
            this.dragon.setYRot(yaw);
            this.dragon.yBodyRot = yaw;
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 常にパクパク動かす
        this.animation.startIfStopped(this.tickCount);
    }
}
