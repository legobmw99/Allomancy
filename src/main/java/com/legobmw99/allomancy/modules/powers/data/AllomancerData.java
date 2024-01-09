package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.Arrays;

public class AllomancerData implements IAllomancerData, INBTSerializable<CompoundTag> {

    private static final int[] MAX_BURN_TIME = {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600, 100, 20, 300, 40, 1000, 10000, 3600, 160};

    private final boolean[] allomantic_powers;
    private final int[] burn_time;
    private final int[] metal_amounts;
    private final boolean[] burning_metals;
    private int damage_stored;
    private String death_dimension;
    private BlockPos death_pos;
    private String spawn_dimension;
    private BlockPos spawn_pos;
    private int enhanced_time;

    public AllomancerData() {

        int powers = Metal.values().length;
        this.allomantic_powers = new boolean[powers];
        Arrays.fill(this.allomantic_powers, false);

        this.metal_amounts = new int[powers];
        Arrays.fill(this.metal_amounts, 0);

        this.burn_time = Arrays.copyOf(MAX_BURN_TIME, powers);

        this.burning_metals = new boolean[powers];
        Arrays.fill(this.burning_metals, false);

        this.enhanced_time = 0;
        this.damage_stored = 0;
        this.death_pos = null;
        this.spawn_pos = null;
    }


    public void tickBurning(ServerPlayer player) {
        boolean sync = false;
        for (Metal metal : Metal.values()) {
            if (this.isBurning(metal)) {
                if (!this.hasPower(metal)) {
                    // put out any metals that the player shouldn't be able to burn
                    this.setBurning(metal, false);
                    sync = true;
                } else {
                    this.setBurnTime(metal, this.getBurnTime(metal) - 1);
                    if (this.getBurnTime(metal) <= 0) {
                        if (this.getAmount(metal) <= 0) {
                            this.setBurning(metal, false);
                        } else {
                            this.setAmount(metal, this.getAmount(metal) - 1);
                        }
                        sync = true;
                        this.setBurnTime(metal, MAX_BURN_TIME[metal.getIndex()]);
                    }
                }
            }
        }
        if (sync) {
            Network.syncAllomancerData(player);
        }

    }


    public boolean hasPower(Metal metal) {
        return this.allomantic_powers[metal.getIndex()];
    }


    public int getPowerCount() {
        int count = 0;
        for (boolean power : this.allomantic_powers) {
            if (power) {
                count++;
            }
        }
        return count;
    }


    public Metal[] getPowers() {
        return Arrays.stream(Metal.values()).filter(this::hasPower).toArray(Metal[]::new);
    }


    public boolean isMistborn() {
        for (boolean power : this.allomantic_powers) {
            if (!power) {
                return false;
            }
        }
        return true;
    }


    public void setMistborn() {
        Arrays.fill(this.allomantic_powers, true);
    }


    public boolean isUninvested() {
        for (boolean power : this.allomantic_powers) {
            if (power) {
                return false;
            }
        }
        return true;
    }


    public void setUninvested() {
        Arrays.fill(this.allomantic_powers, false);
    }


    public void addPower(Metal metal) {
        this.allomantic_powers[metal.getIndex()] = true;
    }


    public void revokePower(Metal metal) {
        this.allomantic_powers[metal.getIndex()] = false;
    }

    public boolean isBurning(Metal metal) {
        return this.burning_metals[metal.getIndex()];
    }


    public void setBurning(Metal metal, boolean metalBurning) {
        this.burning_metals[metal.getIndex()] = metalBurning;
    }


    public int getAmount(Metal metal) {
        return this.metal_amounts[metal.getIndex()];
    }


    public void setAmount(Metal metal, int amt) {
        this.metal_amounts[metal.getIndex()] = amt;
    }


    public void drainMetals(Metal... metals) {
        for (Metal mt : metals) {
            this.metal_amounts[mt.getIndex()] = 0;
            // So that they burn out next tick
            this.burn_time[mt.getIndex()] = 1;
        }
    }


    public int getDamageStored() {
        return this.damage_stored;
    }


    public void setDamageStored(int damageStored) {
        this.damage_stored = damageStored;
    }


    public void setDeathLoc(BlockPos pos, ResourceKey<Level> dim) {
        if (dim != null) {
            setDeathLoc(pos, dim.location().toString());
        }
    }


    public void setDeathLoc(BlockPos pos, String dim_name) {
        this.death_pos = pos;
        this.death_dimension = dim_name;
    }


