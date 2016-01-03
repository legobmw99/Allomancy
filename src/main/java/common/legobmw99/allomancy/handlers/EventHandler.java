package common.legobmw99.allomancy.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.common.Registry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class EventHandler {

	@SubscribeEvent
	public void onDamage(LivingHurtEvent event) {

		if (event.source.getSourceOfDamage() instanceof EntityPlayerMP) {
			EntityPlayerMP source = (EntityPlayerMP) event.source
					.getSourceOfDamage();
			AllomancyData data;
			data = AllomancyData.forPlayer(source);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount += 2;
			}
		}
		if (event.entityLiving instanceof EntityPlayerMP) {
			AllomancyData data = AllomancyData.forPlayer(event.entityLiving);
			if (data.MetalBurning[AllomancyData.matPewter]) {
				event.ammount -= 2;
				data.damageStored++;
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		// Allomancy.MPC.particleBlockTargets.clear();
	}

	/*
	 * @SubscribeEvent public void tickEvent(TickEvent event){ AllomancyData
	 * data; Side side = FMLCommonHandler.instance().getEffectiveSide();
	 * if(event.phase == Phase.END){ if (event.type == Type.PLAYER) { if (side
	 * == Side.CLIENT){ //clientTick(); } else { World world; world = (World)
	 * event;
	 * 
	 * List<EntityPlayerMP> list = world.playerEntities;
	 * 
	 * for (EntityPlayerMP curPlayer : list) {
	 * 
	 * data = AllomancyData.forPlayer(curPlayer); if(AllomancyData.isMistborn ==
	 * true){ if (!data.MetalBurning[AllomancyData.matPewter] &&
	 * data.damageStored > 0) { data.damageStored--;
	 * curPlayer.attackEntityFrom(DamageSource.generic, 2); } if (side ==
	 * Side.CLIENT){ updateBurnTime(data, curPlayer); } if
	 * (data.MetalBurning[AllomancyData.matTin]) {
	 * 
	 * if (!curPlayer.isPotionActive(Potion.nightVision.getId()))
	 * curPlayer.addPotionEffect(new PotionEffect( Potion.nightVision.getId(),
	 * 300, 0, true)); else { PotionEffect eff; eff = curPlayer
	 * .getActivePotionEffect(Potion.nightVision); if (eff.getDuration() < 210)
	 * { curPlayer.addPotionEffect(new PotionEffect( Potion.nightVision.getId(),
	 * 300, 0, true)); } }
	 * 
	 * } if (data.MetalBurning[AllomancyData.matTin] == false &&
	 * curPlayer.isPotionActive(Potion.nightVision.getId())) { if
	 * (curPlayer.getActivePotionEffect(Potion.nightVision).getDuration() < 201)
	 * { curPlayer.removePotionEffect(Potion.nightVision.getId()); } }
	 * 
	 * } } } } } }
	 */
	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent event) {
		ItemStack cur;
		for (int x = 0; x < event.craftMatrix.getSizeInventory(); x++) {
			cur = event.craftMatrix.getStackInSlot(x);
			if (cur == null)
				continue;
			if (cur == new ItemStack(Registry.itemAllomancyGrinder)) {
				cur.damageItem(1, event.player);
			}

		}
	}

}
