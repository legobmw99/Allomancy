package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.entities.EntityGoldNugget;
import com.legobmw99.allomancy.entities.EntityIronNugget;
import com.legobmw99.allomancy.util.AllomancyCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CoinBagItem extends ShootableItem {
    public static final Predicate<ItemStack> NUGGETS = (stack) -> {
        return stack.getItem() == Items.IRON_NUGGET || stack.getItem() == Items.GOLD_NUGGET;
    };

    public CoinBagItem(Item.Properties properties) {
        super(properties);
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "coin_bag"));
    }

    @Nonnull
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return NUGGETS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.func_213356_f(player.getHeldItem(hand));

        if ( AllomancyCapability.forPlayer(player).getMetalBurning(AllomancyCapability.STEEL)) {    // make sure there is always an item available

            if (itemstack.getItem() == Items.GOLD_NUGGET) {
                EntityGoldNugget entitygold = new EntityGoldNugget(world, player);
                world.addEntity(entitygold);
            }

            if (itemstack.getItem() == Items.IRON_NUGGET) {
                EntityIronNugget entityiron = new EntityIronNugget(world, player);
                world.addEntity(entityiron);
            }

            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
        }
        return new ActionResult<ItemStack>(ActionResultType.PASS, player.getHeldItem(hand));
    }


    @Override
    public int getItemEnchantability() {
        return 0;
    }
}