    public BlockPos getDeathLoc() {
        return this.death_pos;
    }


    public ResourceKey<Level> getDeathDim() {
        if (this.death_dimension == null) {
            return null;
        }
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.death_dimension));

    }


    public void setSpawnLoc(BlockPos pos, ResourceKey<Level> dim) {
        setSpawnLoc(pos, dim.location().toString());
    }


    public void setSpawnLoc(BlockPos pos, String dim_name) {
        this.spawn_pos = pos;
        this.spawn_dimension = dim_name;
    }


    public BlockPos getSpawnLoc() {
        return this.spawn_pos;
    }


    public ResourceKey<Level> getSpawnDim() {
        if (this.spawn_dimension == null) {
            return null;
        }
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.spawn_dimension));
    }

    /**
     * Get the burn time of a specific metal
     *
     * @param metal the metal to retrieve
     * @return the burn time
     */
    protected int getBurnTime(Metal metal) {
        return this.burn_time[metal.getIndex()];
    }

    /**
     * Set the burn time of a specific metal
     *
     * @param metal    the metal to set
     * @param burnTime the burn time
     */
    protected void setBurnTime(Metal metal, int burnTime) {
        this.burn_time[metal.getIndex()] = burnTime;
    }

    public void decEnhanced() {
        if (isEnhanced()) {
            this.enhanced_time--;
        }
    }

    public boolean isEnhanced() {
        return this.enhanced_time > 0;
    }

    public void setEnhanced(int time) {
        this.enhanced_time = time;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag allomancy_data = new CompoundTag();

        CompoundTag abilities = new CompoundTag();
        for (Metal mt : Metal.values()) {
            abilities.putBoolean(mt.getName(), this.hasPower(mt));
        }
        allomancy_data.put("abilities", abilities);


        CompoundTag metal_storage = new CompoundTag();
        for (Metal mt : Metal.values()) {
            metal_storage.putInt(mt.getName(), this.getAmount(mt));
        }
        allomancy_data.put("metal_storage", metal_storage);


        CompoundTag metal_burning = new CompoundTag();
        for (Metal mt : Metal.values()) {
            metal_burning.putBoolean(mt.getName(), this.isBurning(mt));
        }
        allomancy_data.put("metal_burning", metal_burning);

        CompoundTag position = new CompoundTag();
        BlockPos death_block = this.getDeathLoc();
        if (death_block != null) {
            position.putString("death_dimension", this.getDeathDim().location().toString());
            position.putInt("death_x", death_block.getX());
            position.putInt("death_y", death_block.getY());
            position.putInt("death_z", death_block.getZ());
        }
        BlockPos spawn_block = this.getSpawnLoc();
        if (spawn_block != null) {
            position.putString("spawn_dimension", this.getSpawnDim().location().toString());
            position.putInt("spawn_x", spawn_block.getX());
            position.putInt("spawn_y", spawn_block.getY());
            position.putInt("spawn_z", spawn_block.getZ());
        }
        allomancy_data.put("position", position);

        return allomancy_data;
    }

    @Override
    public void deserializeNBT(CompoundTag allomancy_data) {
        CompoundTag abilities = (CompoundTag) allomancy_data.get("abilities");
        for (Metal mt : Metal.values()) {
            if (abilities.getBoolean(mt.getName())) {
                this.addPower(mt);
            } else {
                this.revokePower(mt);
            }
        }

        CompoundTag metal_storage = (CompoundTag) allomancy_data.get("metal_storage");
        for (Metal mt : Metal.values()) {
            this.setAmount(mt, metal_storage.getInt(mt.getName()));
        }

        CompoundTag metal_burning = (CompoundTag) allomancy_data.get("metal_burning");
        for (Metal mt : Metal.values()) {
            this.setBurning(mt, metal_burning.getBoolean(mt.getName()));
        }

        CompoundTag position = (CompoundTag) allomancy_data.get("position");
        if (position.contains("death_dimension")) {
            this.setDeathLoc(new BlockPos(position.getInt("death_x"), position.getInt("death_y"), position.getInt("death_z")), position.getString("death_dimension"));
        }
        if (position.contains("spawn_dimension")) {
            this.setSpawnLoc(new BlockPos(position.getInt("spawn_x"), position.getInt("spawn_y"), position.getInt("spawn_z")), position.getString("spawn_dimension"));
        }

    }
}

