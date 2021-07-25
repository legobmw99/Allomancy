package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Arrays;

public class DefaultAllomancerData implements IAllomancerData {

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

    public DefaultAllomancerData() {

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


    public void tickBurning(ServerPlayerEntity player) {
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
                            sync = true;
                        } else {
                            this.setAmount(metal, this.getAmount(metal) - 1);
                        }
                        this.setBurnTime(metal, MAX_BURN_TIME[metal.getIndex()]);
                    }
                }
            }
        }
        if (sync) {
            Network.sync(this, player);
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


    public void setDeathLoc(BlockPos pos, RegistryKey<World> dim) {
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


    public RegistryKey<World> getDeathDim() {
        if (this.death_dimension == null) {
            return null;
        }
        return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(this.death_dimension));

    }


    public void setSpawnLoc(BlockPos pos, RegistryKey<World> dim) {
        setSpawnLoc(pos, dim.location().toString());
    }


    public void setSpawnLoc(BlockPos pos, String dim_name) {
        this.spawn_pos = pos;
        this.spawn_dimension = dim_name;
    }


    public BlockPos getSpawnLoc() {
        return this.spawn_pos;
    }


    public RegistryKey<World> getSpawnDim() {
        if (this.spawn_dimension == null) {
            return null;
        }
        return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(this.spawn_dimension));
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
     * @param metal    the the metal to set
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

}
