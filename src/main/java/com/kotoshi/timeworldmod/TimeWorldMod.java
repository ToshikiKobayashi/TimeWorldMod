package com.kotoshi.timeworldmod;

import com.kotoshi.timeworldmod.item.SlowClock;

import com.kotoshi.timeworldmod.item.FastClock;
import com.kotoshi.timeworldmod.item.NormalClock;
import com.kotoshi.timeworldmod.item.StrengthenClock;
import com.kotoshi.timeworldmod.item.WeakenClock;
import com.kotoshi.timeworldmod.item.StopClock;
import com.kotoshi.timeworldmod.item.WorldStopClock;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(TimeWorldMod.MODID)
public class TimeWorldMod {
    public static final String MODID = "timeworldmod";
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
    public static final RegistryObject<Item> POWER_STONE_ITEM = ITEMS.register("strengthen_clock",
        () -> new StrengthenClock(new Properties().setId(ITEMS.key("strengthen_clock")))
    );
    public static final RegistryObject<Item> WEAKEN_CLOCK_ITEM = ITEMS.register("weaken_clock",
        () -> new WeakenClock(new Properties().setId(ITEMS.key("weaken_clock")))
    );

    public TimeWorldMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        ITEMS.register(modBusGroup);
    }

}
