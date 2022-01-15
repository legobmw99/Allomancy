package com.legobmw99.allomancy.modules.powers.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MetalBlockBlob {

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
