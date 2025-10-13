package com.kotoshi.timeworldmod.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayDeque;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = "timeworldmod")
public class GradualStructureLoader {

    private static final Queue<QueuedStructure> STRUCTURE_QUEUE = new ArrayDeque<>();
    /** 複数の建物テンプレートを配列で管理 */
    private static final ResourceLocation[] STRUCTURES = new ResourceLocation[]{
        ResourceLocation.fromNamespaceAndPath("timeworldmod", "kyoukai"),
        ResourceLocation.fromNamespaceAndPath("timeworldmod", "kyoukai2")
    };

    /** チャンクロード時に建物生成を予約 */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        LevelChunk chunk = (LevelChunk) event.getChunk();

        RandomSource random = serverLevel.random;

        // 1% の確率で生成
        if (random.nextFloat() > 0.001f) return;

        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        BlockPos pos = new BlockPos(chunkX * 16 + 8, serverLevel.getSeaLevel(), chunkZ * 16 + 8);
        STRUCTURE_QUEUE.add(new QueuedStructure(serverLevel, pos));
        System.out.println("[GradualStructurePlacer] Queued structure at " + pos);
    }

    /** 毎Tickに1件ずつ配置 */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (STRUCTURE_QUEUE.isEmpty()) return;

        QueuedStructure task = STRUCTURE_QUEUE.poll();
        if (task == null) return;

        task.place();
    }

    private static class QueuedStructure {
        private final ServerLevel level;
        private final BlockPos pos;

        QueuedStructure(ServerLevel level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        void place() {
            RandomSource random = level.random;
            StructureTemplateManager manager = level.getStructureManager();
            ResourceLocation selectedId = STRUCTURES[random.nextInt(STRUCTURES.length)];
            StructureTemplate template = manager.getOrCreate(selectedId);

            if (template == null) {
                System.out.println("[GradualStructurePlacer] Template not found: " + selectedId);
                return;
            }

            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(Rotation.NONE)
                    .setMirror(Mirror.NONE)
                    .setIgnoreEntities(true)
                    .setFinalizeEntities(true);

            boolean success = template.placeInWorld(
                    level,
                    pos,
                    pos,
                    settings,
                    level.random,
                    2 // block update flag
            );

            if (success) {
                System.out.println("[GradualStructurePlacer] Structure placed successfully at " + pos);
            } else {
                System.out.println("[GradualStructurePlacer] Failed to place structure at " + pos);
            }
        }
    }
}