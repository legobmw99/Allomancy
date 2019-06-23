package com.legobmw99.allomancy.util;

import java.util.concurrent.Callable;

import com.legobmw99.allomancy.Allomancy;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class AllomancyCapability implements ICapabilitySerializable<CompoundNBT> {

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "Allomancy_Data");
    public static final int[] MAX_BURN_TIME = { 1800, 1800, 3600, 600, 1800, 1800, 2400, 1600 };
    public static final int IRON = 0, STEEL = 1, TIN = 2, PEWTER = 3, ZINC = 4, BRASS = 5, COPPER = 6, BRONZE = 7;

    private int allomancyPower = -1;

    private int damageStored = 0;
    private int[] BurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
    private int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
    private boolean[] MetalBurning = { false, false, false, false, false, false, false, false };


    /**
     * Retrieve data for a specific player
     * 
     * @param player
     *            the player you want data for
     * @return the AllomancyCapabilites data of the player
     */
    public static AllomancyCapability forPlayer(Entity player) {
        return player.getCapability(Allomancy.PLAYER_CAP, null).cast(AllomancyCapability);
    }

    /**
     * Constructor of the Capability object
     * 
     */
    public AllomancyCapability() {
    }


    /**
     * Get the player's allomancy power -1 is none, 0-7 are each misting, 8 is full Mistborn
     * 
     * @return the player's allomancy Power
     */
    public int getAllomancyPower() {
        return allomancyPower;
    }

    /**
     * Set the player's allomancy power
     * 
     * @param pow
     *            the value to set
     */
    public void setAllomancyPower(int pow) {
        this.allomancyPower = pow;
    }

    /**
     * Check if a specific metal is burning
     * 
     * @param metal
     *            the index of the metal to check
     * @return whether or not it is burning
     */
    public boolean getMetalBurning(int metal) {
        return MetalBurning[metal];
    }

    /**
     * Set whether or not a metal is burning
     * 
     * @param metal
     *            the index of the metal to set
     * @param metalBurning
     *            the value to set
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
     * @param damageStored
     *            the amount of damage
     */
    public void setDamageStored(int damageStored) {
        this.damageStored = damageStored;
    }

    /**
     * Get the amount of a specific metal
     * 
     * @param metal
     *            the index of the metal to retrieve
     * @return the amount of metal
     */
    public int getMetalAmounts(int metal) {
        return metal >= 0 && metal < 8 ? MetalAmounts[metal] : 0;
    }

    /**
     * Set the amount of a specific metal
     * 
     * @param metal
     *            the index of the metal to set
     * @param metalAmounts
     *            the amount of metal
     */
    public void setMetalAmounts(int metal, int metalAmounts) {
        MetalAmounts[metal] = metalAmounts;
    }

    /**
     * Get the burn time of a specific metal
     * 
     * @param metal
     *            the index of the metal to retrieve
     * @return the burn time
     */
    public int getBurnTime(int metal) {
        return BurnTime[metal];
    }

    /**
     * Set the burn time of a specific metal
     * 
     * @param metal
     *            the index of the metal to set
     * @param burnTime
     *            the burn time
     */
    public void setBurnTime(int metal, int burnTime) {
        BurnTime[metal] = burnTime;
    }

    /**
     * Register the capability
     */
    public static void register() {
        CapabilityManager.INSTANCE.register(AllomancyCapability.class, new AllomancyCapability.Storage(), new AllomancyCapability.Factory());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.setInteger("allomancyPower", this.getAllomancyPower());
        nbt.setInteger("iron", this.getMetalAmounts(0));
        nbt.setInteger("steel", this.getMetalAmounts(1));
        nbt.setInteger("tin", this.getMetalAmounts(2));
        nbt.setInteger("pewter", this.getMetalAmounts(3));
        nbt.setInteger("zinc", this.getMetalAmounts(4));
        nbt.setInteger("brass", this.getMetalAmounts(5));
        nbt.setInteger("copper", this.getMetalAmounts(6));
        nbt.setInteger("bronze", this.getMetalAmounts(7));
        nbt.setBoolean("iron_b", this.getMetalBurning(0));
        nbt.setBoolean("steel_b", this.getMetalBurning(1));
        nbt.setBoolean("tin_b", this.getMetalBurning(2));
        nbt.setBoolean("pewter_b", this.getMetalBurning(3));
        nbt.setBoolean("zinc_b", this.getMetalBurning(4));
        nbt.setBoolean("brass_b", this.getMetalBurning(5));
        nbt.setBoolean("copper_b", this.getMetalBurning(6));
        nbt.setBoolean("bronze_b", this.getMetalBurning(7));
        return nbt;

    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        this.allomancyPower = compound.getInteger("allomancyPower");
        this.MetalAmounts[0] = compound.getInteger("iron");
        this.MetalAmounts[1] = compound.getInteger("steel");
        this.MetalAmounts[2] = compound.getInteger("tin");
        this.MetalAmounts[3] = compound.getInteger("pewter");
        this.MetalAmounts[4] = compound.getInteger("zinc");
        this.MetalAmounts[5] = compound.getInteger("brass");
        this.MetalAmounts[6] = compound.getInteger("copper");
        this.MetalAmounts[7] = compound.getInteger("bronze");
        this.MetalBurning[0] = compound.getBoolean("iron_b");
        this.MetalBurning[1] = compound.getBoolean("steel_b");
        this.MetalBurning[2] = compound.getBoolean("tin_b");
        this.MetalBurning[3] = compound.getBoolean("pewter_b");
        this.MetalBurning[4] = compound.getBoolean("zinc_b");
        this.MetalBurning[5] = compound.getBoolean("brass_b");
        this.MetalBurning[6] = compound.getBoolean("copper_b");
        this.MetalBurning[7] = compound.getBoolean("bronze_b");
        if (compound.getBoolean("ismistborn")) {
            this.setAllomancyPower(8);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, Direction facing) {
        return Allomancy.PLAYER_CAP != null && capability == Allomancy.PLAYER_CAP;

    }

    @Override
    public <T> T getCapability(Capability<T> capability, Direction facing) {
        return Allomancy.PLAYER_CAP != null && capability == Allomancy.PLAYER_CAP ? (T) this : null;
    }

    public static class Storage implements Capability.IStorage<AllomancyCapability> {

        @Override
        public NBTBase writeNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<AllomancyCapability> capability, AllomancyCapability instance, Direction side, NBTBase nbt) {

        }

    }

    public static class Factory implements Callable<AllomancyCapability> {
        @Override
        public AllomancyCapability call() throws Exception {
            return null;
        }
    }

}
