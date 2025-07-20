package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.modules.powers.data.AllomancerDataProvider;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.modules.powers.util.Emotional;
import com.legobmw99.allomancy.modules.powers.util.Enhancement;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import com.legobmw99.allomancy.modules.powers.util.Temporal;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;


public final class CommonEventHandler {


    private CommonEventHandler() {}


    @SubscribeEvent
    public static void onAttachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player p) {
            AllomancerDataProvider provider = new AllomancerDataProvider();
            event.addCapability(AllomancerCapability.IDENTIFIER, provider);
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

                if (PowersConfig.random_mistings.get() && data.isUninvested()) {
                    int randomMisting;

                    if (PowersConfig.respect_player_UUID.get()) {
                        randomMisting = Math.abs(player.getUUID().hashCode()) % Metal.values().length;
                    } else {
                        randomMisting = player.getRandom().nextInt(Metal.values().length);
                    }
                    data.addPower(Metal.getMetal(randomMisting));
                    ItemStack flakes = new ItemStack(WorldSetup.FLAKES.get(randomMisting).get());
                    // Give the player one flake of their metal
                    if (!player.getInventory().add(flakes)) {
                        ItemEntity entity =
                                new ItemEntity(player.level(), player.position().x(), player.position().y(),
                                               player.position().z(), flakes);
                        player.level().addFreshEntity(entity);
                    }
                }
            });
            Network.syncAllomancerData(player);
        }
    }


    @SubscribeEvent
    public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            if (!event.isEndConquered() /* poorly named, is really equivalent to keepInventory... */) {

                player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                    data.drainMetals(Metal.values());
                });
            }
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getEntity().getCommandSenderWorld().isClientSide() &&
            event.getEntity() instanceof ServerPlayer player) {
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(final PlayerEvent.StartTracking event) {
        if (!event.getTarget().level().isClientSide && event.getTarget() instanceof ServerPlayer player) {
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onSetSpawn(final PlayerSetSpawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                data.setSpawnLoc(event.getNewSpawn(), event.getSpawnLevel());
            });
            Network.syncAllomancerData(player);
        }
    }



    @SubscribeEvent
    public static void onEntityHurt(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getEntity() instanceof ServerPlayer source) {
            source.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

                if (data.isBurning(Metal.PEWTER)) {
                    if (data.isEnhanced()) {
                        if (source.getMainHandItem().getItem() instanceof KolossBladeItem) {
                            event.setAmount(550); // Duralumin OHK with Koloss blade
                            Enhancement.wipePlayer(source);
                        } else {
                            event.setAmount(event.getAmount() * 3);
                        }
                    } else {
                        event.setAmount(event.getAmount() + 2);
                    }
                }

                if (data.isBurning(Metal.CHROMIUM)) {

                    if (event.getEntity() instanceof ServerPlayer player) {

                        if (!Emotional.hasTinFoilHat(player)) {
                            Enhancement.wipePlayer(player);
                        }
                    }
                }
            });
        }

        // Reduce incoming damage for pewter burners
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                if (data.isBurning(Metal.PEWTER)) {
                    Allomancy.LOGGER.debug("Reducing Damage");
                    event.setAmount(event.getAmount() - 2);
                    // Note that they took damage, will come in to play if they stop burning
                    data.setDamageStored(data.getDamageStored() + 2);
                    Network.syncAllomancerData(player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerAttacked(final LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

                if (data.isBurning(Metal.PEWTER) && data.isEnhanced()) { // Duralumin invulnerability
                    Allomancy.LOGGER.debug("Canceling Damage");
                    event.setCanceled(true);
                }

                if (data.isBurning(Metal.STEEL)) {
                    if (event
                            .getSource()
                            .type()
                            .equals(player
                                            .level()
                                            .registryAccess()
                                            .registryOrThrow(Registries.DAMAGE_TYPE)
                                            .getHolderOrThrow(DamageTypes.FALL)
                                            .value())) {
                        BlockPos on = player.getOnPos();
                        if (Physical.isBlockStateMetallic(player.level().getBlockState(on)) ||
                            Physical.isBlockStateMetallic(player.level().getBlockState(on.above()))) {
                            event.setCanceled(true);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Level level = event.level;
        if (level instanceof ServerLevel l) {
            var list = l.players();
            for (int i = list.size() - 1; i >= 0; i--) {
                var curPlayer = list.get(i);
                playerPowerTick(curPlayer, l);
            }
        }


    }

    private static void playerPowerTick(ServerPlayer curPlayer, ServerLevel level) {
        curPlayer.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
            // Run the necessary updates on the player's metals

            boolean syncRequired = data.tickBurning();


            ItemStack helmet = curPlayer.getItemBySlot(EquipmentSlot.HEAD);
            if (helmet.getItem() == ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
                GlobalPos seeking = data.getSpecialSeekingLoc();
                if (seeking == null) {
                    BlockPos blockpos =
                            level.findNearestMapStructure(AllomancyTags.SEEKABLE, curPlayer.blockPosition(), 100,
                                                          false);
                    if (blockpos != null) {
                        data.setSpecialSeekingLoc(blockpos, curPlayer.level().dimension());
                        syncRequired = true;
                    }
                } else if (level.isLoaded(seeking.pos())) {
                    BlockPos raised = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, seeking.pos()).below(18);
                    if (raised != seeking.pos()) {
                        data.setSpecialSeekingLoc(raised, seeking.dimension());
                        syncRequired = true;
                    }
                }
            } else {
                syncRequired = syncRequired || data.getSpecialSeekingLoc() != null;
                data.setSpecialSeekingLoc(null, null);
            }

            if (!data.isUninvested()) {

                /*********************************************
                 * ALUMINUM AND DURALUMIN                    *
                 *********************************************/
                if (data.isBurning(Metal.ALUMINUM)) {
                    Enhancement.wipePlayer(curPlayer);
                    syncRequired = true;
                }
                if (data.isBurning(Metal.DURALUMIN) && !data.isEnhanced()) {
                    data.setEnhanced(2);
                    Network.sync(new UpdateEnhancedPacket(2, curPlayer.getId()), curPlayer);
                } else if (!data.isBurning(Metal.DURALUMIN) && data.isEnhanced()) {
                    data.decrementEnhanced();
                    if (!data.isEnhanced()) { //Enhancement ran out this tick
                        data.drainMetals(Arrays.stream(Metal.values()).filter(data::isBurning).toArray(Metal[]::new));
                        syncRequired = true;
                    }
                }


                /*********************************************
                 * CHROMIUM (enhanced)                       *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.CHROMIUM)) {
                    Enhancement.wipeNearby(curPlayer, level);
                }

                /*********************************************
                 * GOLD AND ELECTRUM (enhanced)              *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.ELECTRUM) && data.getStored(Metal.ELECTRUM) >= 9) {
                    Enhancement.teleportToSpawn(curPlayer, level, data);
                    syncRequired = true;
                } else if (data.isEnhanced() && data.isBurning(Metal.GOLD) &&
                           data.getStored(Metal.GOLD) >= 9) { // These should be mutually exclusive
                    Enhancement.teleportToLastDeath(curPlayer, level, data);
                    syncRequired = true;
                }

                /*********************************************
                 * BENDALLOY AND CADMIUM                     *
                 *********************************************/
                if (data.isBurning(Metal.BENDALLOY) && !data.isBurning(Metal.CADMIUM)) {
                    Temporal.speedUpNearby(curPlayer, level, data);
                }
                if (data.isBurning(Metal.CADMIUM) && !data.isBurning(Metal.BENDALLOY)) {
                    Temporal.slowDownNearby(curPlayer, level, data);
                }


                /*********************************************
                 * TIN AND PEWTER                            *
                 *********************************************/
                if (data.isBurning(Metal.TIN)) {
                    // Add night vision to tin-burners
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 5, true, false));
                    if (data.isEnhanced()) { // Tin and Duralumin is too much to handle
                        if (level.random.nextInt(50) == 0) {
                            curPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, true, false));
                        }
                    }
                    // Remove blindness from normal tin burners
                    if (curPlayer.hasEffect(MobEffects.BLINDNESS)) {
                        curPlayer.removeEffect(MobEffects.BLINDNESS);
                    }
                }
                // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with
                // flashing, only if the amplifier is 5
                if (!data.isBurning(Metal.TIN) && curPlayer.getEffect(MobEffects.NIGHT_VISION) != null &&
                    curPlayer.getEffect(MobEffects.NIGHT_VISION).getAmplifier() == 5) {
                    curPlayer.removeEffect(MobEffects.NIGHT_VISION);
                }
                if (data.isBurning(Metal.PEWTER)) {
                    // Add jump boost and speed to pewter burners
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.JUMP, 11, 1, true, false));
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 11, 0, true, false));
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 11, 1, true, false));

                    if (data.getDamageStored() > 0) {
                        if (level.random.nextInt(200) == 0) {
                            data.setDamageStored(data.getDamageStored() - 1);
                        }
                    }

                }
                // Damage the player if they have stored damage and pewter cuts out
                if (!data.isBurning(Metal.PEWTER) && (data.getDamageStored() > 0)) {
                    data.setDamageStored(data.getDamageStored() - 1);
                    curPlayer.hurt(level.damageSources().magic(), 2);
                }


                /*********************************************
                 * COPPER (enhanced)                      *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.COPPER)) {
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 50, true, false));
                }
            }
            if (syncRequired) {
                Network.syncAllomancerData(curPlayer);
            }
        });
    }
}
