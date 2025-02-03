package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.data.AllomancerData;
import com.legobmw99.allomancy.modules.powers.network.EnhanceTimePayload;
import com.legobmw99.allomancy.modules.powers.network.Network;
import com.legobmw99.allomancy.modules.powers.util.Emotional;
import com.legobmw99.allomancy.modules.powers.util.Enhancement;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import com.legobmw99.allomancy.modules.powers.util.Temporal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.io.File;
import java.util.Arrays;

public final class CommonEventHandler {


    private CommonEventHandler() {}

    /**
     * TEMPORARY: Used to port Forge worlds to Neoforged.
     * Loads the player's data file and sees if they have an old forge Capability stored.
     * TODO: Remove in future version once worlds have updated
     */
    @SubscribeEvent
    public static void onPlayerLoad(final PlayerEvent.LoadFromFile event) {
        Player player = event.getEntity();

        if (!player.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            CompoundTag compoundtag = null;
            try {
                File file1 = new File(event.getPlayerDirectory(), event.getPlayerUUID() + ".dat");
                if (file1.exists() && file1.isFile()) {
                    compoundtag = NbtIo.readCompressed(file1.toPath(), NbtAccounter.unlimitedHeap());
                }
            } catch (Exception exception) {
                Allomancy.LOGGER.warn("Failed to load old player data for {}", player.getName().getString());
            }

            if (compoundtag != null && compoundtag.contains("ForgeCaps")) {
                CompoundTag caps = compoundtag.getCompound("ForgeCaps");
                if (caps.contains("allomancy:allomancy_data")) {
                    Allomancy.LOGGER.info("Found old forge data for player {}, trying to load!",
                                          player.getName().getString());
                    var data = new AllomancerData();
                    try {
                        data.deserializeNBT(player.registryAccess(), caps.getCompound("allomancy:allomancy_data"));
                        player.setData(AllomancerAttachment.ALLOMANCY_DATA, data);
                        Allomancy.LOGGER.info("Loaded old forge data for player {}!", player.getName().getString());

                    } catch (Exception exception) {
                        Allomancy.LOGGER.error("Failed to deserialize old data!", exception);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
                var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
                //Handle random misting case
                if (PowersConfig.random_mistings.get() && data.isUninvested()) {
                    byte randomMisting;
                    if (PowersConfig.respect_player_UUID.get()) {
                        randomMisting = (byte) (Math.abs(player.getUUID().hashCode()) % 16);
                    } else {
                        randomMisting = (byte) (player.getRandom().nextInt(Metal.values().length));
                    }
                    data.addPower(Metal.getMetal(randomMisting));
                    ItemStack flakes = new ItemStack(MaterialsSetup.FLAKES.get(randomMisting).get());
                    // Give the player one flake of their metal
                    if (!player.getInventory().add(flakes)) {
                        ItemEntity entity = new ItemEntity(player.getCommandSenderWorld(), player.position().x(),
                                                           player.position().y(), player.position().z(), flakes);
                        player.getCommandSenderWorld().addFreshEntity(entity);
                    }
                }
            }

            //Sync cap to client
            Network.syncAllomancerData(player);
        }

    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            if (event.isWasDeath() && !player.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
                // if they died and keepInventory isn't set, they shouldn't keep their metals.
                var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
                for (Metal mt : Metal.values()) {
                    data.drainMetals(mt);
                    data.setBurning(mt, false);
                }
            }
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().getCommandSenderWorld().isClientSide() &&
            event.getEntity() instanceof ServerPlayer player) {
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
        if (!event.getTarget().level().isClientSide && event.getTarget() instanceof ServerPlayer player &&
            player.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onSetSpawn(final PlayerSetSpawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
            data.setSpawnLoc(event.getNewSpawn(), event.getSpawnLevel());
            Network.syncAllomancerData(player);
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(final LivingIncomingDamageEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getEntity() instanceof ServerPlayer source) {
            var data = source.getData(AllomancerAttachment.ALLOMANCY_DATA);

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
                ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                        .get()
                        .trigger(source, event.getEntity(), Metal.CHROMIUM, data.isEnhanced());
                if (event.getEntity() instanceof ServerPlayer player && !Emotional.hasTinFoilHat(player)) {
                    Enhancement.wipePlayer(player);
                }
            }
        }

        // Reduce incoming damage for pewter burners
        if (event.getEntity() instanceof ServerPlayer player) {
            var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
            if (data.isBurning(Metal.PEWTER)) {
                float newDamage = Math.max(0, event.getAmount() - 2);
                Allomancy.LOGGER.trace("Reducing damage to {} on {}", newDamage, player);
                event.setAmount(newDamage);
                // Note that they took damage, will come in to play if they stop burning
                data.setDamageStored(data.getDamageStored() + 2);
                Network.syncAllomancerData(player);
            }
        }
    }

    @SubscribeEvent
    public static void onInvulnerabilityCheck(final EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

            if (data.isBurning(Metal.PEWTER) && data.isEnhanced()) { // Duralumin invulnerability
                Allomancy.LOGGER.debug("Canceling damage for {}", player);
                event.setInvulnerable(true);
            }

            if (data.isBurning(Metal.STEEL)) { // Prevent fall damage on metal blocks when steelpushing
                if (event
                        .getSource()
                        .type()
                        .equals(player
                                        .level()
                                        .registryAccess()
                                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                                        .getOrThrow(DamageTypes.FALL)
                                        .value())) {
                    BlockPos on = player.getOnPos();
                    if (Physical.isBlockStateMetallic(player.level().getBlockState(on)) ||
                        Physical.isBlockStateMetallic(player.level().getBlockState(on.above()))) {
                        event.setInvulnerable(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel l) {
            var list = l.players();
            for (int i = list.size() - 1; i >= 0; i--) {
                var curPlayer = list.get(i);
                playerPowerTick(curPlayer, l);
            }
        }


    }

    private static void playerPowerTick(ServerPlayer curPlayer, ServerLevel level) {
        var data = curPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA);
        // Run the necessary updates on the player's metals
        boolean syncRequired = data.tickBurning();


        ItemStack helmet = curPlayer.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() == ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            GlobalPos seeking = data.getSpecialSeekingLoc();
            if (seeking == null) {
                BlockPos blockpos =
                        level.findNearestMapStructure(ExtrasSetup.SEEKABLE, curPlayer.blockPosition(), 100, false);
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
            syncRequired = data.getSpecialSeekingLoc() != null;
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
                Network.sync(new EnhanceTimePayload(2, curPlayer.getId()), curPlayer);
            } else if (!data.isBurning(Metal.DURALUMIN) && data.isEnhanced()) {
                data.decrementEnhanced();
                if (!data.isEnhanced()) { //Enhancement ran out this tick
                    Network.sync(new EnhanceTimePayload(false, curPlayer.getId()), curPlayer);
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
            } else if (data.isEnhanced() && data.isBurning(Metal.GOLD) &&
                       data.getStored(Metal.GOLD) >= 9) { // These should be mutually exclusive
                Enhancement.teleportToLastDeath(curPlayer, level, data);
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
                        syncRequired = true;
                    }
                }

            }
            // Damage the player if they have stored damage and pewter cuts out
            if (!data.isBurning(Metal.PEWTER) && (data.getDamageStored() > 0)) {
                data.setDamageStored(data.getDamageStored() - 1);
                curPlayer.hurtServer(level, level.damageSources().magic(), 2);
                syncRequired = true;
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
    }
}

