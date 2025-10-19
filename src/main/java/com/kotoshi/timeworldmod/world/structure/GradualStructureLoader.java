package com.kotoshi.timeworldmod.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Mod.EventBusSubscriber(modid = "timeworldmod")
public class GradualStructureLoader {

    private static final Queue<QueuedStructure> STRUCTURE_QUEUE = new ArrayDeque<>();

    /** 生成済みチャンクキーを保持（"dim:chunkX,chunkZ"） */
    private static final Set<String> GENERATED_CHUNKS = new HashSet<>();

    /** ファイルに保存するためのパスを返す */
    private static Path getSaveFile(ServerLevel level) {
        Path saveDir = level.getServer().getWorldPath(LevelResource.ROOT); // ワールドディレクトリ
        String dimName = level.dimension().location().toString().replace(':', '_').replace('/', '_');
        return saveDir.resolve("generated_structures_" + dimName + ".dat");
    }

    /** サーバー起動時にロード */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        event.getServer().getAllLevels().forEach(level -> {
            if (level instanceof ServerLevel serverLevel) {
                loadGeneratedChunks(serverLevel);
            }
        });
    }

    /** 生成済みチャンクをロード */
    private static void loadGeneratedChunks(ServerLevel level) {
        Path path = getSaveFile(level);
        if (!Files.exists(path)) return;

        try {
            List<String> lines = Files.readAllLines(path);
            GENERATED_CHUNKS.addAll(lines);
            System.out.println("[GradualStructurePlacer] Loaded " + lines.size() + " generated chunks from " + path);
        } catch (IOException e) {
            System.err.println("[GradualStructurePlacer] Failed to load generated chunks: " + e.getMessage());
        }
    }

    /** 生成済みチャンクを保存 */
    private static void saveGeneratedChunks(ServerLevel level) {
        Path path = getSaveFile(level);
        try {
            Files.write(path, GENERATED_CHUNKS);
        } catch (IOException e) {
            System.err.println("[GradualStructurePlacer] Failed to save generated chunks: " + e.getMessage());
        }
    }

    /** 建物テンプレート一覧 */
    private static final ResourceLocation[] STRUCTURES = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("timeworldmod", "kyoukai"),
            ResourceLocation.fromNamespaceAndPath("timeworldmod", "kyoukai2")
    };

    /** チャンクロード時に生成予約 */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        LevelChunk chunk = (LevelChunk) event.getChunk();
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        // シードベースの固定乱数
        long worldSeed = serverLevel.getSeed();
        long combinedSeed = worldSeed ^ (chunkX * 341873128712L + chunkZ * 132897987541L);
        RandomSource random = RandomSource.create(combinedSeed);

        // 1% の確率で生成
        if (random.nextFloat() > 0.001f) return;

        // ディメンション込みのユニークキー
        String chunkKey = serverLevel.dimension().location() + ":" + chunkX + "," + chunkZ;

        // ★ すでに生成済みならスキップ
        if (GENERATED_CHUNKS.contains(chunkKey)) {
            // System.out.println("[GradualStructurePlacer] Skipping already generated chunk: " + chunkKey);
            return;
        }

        BlockPos pos = new BlockPos(chunkX * 16 + 8, serverLevel.getSeaLevel(), chunkZ * 16 + 8);
        STRUCTURE_QUEUE.add(new QueuedStructure(serverLevel, pos, chunkKey));
        System.out.println("[GradualStructurePlacer] Queued structure for " + chunkKey + " at " + pos);
    }

    /** 毎Tickに1件ずつ配置 */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || STRUCTURE_QUEUE.isEmpty()) return;

        QueuedStructure task = STRUCTURE_QUEUE.poll();
        if (task != null) task.place();
    }

    /** 建物配置タスク */
    private static class QueuedStructure {
        private final ServerLevel level;
        private final BlockPos pos;
        private final String chunkKey;

        QueuedStructure(ServerLevel level, BlockPos pos, String chunkKey) {
            this.level = level;
            this.pos = pos;
            this.chunkKey = chunkKey;
        }

        void place() {
            RandomSource random = level.random;
            StructureTemplateManager manager = level.getStructureManager();
            ResourceLocation selectedId = STRUCTURES[random.nextInt(STRUCTURES.length)];
            StructureTemplate template = manager.getOrCreate(selectedId);

            if (template == null) {
                System.err.println("[GradualStructurePlacer] Template not found: " + selectedId);
                return;
            }

            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(Rotation.NONE)
                    .setMirror(Mirror.NONE)
                    .setIgnoreEntities(true)
                    .setFinalizeEntities(true);

            boolean success = template.placeInWorld(level, pos, pos, settings, level.random, 2);

            if (success) {
                GENERATED_CHUNKS.add(chunkKey);
                saveGeneratedChunks(level); // 永続保存
                System.out.println("[GradualStructurePlacer] Structure placed successfully at " + pos + " (" + chunkKey + ")");
            }
        }
    }
}