package com.legobmw99.allomancy.handlers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.network.NetworkHelper;
import com.legobmw99.allomancy.network.packets.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class CommonEventHandler {
    @SubscribeEvent
    public void onAttachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(AllomancyCapability.IDENTIFIER, new AllomancyCapability());
        }
    }


    @SubscribeEvent
    public void onJoinWorld(final PlayerLoggedInEvent  event) {
        if (!event.getPlayer().world.isRemote()) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                AllomancyCapability cap = AllomancyCapability.forPlayer(player);

                NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
                if (cap.getAllomancyPower() >= 0) {
                    NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
                } else if (AllomancyConfig.random_mistings && cap.getAllomancyPower() == -1) {

                    byte randomMisting = (byte) (Math.random() * 8);

                    cap.setAllomancyPower(randomMisting);
                    NetworkHelper.sendTo(new AllomancyPowerPacket(randomMisting), player);
                    Allomancy.LOGGER.info("Assigned " + Registry.flake_metals[randomMisting] + " misting");
                    ItemStack dust = new ItemStack(net.minecraft.util.registry.Registry.ITEM.getValue(new ResourceLocation(Allomancy.MODID, Registry.flake_metals[randomMisting] + "_flakes")).get());
                    // Give the player one flake of their metal
                    if (!player.inventory.addItemStackToInventory(dust)) {
                        ItemEntity entity = new ItemEntity(player.getEntityWorld(), player.posX, player.posY, player.posZ, dust);
                        player.getEntityWorld().addEntity(entity);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone event) {
        AllomancyCapability oldCap = AllomancyCapability.forPlayer(event.getOriginal()); // the dead player's cap
        AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityPlayer()); // the clone's cap
        if (oldCap.getAllomancyPower() >= 0) {
            cap.setAllomancyPower(oldCap.getAllomancyPower()); // make sure the new player has the same mistborn status
            NetworkHelper.sendTo(new AllomancyPowerPacket(oldCap.getAllomancyPower()), (ServerPlayerEntity) event.getEntity());
        }
        if (event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory") || !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
            for (int i = 0; i < 8; i++) {
                cap.setMetalAmounts(i, oldCap.getMetalAmounts(i));
                NetworkHelper.sendTo(new AllomancyCapabilityPacket(cap, event.getEntity().getEntityId()), (ServerPlayerEntity) event.getEntity());

            }
        }
    }

    @SubscribeEvent
    public void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
            ServerPlayerEntity source = (ServerPlayerEntity) event.getSource().getTrueSource();
            AllomancyCapability cap = AllomancyCapability.forPlayer(source);

            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                event.setAmount(event.getAmount() + 2);
            }
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityLiving());
            if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                event.setAmount(event.getAmount() - 2);
                // Note that they took damage, will come in to play if they stop burning
                cap.setDamageStored(cap.getDamageStored() + 1);
            }
        }
    }


    /*todo investigate
     @SubscribeEvent
     public void onLootTableLoad(LootTableLoadEvent event) {
         String name = event.getName().toString();
         if (name.startsWith("minecraft:chests/simple_dungeon") || name.startsWith("minecraft:chests/desert_pyramid") || name.startsWith("minecraft:chests/jungle_temple")) {
             event.getTable().addPool(new LootPool(new ILootGenerator[]{new TableLootEntry(new ResourceLocation(Allomancy.MODID, "inject/lerasium"), 1, 0, new ILootCondition[0], "allomancy_inject_entry")}, new ILootCondition[0], new RandomValueRange(1),
                     new RandomValueRange(0, 1), "allomancy_inject_pool"));
         }
     }*/


    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            World world = (World) event.world;
            List<? extends PlayerEntity> list = world.getPlayers();
            for (PlayerEntity curPlayer : list) {
                AllomancyCapability cap = AllomancyCapability.forPlayer(curPlayer);

                if (cap.getAllomancyPower() >= 0) {
                    // Run the necessary updates on the player's metals
                    if (curPlayer instanceof ServerPlayerEntity) {
                        AllomancyUtils.updateMetalBurnTime(cap, (ServerPlayerEntity) curPlayer);
                    }
                    // Damage the player if they have stored damage and pewter cuts out
                    if (!cap.getMetalBurning(AllomancyCapability.PEWTER) && (cap.getDamageStored() > 0)) {
                        cap.setDamageStored(cap.getDamageStored() - 1);
                        curPlayer.attackEntityFrom(DamageSource.MAGIC, 2);
                    }
                    if (cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                        //Add jump boost and speed to pewter burners
                        curPlayer.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 30, 1, true, false));
                        curPlayer.addPotionEffect(new EffectInstance(Effects.SPEED, 30, 0, true, false));

                        if (cap.getDamageStored() > 0) {
                            if (world.rand.nextInt(200) == 0) {
                                cap.setDamageStored(cap.getDamageStored() - 1);
                            }
                        }

                    }
                    if (cap.getMetalBurning(AllomancyCapability.TIN)) {
                        // Add night vision to tin-burners
                        curPlayer.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, Short.MAX_VALUE, 5, true, false));
                        // Remove blindness for tin burners
                        if (curPlayer.isPotionActive(Effects.BLINDNESS)) {
                            curPlayer.removePotionEffect(Effects.BLINDNESS);
                        } else {
                            EffectInstance eff;
                            eff = curPlayer.getActivePotionEffect(Effects.NIGHT_VISION);

                        }

                    }
                    // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
                    if ((!cap.getMetalBurning(AllomancyCapability.TIN)) && (curPlayer.getActivePotionEffect(Effects.NIGHT_VISION) != null && curPlayer.getActivePotionEffect(Effects.NIGHT_VISION).getAmplifier() == 5)) {
                        curPlayer.removePotionEffect(Effects.NIGHT_VISION);
                    }
                }
            }
        }
    }
}
