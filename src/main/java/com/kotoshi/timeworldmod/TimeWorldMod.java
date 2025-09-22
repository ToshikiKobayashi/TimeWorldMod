package com.kotoshi.timeworldmod;

import com.kotoshi.timeworldmod.item.SlowClock;
import com.kotoshi.timeworldmod.entity.FutureZombie;
import com.kotoshi.timeworldmod.entity.PastZombie;
import com.kotoshi.timeworldmod.item.EvolutionClock;
import com.kotoshi.timeworldmod.item.FastClock;
import com.kotoshi.timeworldmod.item.NormalClock;
import com.kotoshi.timeworldmod.item.StrengthenClock;
import com.kotoshi.timeworldmod.item.WeakenClock;
import com.kotoshi.timeworldmod.item.StopClock;
import com.kotoshi.timeworldmod.item.WorldStopClock;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.SpawnEggItem;

@Mod(TimeWorldMod.MODID)
public class TimeWorldMod {
    public static final String MODID = "timeworldmod";

    // アイテム登録
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> SLOW_CLOCK_ITEM = ITEMS.register("slow_clock",
        () -> new SlowClock(new Properties().setId(ITEMS.key("slow_clock")))
    );
    public static final RegistryObject<Item> FAST_CLOCK_ITEM = ITEMS.register("fast_clock",
        () -> new FastClock(new Properties().setId(ITEMS.key("fast_clock")))
    );
    public static final RegistryObject<Item> NORMAL_CLOCK_ITEM = ITEMS.register("normal_clock",
        () -> new NormalClock(new Properties().setId(ITEMS.key("normal_clock")))
    );
    public static final RegistryObject<Item> STOP_CLOCK_ITEM = ITEMS.register("stop_clock",
        () -> new StopClock(new Properties().setId(ITEMS.key("stop_clock")))
    );
    public static final RegistryObject<Item> WORLD_STOP_CLOCK_ITEM = ITEMS.register("world_stop_clock",
        () -> new WorldStopClock(new Properties().setId(ITEMS.key("world_stop_clock")))
    );
    public static final RegistryObject<Item> STRENGTHEN_CLOCK = ITEMS.register("strengthen_clock",
        () -> new StrengthenClock(new Properties().setId(ITEMS.key("strengthen_clock")))
    );
    public static final RegistryObject<Item> WEAKEN_CLOCK_ITEM = ITEMS.register("weaken_clock",
        () -> new WeakenClock(new Properties().setId(ITEMS.key("weaken_clock")))
    );
    public static final RegistryObject<Item> EVOLUTION_CLOCK_ITEM = ITEMS.register("evolution_clock",
        () -> new EvolutionClock(new Properties().setId(ITEMS.key("evolution_clock")))
    );
    // エンティティ登録
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<FutureZombie>> FUTURE_ZOMBIE = 
        ENTITY_TYPES.register("future_zombie",
            () -> EntityType.Builder.of(FutureZombie::new, MobCategory.MONSTER)
                .sized(0.6f, 1.8f)
                .build(ENTITY_TYPES.key("future_zombie")));
    public static final RegistryObject<EntityType<PastZombie>> PAST_ZOMBIE = 
        ENTITY_TYPES.register("past_zombie",
            () -> EntityType.Builder.of(PastZombie::new, MobCategory.MONSTER)
                .sized(0.6f, 3.6f)
                .build(ENTITY_TYPES.key("past_zombie")));

    // FUTURE_ZOMBIEエンティティのスポーンエッグ登録
    public static final RegistryObject<Item> FUTURE_ZOMBIE_SPAWN_EGG = ITEMS.register("future_zombie_spawn_egg",
        () -> new SpawnEggItem(
            FUTURE_ZOMBIE.get(),
            new Item.Properties().setId(ITEMS.key("future_zombie_spawn_egg"))
        )
    );
    public static final RegistryObject<Item> PAST_ZOMBIE_SPAWN_EGG = ITEMS.register("past_zombie_spawn_egg",
        () -> new SpawnEggItem(
            PAST_ZOMBIE.get(),
            new Item.Properties().setId(ITEMS.key("past_zombie_spawn_egg"))
        )
    );

    // クリエイティブタブ登録
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_MODE_TABS.register("timeworld_tab",
    () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.timeworldmod"))
        .icon(() -> NORMAL_CLOCK_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
            output.accept(SLOW_CLOCK_ITEM.get());
            output.accept(FAST_CLOCK_ITEM.get());
            output.accept(NORMAL_CLOCK_ITEM.get());
            output.accept(STOP_CLOCK_ITEM.get());
            output.accept(WORLD_STOP_CLOCK_ITEM.get());
            output.accept(STRENGTHEN_CLOCK.get());
            output.accept(WEAKEN_CLOCK_ITEM.get());
            output.accept(EVOLUTION_CLOCK_ITEM.get());
            output.accept(FUTURE_ZOMBIE_SPAWN_EGG.get());
            output.accept(PAST_ZOMBIE_SPAWN_EGG.get());
        })
        .build());

    public TimeWorldMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        ITEMS.register(modBusGroup);
        ENTITY_TYPES.register(modBusGroup);
        CREATIVE_MODE_TABS.register(modBusGroup);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusSubscriber {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            try {
                event.put(FUTURE_ZOMBIE.get(), FutureZombie.createAttributes().build());
                event.put(PAST_ZOMBIE.get(), PastZombie.createAttributes().build());
            } catch (IllegalStateException e) {
                // すでに登録されている場合は無視
            }
        }
    }
}
