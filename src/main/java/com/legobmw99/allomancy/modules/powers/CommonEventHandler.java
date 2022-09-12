package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.modules.powers.data.AllomancerDataProvider;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;

public class CommonEventHandler {

    @SubscribeEvent
    public static void onAttachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            AllomancerDataProvider provider = new AllomancerDataProvider();
            event.addCapability(AllomancerCapability.IDENTIFIER, provider);
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer().level.isClientSide()) {
            return;
        }

        if (event.getPlayer() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                //Handle random misting case
                if (PowersConfig.random_mistings.get() && data.isUninvested()) {
                    byte randomMisting;
                    if (PowersConfig.respect_player_UUID.get()) {
                        randomMisting = (byte) (Math.abs(player.getUUID().hashCode()) % 16);
                    } else {
                        randomMisting = (byte) (event.getPlayer().getRandom().nextInt(Metal.values().length));
                    }
                    data.addPower(Metal.getMetal(randomMisting));
                    ItemStack flakes = new ItemStack(MaterialsSetup.FLAKES.get(randomMisting).get());
                    // Give the player one flake of their metal
                    if (!player.getInventory().add(flakes)) {
                        ItemEntity entity = new ItemEntity(player.getCommandSenderWorld(), player.position().x(), player.position().y(), player.position().z(), flakes);
                        player.getCommandSenderWorld().addFreshEntity(entity);
                    }
                }
            });

            //Sync cap to client
            Network.sync(player);
        }

    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.getPlayer().level.isClientSide() && event.getPlayer() instanceof ServerPlayer player) {

            event.getOriginal().reviveCaps();
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                event.getOriginal().getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(oldData -> {
                    data.setDeathLoc(oldData.getDeathLoc(), oldData.getDeathDim());
                    if (!oldData.isUninvested()) { // make sure the new player has the same power status
                        for (Metal mt : Metal.values()) {
                            if (oldData.hasPower(mt)) {
                                data.addPower(mt);
                            }
                        }
                    }

                    if (player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ||
                        !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
                        for (Metal mt : Metal.values()) {
                            data.setAmount(mt, oldData.getAmount(mt));
                        }
                    }
                });
            });
            event.getOriginal().getCapability(AllomancerCapability.PLAYER_CAP).invalidate();
            event.getOriginal().invalidateCaps();

            Network.sync(player);

        }
    }

    @SubscribeEvent
    public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide() && event.getPlayer() instanceof ServerPlayer player) {
            Network.sync(player);
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide() && event.getPlayer() instanceof ServerPlayer player) {
            Network.sync(player);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(final net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!event.getTarget().level.isClientSide && event.getTarget() instanceof ServerPlayer player) {
            Network.sync(player);
        }
    }

    @SubscribeEvent
    public static void onSetSpawn(final PlayerSetSpawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                data.setSpawnLoc(event.getNewSpawn(), event.getSpawnWorld());
                Network.sync(data, player);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                data.setDeathLoc(new BlockPos(player.position()), player.level.dimension());
                Network.sync(data, player);
            });
        }
    }

    @SubscribeEvent
    public static void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getEntity() instanceof ServerPlayer source) {
            source.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {

                if (data.isBurning(Metal.PEWTER)) {
                    if (data.isEnhanced()) {
                        if (source.getMainHandItem().getItem() instanceof KolossBladeItem) {
                            event.setAmount(550); // Duralumin OHK with Koloss blade
                            PowerUtils.wipePlayer(source);
                        } else {
                            event.setAmount(event.getAmount() * 3);
                        }
                    } else {
                        event.setAmount(event.getAmount() + 2);
                    }
                }

                if (data.isBurning(Metal.CHROMIUM)) {
                    if (event.getEntityLiving() instanceof Player player) {
                        PowerUtils.wipePlayer(player);
                    }
                }
            });
        }

        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof ServerPlayer player) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
                if (data.isBurning(Metal.PEWTER)) {
                    if (data.isEnhanced()) { // Duralumin invulnerability
                        Allomancy.LOGGER.debug("Canceling Damage");
                        event.setAmount(0);
                        event.setCanceled(true);
                    } else {
                        Allomancy.LOGGER.debug("Reducing Damage");

                        event.setAmount(event.getAmount() - 2);
                        // Note that they took damage, will come in to play if they stop burning
                        data.setDamageStored(data.getDamageStored() + 1);
                        Network.sync(data, player);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Level level = event.world;
        var list = level.players();
        for (int enti = list.size() - 1; enti >= 0; enti--) {
            Player curPlayer = list.get(enti);
            playerPowerTick(curPlayer, level);
        }

    }

    private static void playerPowerTick(Player curPlayer, Level level) {
        curPlayer.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
            if (!data.isUninvested()) {
                /*********************************************
                 * ALUMINUM AND DURALUMIN                    *
                 *********************************************/
                if (data.isBurning(Metal.ALUMINUM)) {
                    PowerUtils.wipePlayer(curPlayer);
                }
                if (data.isBurning(Metal.DURALUMIN) && !data.isEnhanced()) {
                    data.setEnhanced(2);
                    if (curPlayer instanceof ServerPlayer sp) {
                        Network.sync(new UpdateEnhancedPacket(2, sp.getId()), sp);

                    }
                } else if (!data.isBurning(Metal.DURALUMIN) && data.isEnhanced()) {
                    data.decEnhanced();
                    if (!data.isEnhanced()) { //Enhancement ran out this tick
                        if (curPlayer instanceof ServerPlayer sp) {
                            Network.sync(new UpdateEnhancedPacket(false, sp.getId()), sp);
                        }
                        data.drainMetals(Arrays.stream(Metal.values()).filter(data::isBurning).toArray(Metal[]::new));
                    }
                }


                // Run the necessary updates on the player's metals
                // Ran AFTER duralumin and aluminum to make sure they function correctly
                if (curPlayer instanceof ServerPlayer player) {
                    data.tickBurning(player);
                }


                /*********************************************
                 * CHROMIUM (enhanced)                       *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.CHROMIUM)) {
                    if (level instanceof ServerLevel) {
                        int max = 20;
                        BlockPos negative = new BlockPos(curPlayer.position()).offset(-max, -max, -max);
                        BlockPos positive = new BlockPos(curPlayer.position()).offset(max, max, max);
                        level.getEntitiesOfClass(Player.class, new AABB(negative, positive)).forEach(otherPlayer -> {
                            otherPlayer.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(otherData -> otherData.drainMetals(Metal.values()));
                        });
                    }
                }


                /*********************************************
                 * GOLD AND ELECTRUM (enhanced)              *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.ELECTRUM) && data.getAmount(Metal.ELECTRUM) >= 9) {
                    ResourceKey<Level> spawnDim = data.getSpawnDim();
                    BlockPos spawnLoc;

                    if (spawnDim != null) {
                        spawnLoc = data.getSpawnLoc();
                    } else {
                        spawnDim = Level.OVERWORLD; // no spawn --> use world spawn
                        spawnLoc = new BlockPos(level.getLevelData().getXSpawn(), level.getLevelData().getYSpawn(), level.getLevelData().getZSpawn());

                    }

                    PowerUtils.teleport(curPlayer, level, spawnDim, spawnLoc);
                    if (data.isBurning(Metal.DURALUMIN)) {
                        data.drainMetals(Metal.DURALUMIN);
                    }
                    data.drainMetals(Metal.ELECTRUM);


                } else if (data.isEnhanced() && data.isBurning(Metal.GOLD) && data.getAmount(Metal.GOLD) >= 9) { // These should be mutually exclusive
                    ResourceKey<Level> deathDim = data.getDeathDim();
                    if (deathDim != null) {
                        PowerUtils.teleport(curPlayer, level, deathDim, data.getDeathLoc());
                        if (data.isBurning(Metal.DURALUMIN)) {
                            data.drainMetals(Metal.DURALUMIN);
                        }
                        data.drainMetals(Metal.GOLD);
                    }
                }


                /*********************************************
                 * BENDALLOY AND CADMIUM                     *
                 *********************************************/
                if (data.isBurning(Metal.BENDALLOY) && !data.isBurning(Metal.CADMIUM)) {
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, 3, true, false));
                    curPlayer.aiStep();
                    curPlayer.aiStep();

                    if (level instanceof ServerLevel serverLevel) {
                        int max = data.isEnhanced() ? 10 : 5;
                        BlockPos negative = new BlockPos(curPlayer.position()).offset(-max, -max, -max);
                        BlockPos positive = new BlockPos(curPlayer.position()).offset(max, max, max);
                        serverLevel.getEntitiesOfClass(LivingEntity.class, new AABB(negative, positive)).forEach(entity -> {
                            entity.aiStep();
                            entity.aiStep();
                        });
                        BlockPos.betweenClosedStream(negative, positive).forEach(bp -> {
                            BlockState block = level.getBlockState(bp);
                            BlockEntity te = level.getBlockEntity(bp);
                            if (te == null) {
                                if (block.isRandomlyTicking()) {
                                    // TODO investigate how many ticks is best
                                    for (int i = 0; i < max * 4 / 10; i++) {
                                        block.randomTick(serverLevel, bp, serverLevel.random);
                                    }
                                }
                            } else {
                                Block underlying_block = block.getBlock();
                                if (underlying_block instanceof EntityBlock eb) {
                                    BlockEntityTicker ticker = eb.getTicker(level, block, te.getType());
                                    if (ticker != null) {
                                        for (int i = 0; i < max * 4 / 10; i++) {
                                            ticker.tick(level, bp, block, te);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                if (data.isBurning(Metal.CADMIUM) && !data.isBurning(Metal.BENDALLOY)) {
                    int max = data.isEnhanced() ? 20 : 10;
                    BlockPos negative = new BlockPos(curPlayer.position()).offset(-max, -max, -max);
                    BlockPos positive = new BlockPos(curPlayer.position()).offset(max, max, max);
                    int slowness_amplifier = data.isEnhanced() ? 255 : 2; // Duralumin freezes entities
                    level.getEntitiesOfClass(LivingEntity.class, new AABB(negative, positive)).forEach(entity -> {
                        entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, true, false));
                        if (entity != curPlayer) {
                            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, slowness_amplifier, true, false));
                        }
                    });
                }


                /*********************************************
                 * TIN AND PEWTER                            *
                 *********************************************/
                if (data.isBurning(Metal.TIN)) {
                    // Add night vision to tin-burners
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Short.MAX_VALUE, 5, true, false));
                    if (data.isEnhanced()) { // Tin and Duralumin is too much to handle
                        curPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 150, true, false));
                        if (level.random.nextInt(50) == 0) {
                            curPlayer.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, true, false));
                        }
                    } else { // Remove blindness from normal tin burners
                        if (curPlayer.hasEffect(MobEffects.BLINDNESS)) {
                            curPlayer.removeEffect(MobEffects.BLINDNESS);
                        }
                    }
                }
                // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
                if (!data.isBurning(Metal.TIN) && curPlayer.getEffect(MobEffects.NIGHT_VISION) != null && curPlayer.getEffect(MobEffects.NIGHT_VISION).getAmplifier() == 5) {
                    curPlayer.removeEffect(MobEffects.NIGHT_VISION);
                }
                if (data.isBurning(Metal.PEWTER)) {
                    //Add jump boost and speed to pewter burners
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, 1, true, false));
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 0, true, false));
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 10, 1, true, false));

                    if (data.getDamageStored() > 0) {
                        if (level.random.nextInt(200) == 0) {
                            data.setDamageStored(data.getDamageStored() - 1);
                        }
                    }

                }
                // Damage the player if they have stored damage and pewter cuts out
                if (!data.isBurning(Metal.PEWTER) && (data.getDamageStored() > 0)) {
                    data.setDamageStored(data.getDamageStored() - 1);
                    curPlayer.hurt(DamageSource.MAGIC, 2);
                }


                /*********************************************
                 * COPPER (enhanced)                      *
                 *********************************************/
                if (data.isEnhanced() && data.isBurning(Metal.COPPER)) {
                    curPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20, 50, true, false));
                }

                // TODO this would be atium? need a packet and a renderer
                //                        if (false) {
                //                            BlockPos negative = curPlayer.blockPosition().offset(-30, -30, -30);
                //                            BlockPos positive = curPlayer.blockPosition().offset(30, 30, 30);
                //                            var nearby_players = curPlayer.level.getEntitiesOfClass(Mob.class, new AABB(negative, positive), Objects::nonNull);
                //
                //                            for (Mob mob : nearby_players) {
                //                                Path path = mob.getNavigation().getPath();
                //                                if (path != null) {
                //                                    int count = path.getNodeCount();
                //                                    for (int i = 0; i < count; i++) {
                //                                        Node point = path.getNode(i);
                //                                        event.world.addParticle(ParticleTypes.EXPLOSION, point.x + 0.5, point.y + 0.5, point.z + 0.5, 0, 0, 0);
                //                                    }
                //                                }
                //                            }
                //                        }

            }
        });
    }
}

