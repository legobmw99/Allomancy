package common.legobmw99.allomancy.items;

import java.util.List;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.network.packets.AllomancyBecomeMistbornPacket;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NuggetLerasium extends ItemFood {
	public NuggetLerasium() {
		super(0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(false);
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
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack,
				this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}

	public ItemStack onFoodEaten(ItemStack item, World world, EntityPlayer player) {
		AllomancyData.forPlayer(player);
		double x = player.posX;
		double y = player.posY + 3;
		double z = player.posZ;
		if (AllomancyData.isMistborn == false) {
			AllomancyData.isMistborn = true;
			Allomancy.packetPipeline
					.sendToServer(new AllomancyBecomeMistbornPacket());
		}
		world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
		player.addPotionEffect(new PotionEffect(Potion.fireResistance.getId(),
				20, 0, true, false));
		player.addStat(Registry.becomeMistborn, 1);
		return super.onFoodEaten(item, world, player);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("\u00A75This item is endowed with strange powers");
		par3List.add("\u00A75Perhaps you should ingest it?");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack) {
		return true;
	}
}
