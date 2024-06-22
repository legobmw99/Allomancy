package com.legobmw99.allomancy.api.block;

import net.minecraft.world.entity.player.Player;

/**
 * This interface can be used to signify if a block should react to being pushed
 * or pulled, rather than moving the pusher
 *
 * @author legobmw99
 * @see com.legobmw99.allomancy.modules.extras.ExtrasSetup#ALLOMANTICALLY_USABLE_BLOCK
 */
public interface IAllomanticallyUsable {

    /**
     * Called when the block is steelpushed or ironpulled
     *
     * @param isPush whether the activation is Steel
     * @return whether the block was activated
     */
    boolean useAllomantically(Player player, boolean isPush);
}
