package com.kotoshi.timeworldmod.entity;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class FutureDragonEntity extends Monster {
    private Vec3 orbitCenter;
    private float orbitAngle = 0;
    private int attackCooldown = 0;
    private int attackType = 0; // 0: レーザー, 1: 雷, 2: 体当たり
    private Random random = new Random();

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
        this.goalSelector.addGoal(2, new DragonAttackGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 64.0F));
        
        // ターゲット設定ゴールを追加
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
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
            // 攻撃中でない場合のみ飛行パターンを実行
            LivingEntity target = this.dragon.getTarget();
            return target == null || this.dragon.distanceTo(target) > 30.0D;
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
        
        // 攻撃クールダウンを管理
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }
    }
    
    // 攻撃メソッド
    public void performAttack() {
        if (this.attackCooldown > 0) return;
        
        LivingEntity target = this.getTarget();
        if (target == null) return;

        // ランダムに攻撃タイプを選択
        this.attackType = random.nextInt(3);

        System.out.printf("attack type = %d\n", this.attackType);
        
        switch (this.attackType) {
            case 0: // レーザー光線
                performLaserAttack(target);
                break;
            case 1: // 雷攻撃
                performLightningAttack(target);
                break;
            case 2: // 体当たり
                performChargeAttack(target);
                break;
        }
        
        this.attackCooldown = 100 + random.nextInt(100); // 5-10秒のクールダウン
    }
    
    private void performLaserAttack(LivingEntity target) {
        // レーザー光線（ファイアボール）を発射
        Vec3 direction = target.position().subtract(this.position()).normalize();
        SmallFireball fireball = new SmallFireball(this.level(), 
            this.getX(), this.getY() + 2.0, this.getZ(), direction);
        this.level().addFreshEntity(fireball);
        
        // 音響効果
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
            SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
    }
    
    private void performLightningAttack(LivingEntity target) {
        // 雷を召喚
        BlockPos targetPos = target.blockPosition();
        this.level().explode(this, targetPos.getX(), targetPos.getY(), targetPos.getZ(), 
            3.0F, Level.ExplosionInteraction.MOB);
        
        // 音響効果
        this.level().playSound(null, targetPos, SoundEvents.LIGHTNING_BOLT_THUNDER, 
            SoundSource.WEATHER, 1.0F, 1.0F);
    }
    
    private void performChargeAttack(LivingEntity target) {
        // 体当たり攻撃（高速移動）
        Vec3 direction = target.position().subtract(this.position()).normalize();
        Vec3 chargeVelocity = direction.scale(2.0);
        this.setDeltaMovement(chargeVelocity);
        
        // 音響効果
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), 
            SoundEvents.ENDER_DRAGON_FLAP, SoundSource.HOSTILE, 1.0F, 0.5F);
    }
    
    // 攻撃ゴール
    static class DragonAttackGoal extends Goal {
        private final FutureDragonEntity dragon;
        private int attackTimer = 0;
        
        public DragonAttackGoal(FutureDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        
        @Override
        public boolean canUse() {
            boolean hasTarget = this.dragon.getTarget() != null;
            boolean inRange = false;
            if (hasTarget) {
                LivingEntity target = this.dragon.getTarget();
                if (target != null) {
                    inRange = this.dragon.distanceTo(target) < 50.0D;
                }
            }
            
            return hasTarget && inRange;
        }
        
        @Override
        public void start() {
            this.attackTimer = 0;
        }
        
        @Override
        public void tick() {
            this.attackTimer++;
            
            // 3-5秒ごとに攻撃
            if (this.attackTimer >= 60 + this.dragon.random.nextInt(40)) {
                this.dragon.performAttack();
                this.attackTimer = 0;
            }
        }
    }
}
