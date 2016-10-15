package common.legobmw99.allomancy.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;

public class ItemVial extends Item{
	public static String[] unlocalName = { "emptyvial", "ironelixer",
		"steelelixer", "tinelixer", "pewterelixer", "zincelixer",
		"brasselixer", "copperelixer", "bronzeelixer", };

	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
		AllomancyCapabilities cap;
		cap = AllomancyCapabilities.forPlayer(entityLiving);
		
		if (((EntityPlayer)(entityLiving)).capabilities.isCreativeMode != true) {
			stack.stackSize--; 
			((EntityPlayer)(entityLiving)).inventory.addItemStackToInventory(new ItemStack(Registry.itemVial, 1,0));
		}
		if (cap == null) {
			return stack;
		}

		if (stack.getItemDamage() == 0)
			return stack;
		
			if (cap.getMetalAmounts(stack.getItemDamage() - 1) < 10) {
				cap.setMetalAmounts(stack.getItemDamage() - 1, cap.getMetalAmounts(stack.getItemDamage() - 1) + 1 );
			}
		return stack;
	}


	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 6;
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand){

		AllomancyCapabilities cap;
		cap = AllomancyCapabilities.forPlayer(playerIn);
		//Checks both the metal amount (we only want to fill up to 10) and the item damage (can't drink empty vials)
		if (itemStackIn.getItemDamage() > 0){
			if (cap.getMetalAmounts(itemStackIn.getItemDamage() - 1) < 10) {
		        playerIn.setActiveHand(hand);
		        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);	

			} else {
		        return new ActionResult(EnumActionResult.FAIL, itemStackIn);
			}} else {
	        return new ActionResult(EnumActionResult.FAIL, itemStackIn);		
			}
		}
		
	

	public ItemVial() {
		this.setHasSubtypes(true);
		this.setCreativeTab(Registry.tabsAllomancy);
	}


	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int meta = itemStack.getItemDamage();
		if ((meta < 0) || (meta >= unlocalName.length)) {
			meta = 0;
		}
		return "item.itemVial" + "." + unlocalName[meta];
	}

	

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int meta = 0; meta < 9; meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}
}
