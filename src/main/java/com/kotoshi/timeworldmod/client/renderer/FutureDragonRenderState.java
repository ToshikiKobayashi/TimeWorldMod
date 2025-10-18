package com.kotoshi.timeworldmod.client.renderer;



import javax.annotation.Nullable;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.boss.enderdragon.DragonFlightHistory;
import net.minecraft.world.phys.Vec3;

public class FutureDragonRenderState extends LivingEntityRenderState {
    public float flapTime;
    public float deathTime;
    public boolean hasRedOverlay;
    @Nullable
    public Vec3 beamOffset;
    public boolean isLandingOrTakingOff;
    public boolean isSitting;
    public double distanceToEgg;
    public float partialTicks;
    public final DragonFlightHistory flightHistory = new DragonFlightHistory();
    public final AnimationState animation = new AnimationState();

    public FutureDragonRenderState() {
        super();
    }

    public DragonFlightHistory.Sample getHistoricalPos(int p_368884_) {
        return this.flightHistory.get(p_368884_, this.partialTicks);
    }

    public float getHeadPartYOffset(int p_362696_, DragonFlightHistory.Sample p_368463_, DragonFlightHistory.Sample p_361357_) {
        double d0;
        if (this.isLandingOrTakingOff) {
            d0 = p_362696_ / Math.max(this.distanceToEgg / 4.0, 1.0);
        } else if (this.isSitting) {
            d0 = p_362696_;
        } else if (p_362696_ == 6) {
            d0 = 0.0;
        } else {
            d0 = p_361357_.y() - p_368463_.y();
        }

        return (float)d0;
    }
}