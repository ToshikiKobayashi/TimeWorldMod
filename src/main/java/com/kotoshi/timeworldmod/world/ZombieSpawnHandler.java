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
        CommandStorage storage = event.getLevel().getServer().getCommandStorage();
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath("timeworldmod", "evolution_clock");
        CompoundTag data = storage.get(key);
        boolean evolutionEnabled = false;
        if (data != null) {
            evolutionEnabled = data.getBoolean("evolutionEnabled").orElse(false);
        }
        if (!evolutionEnabled) return;

        // サーバー側だけで処理
        Entity entity = event.getEntity();
        if (!(entity instanceof Zombie oldZombie)) return;
        Level level = oldZombie.level();
        if (level.isClientSide()) return;

        long days = level.getDayTime() / 24000L;
        EntityType<? extends Zombie> replacementType = null;

        if (days >= 7) {
            replacementType = TimeWorldMod.FUTURE_ZOMBIE.get();
        } else if (days <= 3) {
            replacementType = TimeWorldMod.PAST_ZOMBIE.get();
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
