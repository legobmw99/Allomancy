package com.legobmw99.allomancy.handlers;

import java.util.List;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.AllomancyConfig;
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
    
    //TODO: for some reason this does not function if moved into the Utils class
    /**
     * Runs each worldTick, checking the burn times, abilities, and metal amounts. Then syncs with the client to make sure everyone is on the same page
     * 
     * @param cap
     *            the AllomancyCapabilities data
     * @param player
     *            the player being checked
     */
    private static void updateMetalBurnTime(AllomancyCapabilities cap1, EntityPlayerMP player) {
        for (int i = 0; i < 8; i++) {
            if (cap1.getMetalBurning(i)) {
                if (cap1.getAllomancyPower() != i && cap1.getAllomancyPower() != 8) {
                    // put out any metals that the player shouldn't be able to burn
                    cap1.setMetalBurning(i, false);
                    Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap1, player.getEntityId()), player);
                } else {
                    cap1.setBurnTime(i, cap1.getBurnTime(i) - 1);
                    if (cap1.getBurnTime(i) == 0) {
                        cap1.setBurnTime(i, cap1.MaxBurnTime[i]);
                        cap1.setMetalAmounts(i, cap1.getMetalAmounts(i) - 1);
                        Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap1, player.getEntityId()), player);
                        if (cap1.getMetalAmounts(i) == 0) {
                            cap1.setMetalBurning(i, false);
                            Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap1, player.getEntityId()), player);
                        }
                    }
                }

            }
        }
    }
    
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer && !event.getObject().hasCapability(Allomancy.PLAYER_CAP, null)) {
            event.addCapability(new ResourceLocation(Allomancy.MODID, "Allomancy_Data"), new AllomancyCapabilities(((EntityPlayer) event.getObject())));
        }
    }

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
            EntityPlayerMP source = (EntityPlayerMP) event.getSource().getTrueSource();
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(source);

            if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
                event.setAmount(event.getAmount() + 2);
            }
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntityLiving());
            if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
                event.setAmount(event.getAmount() - 2);
                // Note that they took damage, will come in to play if they stop
                // burning
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

        AllomancyCapabilities oldCap = AllomancyCapabilities.forPlayer(event.getOriginal()); // the dead player's cap
        AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntityPlayer()); // the clone's cap
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
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
            Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap, event.getEntity().getEntityId()), player);
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
                AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(curPlayer);

                if (cap.getAllomancyPower() >= 0) {
                    // Run the necessary updates on the player's metals
                    if (curPlayer instanceof EntityPlayerMP) {
                        updateMetalBurnTime(cap,(EntityPlayerMP) curPlayer);
                    }
                    // Damage the player if they have stored damage and pewter cuts out
                    if (!cap.getMetalBurning(AllomancyCapabilities.matPewter) && (cap.getDamageStored() > 0)) {
                        cap.setDamageStored(cap.getDamageStored() - 1);
                        curPlayer.attackEntityFrom(DamageSource.GENERIC, 2);
                    }
                    if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
                        curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(8), 30, 1, false, false));
                    }
                    if (cap.getMetalBurning(AllomancyCapabilities.matTin)) {
                        // Add night vision to tin-burners
                        if (!curPlayer.isPotionActive(Potion.getPotionById(16))) {
                            curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 300, 0, false, false));
                        }
                        // Remove blindness for tin burners
                        if (curPlayer.isPotionActive(Potion.getPotionById(15))) {
                            curPlayer.removePotionEffect(Potion.getPotionById(15));

                        } else {
                            PotionEffect eff;
                            eff = curPlayer.getActivePotionEffect(Potion.getPotionById(16));
                            // Fix for the flashing that occurs when nightvision
                            // effect is about to run out
                            if (eff.getDuration() < 210) {
                                curPlayer.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 300, 0, false, false));
                            }
                        }

                    }
                    // Remove night vision from non-tin burners if duration < 10
                    // seconds. Related to the above issue with flashing
                    if ((!cap.getMetalBurning(AllomancyCapabilities.matTin)) && curPlayer.isPotionActive(Potion.getPotionById(16))) {
                        if (curPlayer.getActivePotionEffect(Potion.getPotionById(16)).getDuration() < 201) {
                            curPlayer.removePotionEffect(Potion.getPotionById(16));
                        }
                    }
                }
            }
        }
    }
}
