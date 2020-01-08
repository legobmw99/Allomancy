package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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

    private static final int[] MAX_BURN_TIME = {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600};


    private boolean[] allomanticPowers;
    private int damageStored;
    private int[] BurnTime;
    private int[] MetalAmounts;
    private boolean[] MetalBurning;

    private LazyOptional<AllomancyCapability> handler;

    public AllomancyCapability() {
        handler = LazyOptional.of(() -> this);

        int powers = Metal.values().length;
        allomanticPowers = new boolean[powers];
        Arrays.fill(allomanticPowers, false);

        MetalAmounts = new int[powers];
        Arrays.fill(MetalAmounts, 0);

        BurnTime = Arrays.copyOf(MAX_BURN_TIME, powers);

        MetalBurning = new boolean[powers];
        Arrays.fill(MetalBurning, false);

        damageStored = 0;
    }


    /**
     * Get if the player has the supplied power
     *
     * @param metal the Metal to check
     * @return true if this capability has the power specified
     */
    public boolean hasPower(Metal metal) {
        return allomanticPowers[metal.getIndex()];
    }

    /**
     * Check if the player is a Mistborn
     *
     * @return true if the player has ALL powers
     */
    public boolean isMistborn() {
        for (boolean power : allomanticPowers) {
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
        Arrays.fill(allomanticPowers, true);
    }

    /**
     * Check if the player is uninvested
     *
     * @return true if the player has NO powers
     */
    public boolean isUninvested() {
        for (boolean power : allomanticPowers) {
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
        Arrays.fill(allomanticPowers, false);
    }


    /**
     * Grant this player the given metal power
     *
     * @param metal the Metal to add
     */
    public void addPower(Metal metal) {
        allomanticPowers[metal.getIndex()] = true;
    }

    /**
     * Remove the given metal power from this player
     *
     * @param metal the Metal to remove
     */
    public void revokePower(Metal metal) {
        allomanticPowers[metal.getIndex()] = false;
    }

    /**
     * Checks if the player is burning the given metal
     *
     * @param metal the Metal to check
     * @return true if the player is burning it
     */
    public boolean isBurning(Metal metal) {
        return MetalBurning[metal.getIndex()];
    }

    /**
     * Sets the player's burning flag for the given metal
     *
     * @param metal        the Metal to set
     * @param metalBurning the value to set it to
     */
    public void setBurning(Metal metal, boolean metalBurning) {
        MetalBurning[metal.getIndex()] = metalBurning;
    }

    /**
     * Gets the players stored amount of the given metal
     *
     * @param metal the Metal to check
     * @return the amount stored
     */
    public int getAmount(Metal metal) {
        return MetalAmounts[metal.getIndex()];
    }

    /**
     * Sets the players amount of Metal to the given value
     *
     * @param metal the Metal to set
     * @param amt   the amount stored
     */
    public void setAmount(Metal metal, int amt) {
        MetalAmounts[metal.getIndex()] = amt;
    }

    /**
     * Get how much damage has been accumulated
     *
     * @return the amount of damage
     */
    public int getDamageStored() {
        return damageStored;
    }

    /**
     * Set the amount of damage stored
     *
     * @param damageStored the amount of damage
     */
    public void setDamageStored(int damageStored) {
        this.damageStored = damageStored;
    }

    /**
     * Get the burn time of a specific metal
     *
     * @param metal the metal to retrieve
     * @return the burn time
     */
    protected int getBurnTime(Metal metal) {
        return BurnTime[metal.getIndex()];
    }

    /**
     * Set the burn time of a specific metal
     *
     * @param metal    the the metal to set
     * @param burnTime the burn time
     */
    protected void setBurnTime(Metal metal, int burnTime) {
        BurnTime[metal.getIndex()] = burnTime;
    }


    /**
     * Runs each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param capability the AllomancyCapabilities data
     * @param player     the player being checked
     */
    public static void updateMetalBurnTime(AllomancyCapability capability, ServerPlayerEntity player) {
        for (Metal metal : Metal.values()) {
            if (capability.isBurning(metal)) {
                if (!capability.hasPower(metal)) {
                    // put out any metals that the player shouldn't be able to burn
                    capability.setBurning(metal, false);
                    Network.sync(capability, player);
                } else {
                    capability.setBurnTime(metal, capability.getBurnTime(metal) - 1);
                    if (capability.getBurnTime(metal) == 0) {
                        if (capability.getAmount(metal) == 0) {
                            capability.setBurning(metal, false);
                        } else {
                            capability.setBurnTime(metal, MAX_BURN_TIME[metal.getIndex()]);
                            capability.setAmount(metal, capability.getAmount(metal) - 1);
                        }
                        Network.sync(capability, player);
                    }
                }
            }
        }
    }


    /**
     * Retrieve data for a specific player
     *
     * @param player the player you want data for
     * @return the AllomancyCapabilites data of the player
     */
    public static AllomancyCapability forPlayer(Entity player) {
        return player.getCapability(PLAYER_CAP).orElseThrow(() -> new RuntimeException("Capability not attached!"));
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_CAP.orEmpty(cap, handler);
    }


    public static void register() {
        CapabilityManager.INSTANCE.register(AllomancyCapability.class, new AllomancyCapability.Storage(), () -> null);
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

        return allomancy_data;

    }

    @Override
    public void deserializeNBT(CompoundNBT allomancy_data) {

        if (allomancy_data.contains("allomancyPower")) {
            byte old_power = allomancy_data.getByte("allomancyPower");
            if (old_power != -1) {
                if (old_power == 8) {
                    this.setMistborn();
                } else {
                    this.addPower(Metal.getMetal(old_power));
                }
            }
        }

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
