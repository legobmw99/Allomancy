package com.legobmw99.allomancy.modules.powers.handlers;

import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.network.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.Metal;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class CommonEventHandler {

    @SubscribeEvent
    public void onAttachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(AllomancyCapability.IDENTIFIER, new AllomancyCapability());
        }
    }


    @SubscribeEvent
    public void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().world.isRemote) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
                AllomancyCapability cap = AllomancyCapability.forPlayer(player);

                //Handle random misting case
                if (PowersConfig.random_mistings.get() && cap.isUninvested()) {
                    byte randomMisting = (byte) (Math.random() * Metal.values().length);
                    cap.addPower(Metal.getMetal(randomMisting));
                    ItemStack flakes = new ItemStack(MaterialsSetup.FLAKES.get(randomMisting).get());
                    // Give the player one flake of their metal
                    if (!player.inventory.addItemStackToInventory(flakes)) {
                        ItemEntity entity = new ItemEntity(player.getEntityWorld(), player.getPositionVec().getX(), player.getPositionVec().getY(), player.getPositionVec().getZ(), flakes);
                        player.getEntityWorld().addEntity(entity);
                    }
                }

                //Sync cap to client
                Network.sync(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.getPlayer().world.isRemote()) {

            PlayerEntity player = event.getPlayer();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player); // the clone's cap

            PlayerEntity old = event.getOriginal();

            old.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(oldCap -> {
                if (!oldCap.isUninvested()) { // make sure the new player has the same power status
                    for (Metal mt : Metal.values()) {
                        if (oldCap.hasPower(mt)) {
                            cap.addPower(mt);
                        }
                    }
                }

                if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
                    for (Metal mt : Metal.values()) {
                        cap.setAmount(mt, oldCap.getAmount(mt));
                    }
                }
            });

            Network.sync(player);
        }
    }

    @SubscribeEvent
    public void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote()) {
            Network.sync(event.getPlayer());
        }
    }


    @SubscribeEvent
    public void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getPlayer().getEntityWorld().isRemote()) {
            Network.sync(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onStartTracking(final net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!event.getTarget().world.isRemote) {
            if (event.getTarget() instanceof ServerPlayerEntity) {
                ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getTarget();
                Network.sendTo(new AllomancyCapabilityPacket(AllomancyCapability.forPlayer(playerEntity), playerEntity.getEntityId()), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
            ServerPlayerEntity source = (ServerPlayerEntity) event.getSource().getTrueSource();
            AllomancyCapability cap = AllomancyCapability.forPlayer(source);

            if (cap.isBurning(Metal.PEWTER)) {
                event.setAmount(event.getAmount() + 2);
            }
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            AllomancyCapability cap = AllomancyCapability.forPlayer(event.getEntityLiving());
            if (cap.isBurning(Metal.PEWTER)) {
                event.setAmount(event.getAmount() - 2);
                // Note that they took damage, will come in to play if they stop burning
                cap.setDamageStored(cap.getDamageStored() + 1);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            World world = (World) event.world;
            List<? extends PlayerEntity> list = world.getPlayers();
            for (PlayerEntity curPlayer : list) {
                AllomancyCapability cap = AllomancyCapability.forPlayer(curPlayer);

                if (!cap.isUninvested()) {
                    // Run the necessary updates on the player's metals
                    if (curPlayer instanceof ServerPlayerEntity) {
                        AllomancyCapability.updateMetalBurnTime(cap, (ServerPlayerEntity) curPlayer);
                    }
                    // Damage the player if they have stored damage and pewter cuts out
                    if (!cap.isBurning(Metal.PEWTER) && (cap.getDamageStored() > 0)) {
                        cap.setDamageStored(cap.getDamageStored() - 1);
                        curPlayer.attackEntityFrom(DamageSource.MAGIC, 2);
                    }
                    if (cap.isBurning(Metal.PEWTER)) {
                        //Add jump boost and speed to pewter burners
                        curPlayer.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 30, 1, true, false));
                        curPlayer.addPotionEffect(new EffectInstance(Effects.SPEED, 30, 0, true, false));
                        curPlayer.addPotionEffect(new EffectInstance(Effects.HASTE, 30, 1, true, false));

                        if (cap.getDamageStored() > 0) {
                            if (world.rand.nextInt(200) == 0) {
                                cap.setDamageStored(cap.getDamageStored() - 1);
                            }
                        }

                    }
                    if (cap.isBurning(Metal.TIN)) {
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
                    if ((!cap.isBurning(Metal.TIN)) &&
                            (curPlayer.getActivePotionEffect(Effects.NIGHT_VISION) != null &&
                                    curPlayer.getActivePotionEffect(Effects.NIGHT_VISION).getAmplifier() == 5)) {
                        curPlayer.removePotionEffect(Effects.NIGHT_VISION);
                    }
                }
            }
        }
    }
}
