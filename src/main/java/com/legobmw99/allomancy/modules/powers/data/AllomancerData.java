package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AllomancerData implements IAllomancerData, INBTSerializable<CompoundTag> {
    private static final int[] MAX_BURN_TIME =
            {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600, 100, 20, 300, 40, 1000, 10000, 3600, 160};
    private final boolean[] allomantic_powers;
    private final int[] burn_time;
    private final int[] metal_amounts;
    private final boolean[] burning_metals;
    private int damage_stored;
    private GlobalPos spawn_pos;
    private GlobalPos seeking_pos;
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
        this.spawn_pos = null;
        this.seeking_pos = null;
    }


    public boolean tickBurning() {
        boolean sync = false;
        for (Metal metal : Metal.values()) {
            if (this.isBurning(metal)) {
                if (!this.hasPower(metal)) {
                    // put out any metals that the player shouldn't be able to burn
                    this.setBurning(metal, false);
                    sync = true;
                } else {
                    this.burn_time[metal.getIndex()]--;

                    if (this.burn_time[metal.getIndex()] <= 0) {
                        if (this.getStored(metal) <= 0) {
                            this.setBurning(metal, false);
                        } else {
                            this.decrementStored(metal);
                        }
                        sync = true;
                        this.burn_time[metal.getIndex()] = MAX_BURN_TIME[metal.getIndex()];
                    }
                }
            }
        }
        return sync;
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


    public int getStored(Metal metal) {
        return this.metal_amounts[metal.getIndex()];
    }

    public void incrementStored(Metal metal) {
        if (this.metal_amounts[metal.getIndex()] < MAX_STORAGE) {
            this.metal_amounts[metal.getIndex()]++;
        }
    }

    public void decrementStored(Metal metal) {
        if (this.metal_amounts[metal.getIndex()] > 0) {
            this.metal_amounts[metal.getIndex()]--;
        }
    }

    public void drainMetals(Metal... metals) {
        for (Metal mt : metals) {
            this.metal_amounts[mt.getIndex()] = 0;
            this.burn_time[mt.getIndex()] = MAX_BURN_TIME[mt.getIndex()];
            this.setBurning(mt, false);
        }
    }

    public int getDamageStored() {
        return this.damage_stored;
    }


    public void setDamageStored(int damageStored) {
        this.damage_stored = damageStored;
    }


    public void setSpawnLoc(BlockPos pos, ResourceKey<Level> dim) {
        if (pos != null && dim != null) {
            this.spawn_pos = GlobalPos.of(dim, pos);
        }
    }

    public @Nullable GlobalPos getSpawnLoc() {
        return this.spawn_pos;
    }

    public void setSpecialSeekingLoc(BlockPos pos, ResourceKey<Level> dim) {
        if (pos != null && dim != null) {
            this.seeking_pos = GlobalPos.of(dim, pos);
        } else {
            this.seeking_pos = null;
        }
    }

    public @Nullable GlobalPos getSpecialSeekingLoc() {
        return this.seeking_pos;
    }

    public void decrementEnhanced() {
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
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag allomancy_data = new CompoundTag();

        CompoundTag abilities = new CompoundTag();
        for (Metal mt : Metal.values()) {
            abilities.putBoolean(mt.getName(), this.hasPower(mt));
        }
        allomancy_data.put("abilities", abilities);


        CompoundTag metal_storage = new CompoundTag();
        for (Metal mt : Metal.values()) {
            metal_storage.putInt(mt.getName(), this.getStored(mt));
        }
        allomancy_data.put("metal_storage", metal_storage);


        CompoundTag metal_burning = new CompoundTag();
        for (Metal mt : Metal.values()) {
            metal_burning.putBoolean(mt.getName(), this.isBurning(mt));
        }
        allomancy_data.put("metal_burning", metal_burning);

        CompoundTag position = new CompoundTag();
        if (this.spawn_pos != null) {
            position.putString("spawn_dimension", this.spawn_pos.dimension().location().toString());
            BlockPos spawn_block = this.spawn_pos.pos();
            position.putInt("spawn_x", spawn_block.getX());
            position.putInt("spawn_y", spawn_block.getY());
            position.putInt("spawn_z", spawn_block.getZ());
        }
        if (this.seeking_pos != null) {
            position.putString("seeking_dimension", this.seeking_pos.dimension().location().toString());
            BlockPos spawn_block = this.seeking_pos.pos();
            position.putInt("seeking_x", spawn_block.getX());
            position.putInt("seeking_y", spawn_block.getY());
            position.putInt("seeking_z", spawn_block.getZ());
        }
        allomancy_data.put("position", position);

        return allomancy_data;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag allomancy_data) {
        allomancy_data.getCompound("abilities").ifPresent(abilities -> {
            for (Metal mt : Metal.values()) {
                if (abilities.getBoolean(mt.getName()).orElse(false)) {
                    this.addPower(mt);
                } else {
                    this.revokePower(mt);
                }
            }
        });


        allomancy_data.getCompound("metal_storage").ifPresent(metal_storage -> {
            for (Metal mt : Metal.values()) {
                this.metal_amounts[mt.getIndex()] = metal_storage.getInt(mt.getName()).orElse(0);
            }
        });


        allomancy_data.getCompound("metal_burning").ifPresent(metal_burning -> {
            for (Metal mt : Metal.values()) {
                this.setBurning(mt, metal_burning.getBoolean(mt.getName()).orElse(false));
            }
        });


        allomancy_data.getCompound("position").ifPresent(position -> {

            if (position.contains("spawn_dimension")) {
                this.setSpawnLoc(new BlockPos(position.getInt("spawn_x").get(), position.getInt("spawn_y").get(),
                                              position.getInt("spawn_z").get()),
                                 ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(
                                         position.getString("spawn_dimension").get())));
            }
            if (position.contains("seeking_dimension")) {
                this.setSpecialSeekingLoc(
                        new BlockPos(position.getInt("seeking_x").get(), position.getInt("seeking_y").get(),
                                     position.getInt("seeking_z").get()), ResourceKey.create(Registries.DIMENSION,
                                                                                             ResourceLocation.parse(
                                                                                                     position
                                                                                                             .getString(
                                                                                                                     "seeking_dimension")
                                                                                                             .get())));
            } else {
                this.setSpecialSeekingLoc(null, null);
            }

        });
    }
}

