package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.entity.Entity;
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

public class AllomancyCapability implements ICapabilitySerializable<CompoundNBT> {

    @CapabilityInject(AllomancyCapability.class)
    public static final Capability<AllomancyCapability> PLAYER_CAP = null;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    private LazyOptional<AllomancyCapability> handler;

    //todo seriously rethink this cap
    public static final int[] MAX_BURN_TIME = {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600};
    public static final int IRON = 0, STEEL = 1, TIN = 2, PEWTER = 3, ZINC = 4, BRASS = 5, COPPER = 6, BRONZE = 7;

    private byte allomancyPower = -1;

    private int damageStored = 0;
    private int[] BurnTime = {1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400};
    private int[] MetalAmounts = {0, 0, 0, 0, 0, 0, 0, 0};
    private boolean[] MetalBurning = {false, false, false, false, false, false, false, false};


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

    public AllomancyCapability() {
        handler = LazyOptional.of(() -> this);
    }

    /**
     * Get the player's allomancy power -1 is none, 0-7 are each misting, 8 is full Mistborn
     *
     * @return the player's allomancy Power
     */
    public byte getAllomancyPower() {
        return allomancyPower;
    }

    /**
     * Set the player's allomancy power
     *
     * @param pow the value to set
     */
    public void setAllomancyPower(byte pow) {
        this.allomancyPower = pow;
    }

    /**
     * Check if a specific metal is burning
     *
     * @param metal the index of the metal to check
     * @return whether or not it is burning
     */
    public boolean getMetalBurning(int metal) {
        return MetalBurning[metal];
    }

    /**
     * Set whether or not a metal is burning
     *
     * @param metal        the index of the metal to set
     * @param metalBurning the value to set
     */
    public void setMetalBurning(int metal, boolean metalBurning) {
        MetalBurning[metal] = metalBurning;
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
     * Get the amount of a specific metal
     *
     * @param metal the index of the metal to retrieve
     * @return the amount of metal
     */
    public int getMetalAmounts(int metal) {
        return metal >= 0 && metal < 8 ? MetalAmounts[metal] : 0;
    }

    /**
     * Set the amount of a specific metal
     *
     * @param metal        the index of the metal to set
     * @param metalAmounts the amount of metal
     */
    public void setMetalAmounts(int metal, int metalAmounts) {
        MetalAmounts[metal] = metalAmounts;
    }

    /**
     * Get the burn time of a specific metal
     *
     * @param metal the index of the metal to retrieve
     * @return the burn time
     */
    public int getBurnTime(int metal) {
        return BurnTime[metal];
    }

    /**
     * Set the burn time of a specific metal
     *
     * @param metal    the index of the metal to set
     * @param burnTime the burn time
     */
    public void setBurnTime(int metal, int burnTime) {
        BurnTime[metal] = burnTime;
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(AllomancyCapability.class, new AllomancyCapability.Storage(), () -> null);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT allomancy_data = new CompoundNBT();
        allomancy_data.putByte("allomancyPower", this.getAllomancyPower());

        CompoundNBT metal_storage = new CompoundNBT();
        metal_storage.putInt("iron", this.getMetalAmounts(0));
        metal_storage.putInt("steel", this.getMetalAmounts(1));
        metal_storage.putInt("tin", this.getMetalAmounts(2));
        metal_storage.putInt("pewter", this.getMetalAmounts(3));
        metal_storage.putInt("zinc", this.getMetalAmounts(4));
        metal_storage.putInt("brass", this.getMetalAmounts(5));
        metal_storage.putInt("copper", this.getMetalAmounts(6));
        metal_storage.putInt("bronze", this.getMetalAmounts(7));

        allomancy_data.put("metal_storage", metal_storage);

        CompoundNBT metal_burning = new CompoundNBT();
        metal_burning.putBoolean("iron", this.getMetalBurning(0));
        metal_burning.putBoolean("steel", this.getMetalBurning(1));
        metal_burning.putBoolean("tin", this.getMetalBurning(2));
        metal_burning.putBoolean("pewter", this.getMetalBurning(3));
        metal_burning.putBoolean("zinc", this.getMetalBurning(4));
        metal_burning.putBoolean("brass", this.getMetalBurning(5));
        metal_burning.putBoolean("copper", this.getMetalBurning(6));
        metal_burning.putBoolean("bronze", this.getMetalBurning(7));

        allomancy_data.put("metal_burning", metal_burning);

        return allomancy_data;

    }

    @Override
    public void deserializeNBT(CompoundNBT allomancy_data) {
        this.allomancyPower = allomancy_data.getByte("allomancyPower");

        CompoundNBT metal_storage = (CompoundNBT) allomancy_data.get("metal_storage");
        this.MetalAmounts[0] = metal_storage.getInt("iron");
        this.MetalAmounts[1] = metal_storage.getInt("steel");
        this.MetalAmounts[2] = metal_storage.getInt("tin");
        this.MetalAmounts[3] = metal_storage.getInt("pewter");
        this.MetalAmounts[4] = metal_storage.getInt("zinc");
        this.MetalAmounts[5] = metal_storage.getInt("brass");
        this.MetalAmounts[6] = metal_storage.getInt("copper");
        this.MetalAmounts[7] = metal_storage.getInt("bronze");

        CompoundNBT metal_burning = (CompoundNBT) allomancy_data.get("metal_burning");
        this.MetalBurning[0] = metal_burning.getBoolean("iron");
        this.MetalBurning[1] = metal_burning.getBoolean("steel");
        this.MetalBurning[2] = metal_burning.getBoolean("tin");
        this.MetalBurning[3] = metal_burning.getBoolean("pewter");
        this.MetalBurning[4] = metal_burning.getBoolean("zinc");
        this.MetalBurning[5] = metal_burning.getBoolean("brass");
        this.MetalBurning[6] = metal_burning.getBoolean("copper");
        this.MetalBurning[7] = metal_burning.getBoolean("bronze");

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
