package common.legobmw99.allomancy.items;

import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.entity.EntityIronNugget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemCoinBag extends Item {
    public ItemCoinBag() {
        super();
        this.setUnlocalizedName("coinbag");
        this.setCreativeTab(Registry.tabsAllomancy);
        this.maxStackSize = 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {

        ItemStack itemstack = this.findArrow(playerIn);
        if (playerIn.capabilities.isCreativeMode || itemstack != null && AllomancyCapabilities.forPlayer(playerIn).getMetalBurning(AllomancyCapabilities.matSteel)) {
            
            if (itemstack.getItem() == Items.GOLD_NUGGET) {
                EntityGoldNugget entitygold = new EntityGoldNugget(worldIn, playerIn);
                worldIn.spawnEntity(entitygold);
            }
            
            if (itemstack.getItem() == Items.field_191525_da) {
                EntityIronNugget entityiron = new EntityIronNugget(worldIn, playerIn);
                worldIn.spawnEntity(entityiron);
            }
            
            if (!playerIn.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }
        }
        return new ActionResult(EnumActionResult.PASS, playerIn.getHeldItem(hand));
    }

    /*
     * Finds items in inventory
     */
    private ItemStack findArrow(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (this.isArrow(itemstack)) {
                if (itemstack.getCount() == 1) {
                    player.inventory.removeStackFromSlot(i);
                    return itemstack;

                }
                return itemstack;
            }
        }

        return null;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack != null && (stack.getItem() == Items.GOLD_NUGGET || stack.getItem() == Items.field_191525_da /* IRON_NUGGET */);
    }

    public int getItemEnchantability() {
        return 0;
    }
}