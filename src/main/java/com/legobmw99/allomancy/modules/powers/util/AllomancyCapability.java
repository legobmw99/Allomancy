package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class AllomancyCapability implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(AllomancyCapability.class)
    public static final Capability<AllomancyCapability> PLAYER_CAP = null;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    private static final int[] MAX_BURN_TIME = {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600, 100, 20, 300, 40, 1000, 10000, 3600, 160};


    private final boolean[] allomantic_powers;
    private final int[] burn_time;
    private final int[] metal_amounts;
    private final boolean[] burning_metals;
    private final LazyOptional<AllomancyCapability> handler;
    private int damage_stored;
    private String death_dimension;
    private BlockPos death_pos;
    private String spawn_dimension;
    private BlockPos spawn_pos;
    private int enhanced_time;

    public AllomancyCapability() {
        this.handler = LazyOptional.of(() -> this);

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

    /**
     * Retrieve data for a specific player
     *
     * @param player the player you want data for
     * @return the AllomancyCapabilities data of the player
     */
    public static AllomancyCapability forPlayer(Entity player) {
        //noinspection ConstantConditions
        return player.getCapability(PLAYER_CAP).orElseThrow(() -> new RuntimeException("Capability not attached!"));
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(AllomancyCapability.class, new AllomancyCapability.Storage(), () -> null);
    }

    /**
     * Called each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param player the player being checked
     */
    public void updateMetalBurnTime(ServerPlayerEntity player) {
        for (Metal metal : Metal.values()) {
            if (this.isBurning(metal)) {
                if (!this.hasPower(metal)) {
                    // put out any metals that the player shouldn't be able to burn
                    this.setBurning(metal, false);
                    Network.sync(this, player);
                } else {
                    this.setBurnTime(metal, this.getBurnTime(metal) - 1);
                    if (this.getBurnTime(metal) <= 0) {
                        if (this.getAmount(metal) <= 0) {
                            this.setBurning(metal, false);
                        } else {
                            this.setAmount(metal, this.getAmount(metal) - 1);
                        }
                        this.setBurnTime(metal, MAX_BURN_TIME[metal.getIndex()]);
                        Network.sync(this, player);
                    }
                }
            }
        }
    }

    /**
     * Get if the player has the supplied power
     *
     * @param metal the Metal to check
     * @return true if this capability has the power specified
     */
    public boolean hasPower(Metal metal) {
        return this.allomantic_powers[metal.getIndex()];
    }

    /**
     * Get the number of powers the player has
     *
     * @return int between 0 and 16, inclusive
     */
    public int getPowerCount() {
        int count = 0;
        for (boolean power : this.allomantic_powers) {
            if (power) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns an array of the player's metal abilities
     *
     * @return array of Metal
     */
    public Metal[] getPowers() {
        return Arrays.stream(Metal.values()).filter(this::hasPower).toArray(Metal[]::new);
    }

    /**
     * Check if the player is a Mistborn
     *
     * @return true if the player has ALL powers
     */
    public boolean isMistborn() {
        for (boolean power : this.allomantic_powers) {
            if (!power) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the player as a Mistborn
     */
    public void setMistborn() {
        Arrays.fill(this.allomantic_powers, true);
    }

    /**
     * Check if the player is uninvested
     *
     * @return true if the player has NO powers
     */
    public boolean isUninvested() {
        for (boolean power : this.allomantic_powers) {
            if (power) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the player as uninvested
     */
    public void setUninvested() {
        Arrays.fill(this.allomantic_powers, false);
    }

    /**
     * Grant this player the given metal power
     *
     * @param metal the Metal to add
     */
    public void addPower(Metal metal) {
        this.allomantic_powers[metal.getIndex()] = true;
    }

    /**
     * Remove the given metal power from this player
     *
     * @param metal the Metal to remove
     */
    public void revokePower(Metal metal) {
        this.allomantic_powers[metal.getIndex()] = false;
    }

    /**
     * Checks if the player is burning the given metal
     *
     * @param metal the Metal to check
     * @return true if the player is burning it
     */
    public boolean isBurning(Metal metal) {
        return this.burning_metals[metal.getIndex()];
    }

    /**
     * Sets the player's burning flag for the given metal
     *
     * @param metal        the Metal to set
     * @param metalBurning the value to set it to
     */
    public void setBurning(Metal metal, boolean metalBurning) {
        this.burning_metals[metal.getIndex()] = metalBurning;
    }

    /**
     * Gets the players stored amount of the given metal
     *
     * @param metal the Metal to check
     * @return the amount stored
     */
    public int getAmount(Metal metal) {
        return this.metal_amounts[metal.getIndex()];
    }

    /**
     * Sets the players amount of Metal to the given value
     *
     * @param metal the Metal to set
     * @param amt   the amount stored
     */
    public void setAmount(Metal metal, int amt) {
        this.metal_amounts[metal.getIndex()] = amt;
    }

    /**
     * Drain all specified metals
     *
     * @param metals all metals to drain
     */
    public void drainMetals(Metal... metals) {
        for (Metal mt : metals) {
            this.metal_amounts[mt.getIndex()] = 0;
            // So that they burn out next tick
            this.burn_time[mt.getIndex()] = 1;
        }
    }

    /**
     * Get how much damage has been accumulated
     *
     * @return the amount of damage
     */
    public int getDamageStored() {
        return this.damage_stored;
    }

    /**
     * Set the amount of damage stored
     *
     * @param damageStored the amount of damage
     */
    public void setDamageStored(int damageStored) {
        this.damage_stored = damageStored;
    }

    /**
     * Set the death location and dimension
     *
     * @param pos BlockPos of the death location
     * @param dim The RegistryKey representing the dimension the death occurred in
     */
    public void setDeathLoc(BlockPos pos, RegistryKey<World> dim) {
        if (dim != null) {
            setDeathLoc(pos, dim.location().toString());
        }
    }

    /**
     * Set the death location and dimension
     *
     * @param pos      BlockPos of the death location
     * @param dim_name A string representing the dimension the death occurred in, e.g. minecraft:overworld
     */
    protected void setDeathLoc(BlockPos pos, String dim_name) {
        this.death_pos = pos;
        this.death_dimension = dim_name;
    }

    /**
     * Returns the location of the most recent player's death, or null
     *
     * @return BlockPos of player's death, or null
     */
    public BlockPos getDeathLoc() {
        return this.death_pos;
    }

    /**
     * Returns the dimension of the most recent player's death, or null
     *
     * @return RegistryKey corresponding to the dimension, or null
     */
    public RegistryKey<World> getDeathDim() {
        if (this.death_dimension == null) {
            return null;
        }
        return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(this.death_dimension));

    }

    /**
     * Set the spawn location and dimension
     *
     * @param pos BlockPos of the spawn point
     * @param dim The RegistryKey representing the spawn dimension
     */
    public void setSpawnLoc(BlockPos pos, RegistryKey<World> dim) {
        setSpawnLoc(pos, dim.location().toString());
    }

    /**
     * Set the spawn location and dimension
     *
     * @param pos      BlockPos of the spawn point
     * @param dim_name A string representing the dimension the spawn dimension, e.g. minecraft:overworld
     */
    public void setSpawnLoc(BlockPos pos, String dim_name) {
        this.spawn_pos = pos;
        this.spawn_dimension = dim_name;
    }

    /**
     * Returns the location of the players spawn point if set, or null
     *
     * @return BlockPos of player's death, or null
     */
    public BlockPos getSpawnLoc() {
        return this.spawn_pos;
    }

    /**
     * Returns the dimension of the most player's spawn point, or null if unset.
     *
     * @return RegistryKey corresponding to the dimension, or null
     */
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_CAP.orEmpty(cap, this.handler);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT allomancy_data = new CompoundNBT();

        CompoundNBT abilities = new CompoundNBT();
        for (Metal mt : Metal.values()) {
            abilities.putBoolean(mt.getName(), this.hasPower(mt));
        }
        allomancy_data.put("abilities", abilities);


        CompoundNBT metal_storage = new CompoundNBT();
        for (Metal mt : Metal.values()) {
            metal_storage.putInt(mt.getName(), this.getAmount(mt));
        }
        allomancy_data.put("metal_storage", metal_storage);


        CompoundNBT metal_burning = new CompoundNBT();
        for (Metal mt : Metal.values()) {
            metal_burning.putBoolean(mt.getName(), this.isBurning(mt));
        }
        allomancy_data.put("metal_burning", metal_burning);

        CompoundNBT position = new CompoundNBT();
        if (this.death_pos != null) {
            position.putString("death_dimension", this.death_dimension);
            position.putInt("death_x", this.death_pos.getX());
            position.putInt("death_y", this.death_pos.getY());
            position.putInt("death_z", this.death_pos.getZ());
        }
        if (this.spawn_pos != null) {
            position.putString("spawn_dimension", this.spawn_dimension);
            position.putInt("spawn_x", this.spawn_pos.getX());
            position.putInt("spawn_y", this.spawn_pos.getY());
            position.putInt("spawn_z", this.spawn_pos.getZ());
        }
        allomancy_data.put("position", position);

        return allomancy_data;

    }

    @Override
    public void deserializeNBT(CompoundNBT allomancy_data) {

        CompoundNBT abilities = (CompoundNBT) allomancy_data.get("abilities");
        for (Metal mt : Metal.values()) {
            if (abilities.getBoolean(mt.getName())) {
                this.addPower(mt);
            } else {
                this.revokePower(mt);
            }
        }

        CompoundNBT metal_storage = (CompoundNBT) allomancy_data.get("metal_storage");
        for (Metal mt : Metal.values()) {
            this.setAmount(mt, metal_storage.getInt(mt.getName()));
        }

        CompoundNBT metal_burning = (CompoundNBT) allomancy_data.get("metal_burning");
        for (Metal mt : Metal.values()) {
            this.setBurning(mt, metal_burning.getBoolean(mt.getName()));
        }

        CompoundNBT position = (CompoundNBT) allomancy_data.get("position");
        if (position.contains("death_dimension")) {
            this.setDeathLoc(new BlockPos(position.getInt("death_x"), position.getInt("death_y"), position.getInt("death_z")), position.getString("death_dimension"));
        }
        if (position.contains("spawn_dimension")) {
            this.setSpawnLoc(new BlockPos(position.getInt("spawn_x"), position.getInt("spawn_y"), position.getInt("spawn_z")), position.getString("spawn_dimension"));
        }

    }


    public static class Storage implements Capability.IStorage<AllomancyCapability> {

        @Override
        public INBT writeNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }
    }
}
