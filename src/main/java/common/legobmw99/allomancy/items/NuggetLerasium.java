package common.legobmw99.allomancy.items;

import java.util.List;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;

public class NuggetLerasium extends ItemFood {
	public NuggetLerasium() {
		super(0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(false);
		this.setUnlocalizedName("nuggetLerasium");
		this.setCreativeTab(Registry.tabsAllomancy);
		this.maxStackSize = 1;
	}

	public int getHealAmount() {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.EAT;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand){
		AllomancyData.forPlayer(playerIn);
		if (!AllomancyData.isMistborn) {
	        playerIn.setActiveHand(hand);
	        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);	

		} else {
        return new ActionResult(EnumActionResult.FAIL, itemStackIn);	
		}
	}

	public ItemStack onItemUseFinish(ItemStack item, World world, EntityPlayer player) {
		AllomancyData.forPlayer(player);
		double x = player.posX;
		double y = player.posY + 3;
		double z = player.posZ;
		if (AllomancyData.isMistborn == false) {
			AllomancyData.isMistborn = true;

		}
		//Fancy shmancy effects
		world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z, true));
		player.addPotionEffect(new PotionEffect(Potion.getPotionById(12),
				20, 0, true, false));
		player.addStat(Registry.becomeMistborn, 1);
		return super.onItemUseFinish(item, world, player);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\u00A75This item is endowed with strange powers");
		par3List.add("\u00A75Perhaps you should ingest it?");
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		//Add enchantment glint
        return true;
	}
}