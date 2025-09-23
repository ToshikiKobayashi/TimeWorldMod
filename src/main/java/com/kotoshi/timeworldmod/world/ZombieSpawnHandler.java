package com.kotoshi.timeworldmod.world;

import com.kotoshi.timeworldmod.TimeWorldMod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TimeWorldMod.MODID)
public class ZombieSpawnHandler {

    @SubscribeEvent
    public static void onZombieSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if(event.getSpawnReason() != EntitySpawnReason.NATURAL) return;

        CommandStorage storage = event.getLevel().getServer().getCommandStorage();
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath("timeworldmod", "evolution_clock");
        CompoundTag data = storage.get(key);
        Integer evolutionType = 0;
        if (data != null) {
            evolutionType = data.getInt("evolutionType").orElse(0);
        }
        if (evolutionType == 0) return;

        // サーバー側だけで処理
        Entity entity = event.getEntity();
        if (!(entity instanceof Zombie oldZombie)) return;
        Level level = oldZombie.level();
        if (level.isClientSide()) return;

        EntityType<? extends Zombie> replacementType = null;

        if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_PAST)) {
            replacementType = TimeWorldMod.PAST_ZOMBIE.get();
        } else if (evolutionType.equals(TimeWorldMod.TIMEWORLD_TYPE_FUTURE)) {
            replacementType = TimeWorldMod.FUTURE_ZOMBIE.get();
        } else {
            replacementType = null;
        }

        if (replacementType == null) return;

        // 新しいゾンビを作る
        Zombie newZombie = (Zombie) replacementType.create(level, EntitySpawnReason.EVENT);
        if (newZombie == null) return;

        newZombie.setPos(oldZombie.getX(), oldZombie.getY(), oldZombie.getZ());
        newZombie.setYRot(oldZombie.getYRot());
        newZombie.setXRot(oldZombie.getXRot());
        newZombie.setYHeadRot(oldZombie.getYRot());
        newZombie.setYBodyRot(oldZombie.getYRot());

        // ---------- ワールドに追加して古いエンティティを消す ----------
        level.addFreshEntity(newZombie); // サーバー側であれば ServerLevel でも可
        oldZombie.discard(); // 旧エンティティを削除
    }
}
