package com.legobmw99.allomancy.modules.powers.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class MetalBlockBlob {

    private static final Level level = Minecraft.getInstance().level;
    private final Set<BlockPos> blocks = new HashSet<>();
    private Vec3 center = null;

    public MetalBlockBlob(BlockPos initial) {
        this.add(initial);
    }

    public MetalBlockBlob() {

    }

    public static MetalBlockBlob merge(MetalBlockBlob blob1, MetalBlockBlob blob2) {
        if (blob1 == null) {
            return blob2;
        } else if (blob2 == null) {
            return blob1;
        }

        MetalBlockBlob blob3 = new MetalBlockBlob();
        blob3.blocks.addAll(blob1.blocks);
        blob3.blocks.addAll(blob2.blocks);
        blob3.center = blob1.center.scale(blob1.blocks.size()).add(blob2.center.scale(blob2.blocks.size())).scale(1.0 / blob3.blocks.size());
        return blob3;
    }

    public boolean isMatch(BlockPos pos) {
        return this.blocks.stream().anyMatch(bp -> Vec3.atCenterOf(bp).distanceTo(Vec3.atCenterOf(pos)) <= 1.5);
    }

    public int size() {
        return this.blocks.size();
    }

    private Vec3 getCenterOfBlock(BlockPos pos) {
        try {
            return Vec3.atLowerCornerOf(pos).add(level.getBlockState(pos).getShape(level, pos).bounds().getCenter());
        } catch (UnsupportedOperationException e) {
            return Vec3.atCenterOf(pos);
        }
    }

    public boolean add(BlockPos pos) {
        pos = pos.immutable();

        if (this.blocks.add(pos)) {

            if (this.center == null) {
                this.center = getCenterOfBlock(pos);
            } else {
                int count = this.blocks.size();
                this.center = this.center.scale(count - 1).add(getCenterOfBlock(pos)).scale(1.0D / count);

            }
            return true;
        }
        return false;
    }

    public boolean isIn(BlockPos pos) {
        return this.blocks.contains(pos);
    }

    public Vec3 getCenter() {
        return this.center;
    }

}
