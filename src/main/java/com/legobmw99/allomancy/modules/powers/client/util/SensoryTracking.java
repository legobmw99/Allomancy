package com.legobmw99.allomancy.modules.powers.client.util;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class SensoryTracking {

    private final List<Entity> metal_entities = new ArrayList<>();
    private final List<MetalBlockBlob> metal_blobs = new ArrayList<>();
    private final List<Player> nearby_allomancers = new ArrayList<>();

    private int tickOffset = 0;
    private final Deque<BlockPos> to_consider = new ArrayListDeque<>();
    private final Set<BlockPos> seen = new HashSet<>();

    public void tick() {
        this.tickOffset = (this.tickOffset + 1) % 2;
        if (this.tickOffset == 0) {
            populateSensoryLists();
        }
    }

    public void forEachSeeked(Consumer<Player> f) {
        this.nearby_allomancers.forEach(f);
    }

    public void forEachMetalicEntity(Consumer<Entity> f) {
        this.metal_entities.forEach(f);
    }

    public void forEachMetalBlob(Consumer<MetalBlockBlob> f) {
        this.metal_blobs.forEach(f);
    }

    private void populateSensoryLists() {
        Player player = Minecraft.getInstance().player;
        IAllomancerData data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        this.metal_blobs.clear();
        this.metal_entities.clear();
        if (data.isBurning(Metal.IRON) || data.isBurning(Metal.STEEL)) {
            int max = PowersConfig.max_metal_detection.get();
            var negative = player.blockPosition().offset(-max, -max, -max);
            var positive = player.blockPosition().offset(max, max, max);

            // Add metal entities to metal list
            this.metal_entities.addAll(
                    player.level().getEntitiesOfClass(Entity.class, AABB.encapsulatingFullBlocks(negative, positive), e -> PowerUtils.isEntityMetal(e) && !e.equals(player)));

            // Add metal blobs to metal list
            this.seen.clear();
            BlockPos.betweenClosed(negative, positive).forEach(starter -> searchNearbyMetalBlocks(player.blockPosition(), max, starter.immutable(), player.level()));
        }

        // Populate our list of nearby allomancy users
        this.nearby_allomancers.clear();
        if (data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER))) {
            // Add metal burners to a list
            var negative = player.position().add(-30, -30, -30);
            var positive = player.position().add(30, 30, 30);


            var nearby_players = player.level().getEntitiesOfClass(Player.class, new AABB(negative, positive), entity -> entity != null && entity != player);

            for (Player otherPlayer : nearby_players) {
                if (!addSeeked(data, otherPlayer)) {
                    this.nearby_allomancers.clear();
                    break;
                }
            }
        }
    }

    /**
     * A sort of BFS with a global seen list
     */
    private void searchNearbyMetalBlocks(BlockPos origin, int range, BlockPos starter, Level level) {
        if (this.seen.contains(starter)) {
            return;
        }
        this.seen.add(starter);

        if (!PowerUtils.isBlockStateMetal(level.getBlockState(starter))) {
            return;
        }

        int range_sqr = 4 * range * range;

        this.to_consider.clear();
        this.to_consider.addFirst(starter);

        var blob = new MetalBlockBlob(starter);
        while (!this.to_consider.isEmpty()) {
            var pos = this.to_consider.removeLast();
            for (var p1 : BlockPos.withinManhattan(pos, 1, 1, 1)) {
                if (!this.seen.contains(p1)) {
                    var p2 = p1.immutable();
                    this.seen.add(p2);
                    if (origin.distSqr(p2) < range_sqr && PowerUtils.isBlockStateMetal(level.getBlockState(p2))) {
                        this.to_consider.add(p2);
                        blob.add(p2);
                    }
                }
            }
        }
        this.metal_blobs.add(blob);
    }

    private boolean addSeeked(IAllomancerData data, Player otherPlayer) {
        var otherData = otherPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA);
        if (otherData.isBurning(Metal.COPPER) && (!data.isEnhanced() || otherData.isEnhanced())) {
            return false;
        }

        if (Arrays.stream(Metal.values()).anyMatch(otherData::isBurning)) {
            this.nearby_allomancers.add(otherPlayer);
        }

        return true;
    }


    public static class MetalBlockBlob {

        private static final Level level = Minecraft.getInstance().level;
        private int blocks = 0;
        private Vec3 center = null;

        public MetalBlockBlob(BlockPos initial) {
            this.add(initial);
        }

        public MetalBlockBlob() {
        }

        private static Vec3 getCenterOfBlock(BlockPos pos) {
            try {
                return Vec3.atLowerCornerOf(pos).add(level.getBlockState(pos).getShape(level, pos).bounds().getCenter());
            } catch (UnsupportedOperationException e) {
                return Vec3.atCenterOf(pos);
            }
        }

        public int size() {
            return this.blocks;
        }

        public void add(BlockPos pos) {
            pos = pos.immutable();

            this.blocks += 1;

            if (this.center == null) {
                this.center = getCenterOfBlock(pos);
            } else {
                this.center = this.center.scale(this.blocks - 1).add(getCenterOfBlock(pos)).scale(1.0D / this.blocks);
            }

        }

        public Vec3 getCenter() {
            return this.center;
        }

        @Override
        public String toString() {
            return "MetalBlockBlob{" + "blocks=" + this.blocks + ", center=" + this.center + '}';
        }
    }
}
