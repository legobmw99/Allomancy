package com.legobmw99.allomancy.handlers;

import java.util.List;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.packets.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommonEventHandler {
    
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer && !event.getObject().hasCapability(Allomancy.PLAYER_CAP, null)) {
            event.addCapability(new ResourceLocation(Allomancy.MODID, "Allomancy_Data"), new AllomancyCapability(((EntityPlayer) event.getObject())));
        }
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP source = (EntityPlayerMP) event.getSource().getTrueSource();
            AllomancyCapability cap = AllomancyCapability.forPlayer(source);

            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                event.setAmount(event.getAmount() + 2);
            }
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityLiving());
            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                event.setAmount(event.getAmount() - 2);
                // Note that they took damage, will come in to play if they stop burning
                cap.setDamageStored(cap.getDamageStored() + 1);
            }
        }
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        String name = event.getName().toString();
        if (name.startsWith("minecraft:chests/simple_dungeon") || name.startsWith("minecraft:chests/desert_pyramid") || name.startsWith("minecraft:chests/jungle_temple")) {
            event.getTable().addPool(new LootPool(new LootEntry[] { new LootEntryTable(new ResourceLocation(Allomancy.MODID, "inject/lerasium"), 1, 0, new LootCondition[0], "allomancy_inject_entry") }, new LootCondition[0], new RandomValueRange(1),
                    new RandomValueRange(0, 1), "allomancy_inject_pool"));
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {

        AllomancyCapability oldCap = AllomancyCapability.forPlayer(event.getOriginal()); // the dead player's cap
        AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityPlayer()); // the clone's cap
        if (oldCap.getAllomancyPower() >= 0) {
            cap.setAllomancyPower(oldCap.getAllomancyPower()); // make sure the new player has the same mistborn status
            Registry.network.sendTo(new AllomancyPowerPacket(oldCap.getAllomancyPower()), (EntityPlayerMP) event.getEntity());
        }
        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory") || !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
            for (int i = 0; i < 8; i++) {
                cap.setMetalAmounts(i, oldCap.getMetalAmounts(i));
            }
        }

    }

    @SubscribeEvent
    public void onPlayerLogin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);
            Registry.network.sendTo(new AllomancyCapabilityPacket(cap, event.getEntity().getEntityId()), player);
            if (cap.getAllomancyPower() >= 0) {
                Registry.network.sendTo(new AllomancyPowerPacket(cap.getAllomancyPower()), player);
            } else if (AllomancyConfig.randomizeMistings && cap.getAllomancyPower() == -1) {

                int randomMisting = (int) (Math.random() * 8);

                cap.setAllomancyPower(randomMisting);
                Registry.network.sendTo(new AllomancyPowerPacket(randomMisting), player);
                ItemStack dust = new ItemStack(Item.getByNameOrId("allomancy:flake" + Registry.flakeMetals[randomMisting]));
                // Give the player one flake of their metal
                if (!player.inventory.addItemStackToInventory(dust)) {
                    EntityItem entity = new EntityItem(event.getEntity().getEntityWorld(), player.posX, player.posY, player.posZ, dust);
                    event.getEntity().getEntityWorld().spawnEntity(entity);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event){
    	Registry.initItems(event);
    }
    
    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event){
    	Registry.initBlocks(event);
    }
    
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            World world = (World) event.world;
            List<EntityPlayer> list = world.playerEntities;
            for (EntityPlayer curPlayer : list) {
                AllomancyCapability cap = AllomancyCapability.forPlayer(curPlayer);

                if (cap.getAllomancyPower() >= 0) {
                    // Run the necessary updates on the player's metals
                    if (curPlayer instanceof EntityPlayerMP) {
                        AllomancyUtils.updateMetalBurnTime(cap,(EntityPlayerMP) curPlayer);
                    }
                    // Damage the player if they have stored damage and pewter cuts out
                    if (!cap.getMetalBurning(AllomancyCapability.PEWTER) && (cap.getDamageStored() > 0)) {
                        cap.setDamageStored(cap.getDamageStored() - 1);
                        curPlayer.attackEntityFrom(DamageSource.GENERIC, 2);
                    }
                    if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                    	//Add jump boost and speed to pewter burners
                        curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(8), 30, 1, true, false));
                        curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(1), 30, 0, true, false));

                    }
                    if (cap.getMetalBurning(AllomancyCapability.TIN)) {
                        // Add night vision to tin-burners
                        curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 210, 5, true, false));
                        // Remove blindness for tin burners
                        if (curPlayer.isPotionActive(Potion.getPotionById(15))) {
                            curPlayer.removePotionEffect(Potion.getPotionById(15));

                        } else {
                            PotionEffect eff;
                            eff = curPlayer.getActivePotionEffect(Potion.getPotionById(16));

                        }

                    }
                    // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
                    if ((!cap.getMetalBurning(AllomancyCapability.TIN)) && (curPlayer.getActivePotionEffect(Potion.getPotionById(16)) != null && curPlayer.getActivePotionEffect(Potion.getPotionById(16)).getAmplifier() == 5)) {
                        if (curPlayer.getActivePotionEffect(Potion.getPotionById(16)).getDuration() < 201) {
                            curPlayer.removePotionEffect(Potion.getPotionById(16));
                        }
                    }
                }
            }
        }
    }
}
