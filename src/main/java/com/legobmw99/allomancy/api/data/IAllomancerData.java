package com.legobmw99.allomancy.api.data;

import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAllomancerData {

    /**
     * Called each worldTick, checking the burn times, abilities, and metal
     * amounts. Then syncs to the client to make sure everyone is on the same
     * page
     *
     * @param player the player being checked
     */
    void tickBurning(ServerPlayerEntity player);

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
     * Sets the players amount of Metal to the given value
     *
     * @param metal the Metal to set
     * @param amt   the amount stored
     */
    void setAmount(Metal metal, int amt);

    /**
     * Gets the players stored amount of the given metal
     *
     * @param metal the Metal to check
     * @return the amount stored
     */
    int getAmount(Metal metal);

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
     * Set the death location and dimension
     *
     * @param pos BlockPos of the death location
     * @param dim The RegistryKey representing the dimension the death occurred in
     */
    void setDeathLoc(BlockPos pos, RegistryKey<World> dim);

    void setDeathLoc(BlockPos pos, String dim_name);

    /**
     * Returns the location of the most recent player's death, or null
     *
     * @return BlockPos of player's death, or null
     */
    BlockPos getDeathLoc();

    /**
     * Returns the dimension of the most recent player's death, or null
     *
     * @return RegistryKey corresponding to the dimension, or null
     */
    RegistryKey<World> getDeathDim();

    /**
     * Set the spawn location and dimension
     *
     * @param pos BlockPos of the spawn point
     * @param dim The RegistryKey representing the spawn dimension
     */
    void setSpawnLoc(BlockPos pos, RegistryKey<World> dim);

    void setSpawnLoc(BlockPos pos, String dim_name);

    /**
     * Returns the location of the players spawn point if set, or null
     *
     * @return BlockPos of player's death, or null
     */
    BlockPos getSpawnLoc();

    /**
     * Returns the dimension of the most player's spawn point, or null if unset.
     *
     * @return RegistryKey corresponding to the dimension, or null
     */
    RegistryKey<World> getSpawnDim();

    void decEnhanced();

    boolean isEnhanced();

    void setEnhanced(int time);

}
