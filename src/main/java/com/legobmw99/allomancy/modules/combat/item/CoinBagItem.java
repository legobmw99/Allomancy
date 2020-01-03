package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.modules.combat.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.modules.combat.entity.IronNuggetEntity;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.AllomancySetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CoinBagItem extends ShootableItem {

    public static final Predicate<ItemStack> NUGGETS = (stack) -> stack.getItem() == Items.IRON_NUGGET || stack.getItem() == Items.GOLD_NUGGET;

    public CoinBagItem() {
        super(new Item.Properties().group(AllomancySetup.allomancy_group).maxStackSize(1));
    }

    @Nonnull
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return NUGGETS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.findAmmo(player.getHeldItem(hand));
        if (itemstack.getItem() instanceof ArrowItem) { // the above get function has silly default behavior
            itemstack = new ItemStack(Items.GOLD_NUGGET, 1);
        }

        if (AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.STEEL)) {    // make sure there is always an item available
            if (!world.isRemote) {

                if (itemstack.getItem() == Items.GOLD_NUGGET) {
                    GoldNuggetEntity entitygold = new GoldNuggetEntity(player, world);
                    entitygold.shoot(player, player.rotationPitch, player.rotationYawHead, 2.0F, 4.0F, 1.0F);
                    world.addEntity(entitygold);
                }

                if (itemstack.getItem() == Items.IRON_NUGGET) {
                    IronNuggetEntity entityiron = new IronNuggetEntity(player, world);
                    entityiron.shoot(player, player.rotationPitch, player.rotationYawHead, 2.0F, 2.25F, 2.5F);
                    world.addEntity(entityiron);

                }

                if (!player.abilities.isCreativeMode) {
                    itemstack.shrink(1);
                }

                return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));

            }


        }
        return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));

    }


    @Override
    public int getItemEnchantability() {
        return 0;
    }
}