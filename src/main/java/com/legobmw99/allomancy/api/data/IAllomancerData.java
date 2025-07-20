package com.legobmw99.allomancy.api.data;

import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IAllomancerData {

    /**
     * Maximum amount of metal that can be stored.
     */
    int MAX_STORAGE = 10;

    /**
     * Called each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @return whether an observable change was made
     */
    boolean tickBurning();

    /**
     * Get if the player has the supplied power
     *
     * @param metal the Metal to check
     * @return true if this capability has the power specified
     */
    boolean hasPower(Metal metal);

    /**
     * Get the number of powers the player has
     *
     * @return int between 0 and 16, inclusive
     */
    int getPowerCount();

    /**
     * Returns an array of the player's metal abilities
     *
     * @return array of Metal
     */
    Metal[] getPowers();

    /**
     * Sets the player as a Mistborn
     */
    void setMistborn();

    /**
     * Check if the player is a Mistborn
     *
     * @return true if the player has ALL powers
     */
    boolean isMistborn();

    /**
     * Check if the player is uninvested
     *
     * @return true if the player has NO powers
     */
    boolean isUninvested();

    /**
     * Sets the player as uninvested
     */
    void setUninvested();

    /**
     * Grant this player the given metal power
     *
     * @param metal the Metal to add
     */
    void addPower(Metal metal);

    /**
     * Remove the given metal power from this player
     *
     * @param metal the Metal to remove
     */
    void revokePower(Metal metal);

    /**
     * Checks if the player is burning the given metal
     *
     * @param metal the Metal to check
     * @return true if the player is burning it
     */
    boolean isBurning(Metal metal);

    /**
     * Sets the player's burning flag for the given metal
     *
     * @param metal        the Metal to set
     * @param metalBurning the value to set it to
     */
    void setBurning(Metal metal, boolean metalBurning);

    /**
     * Increase the amount of this metal being stored
     *
     * @param metal the Metal to store more off
     */
    void incrementStored(Metal metal);

    /**
     * Decrease the amount of this metal being stored
     *
     * @param metal the Metal to store less of
     */
    void decrementStored(Metal metal);

    /**
     * Gets the players stored amount of the given metal
     *
     * @param metal the Metal to check
     * @return the amount stored
     */
    int getStored(Metal metal);

    /**
     * Drain all specified metals
     *
     * @param metals all metals to drain
     */
    void drainMetals(Metal... metals);

    /**
     * Get how much damage has been accumulated
     *
     * @return the amount of damage
     */
    int getDamageStored();

    /**
     * Set the amount of damage stored
     *
     * @param damageStored the amount of damage
     */
    void setDamageStored(int damageStored);


    /**
     * Set the spawn location and dimension
     *
     * @param pos BlockPos of the spawn point
     * @param dim The RegistryKey representing the spawn dimension
     */
    void setSpawnLoc(@Nullable BlockPos pos, @Nullable ResourceKey<Level> dim);

    /**
     * Returns the location of the players spawn point if set, or null
     *
     * @return BlockPos and dimension of player's spawn point, or null
     */
    @Nullable GlobalPos getSpawnLoc();


    /**
     * Set the location we're trying to find with an earring
     *
     * @param pos BlockPos of the point
     * @param dim The RegistryKey representing the dimension
     */
    void setSpecialSeekingLoc(@Nullable BlockPos pos, @Nullable ResourceKey<Level> dim);

    /**
     * Returns the location of the players seeking point if set, or null
     *
     * @return BlockPos and dimension of player's seeking point, or null
     */
    @Nullable GlobalPos getSpecialSeekingLoc();

    void decrementEnhanced();

    boolean isEnhanced();

    void setEnhanced(int time);

    void load(CompoundTag nbt);

    CompoundTag save();
}