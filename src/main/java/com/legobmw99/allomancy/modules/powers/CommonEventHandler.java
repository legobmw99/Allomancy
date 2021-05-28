package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.data.AllomancyDataProvider;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.Metal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
        if (event.getObject() instanceof PlayerEntity) {
            AllomancyDataProvider provider = new AllomancyDataProvider();
            event.addCapability(AllomancyCapability.IDENTIFIER, provider);
            event.addListener(provider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            if (event.getPlayer() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

                player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                    //Handle random misting case
                    if (PowersConfig.random_mistings.get() && data.isUninvested()) {
                        byte randomMisting = (byte) (Math.random() * Metal.values().length);
                        data.addPower(Metal.getMetal(randomMisting));
                        ItemStack flakes = new ItemStack(MaterialsSetup.FLAKES.get(randomMisting).get());
                        // Give the player one flake of their metal
                        if (!player.inventory.add(flakes)) {
                            ItemEntity entity = new ItemEntity(player.getCommandSenderWorld(), player.position().x(), player.position().y(), player.position().z(), flakes);
                            player.getCommandSenderWorld().addFreshEntity(entity);
                        }
                    }
                });

                //Sync cap to client
                Network.sync(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (!event.getPlayer().level.isClientSide()) {

            PlayerEntity player = event.getPlayer();
            player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {

                event.getOriginal().getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(oldData -> {
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

            Network.sync(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide()) {
            Network.sync(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide()) {
            Network.sync(event.getPlayer());
        }
    }

    @SubscribeEvent
    public static void onStartTracking(final net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        if (!event.getTarget().level.isClientSide) {
            if (event.getTarget() instanceof ServerPlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) event.getTarget();
                Network.sync(player);
            }
        }
    }

    @SubscribeEvent
    public static void onSetSpawn(final PlayerSetSpawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                data.setSpawnLoc(event.getNewSpawn(), event.getSpawnWorld());
                Network.sync(data, player);
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                data.setDeathLoc(new BlockPos(player.position()), player.level.dimension());
                Network.sync(data, player);
            });
        }
    }

    @SubscribeEvent
    public static void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity source = (ServerPlayerEntity) event.getSource().getEntity();
            source.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {

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
                    if (event.getEntityLiving() instanceof PlayerEntity) {
                        PowerUtils.wipePlayer((PlayerEntity) event.getEntityLiving());
                    }
                }
            });
        }

        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            event.getEntityLiving().getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
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
                        Network.sync(data, (ServerPlayerEntity) event.getEntityLiving());
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            event.world.players().forEach(curPlayer -> {
                curPlayer.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
                    if (!data.isUninvested()) {

                        /*********************************************
                         * ALUMINUM AND DURALUMIN                    *
                         *********************************************/
                        if (data.isBurning(Metal.ALUMINUM)) {
                            PowerUtils.wipePlayer(curPlayer);
                        }
                        if (data.isBurning(Metal.DURALUMIN) && !data.isEnhanced()) {
                            data.setEnhanced(2);
                            Network.sync(new UpdateEnhancedPacket(2, curPlayer.getId()), curPlayer);
                        } else if (!data.isBurning(Metal.DURALUMIN) && data.isEnhanced()) {
                            data.decEnhanced();
                            if (!data.isEnhanced()) { //Enhancement ran out this tick
                                Network.sync(new UpdateEnhancedPacket(false, curPlayer.getId()), curPlayer);
                                data.drainMetals(Arrays.stream(Metal.values()).filter(data::isBurning).toArray(Metal[]::new));
                            }
                        }


                        // Run the necessary updates on the player's metals
                        // Ran AFTER duralumin and aluminum to make sure they function correctly
                        if (curPlayer instanceof ServerPlayerEntity) {
                            data.updateMetalBurnTime((ServerPlayerEntity) curPlayer);
                        }


                        /*********************************************
                         * CHROMIUM (enhanced)                       *
                         *********************************************/
                        if (data.isEnhanced() && data.isBurning(Metal.CHROMIUM)) {
                            if (event.world instanceof ServerWorld) {
                                int max = 20;
                                BlockPos negative = new BlockPos(curPlayer.position()).offset(-max, -max, -max);
                                BlockPos positive = new BlockPos(curPlayer.position()).offset(max, max, max);
                                event.world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(negative, positive)).forEach(otherPlayer -> {
                                    otherPlayer.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(otherData -> otherData.drainMetals(Metal.values()));
                                });
                            }
                        }


                        /*********************************************
                         * GOLD AND ELECTRUM (enhanced)              *
                         *********************************************/
                        if (data.isEnhanced() && data.isBurning(Metal.ELECTRUM) && data.getAmount(Metal.ELECTRUM) >= 9) {
                            RegistryKey<World> spawnDim = data.getSpawnDim();
                            BlockPos spawnLoc;

                            if (spawnDim != null) {
                                spawnLoc = data.getSpawnLoc();
                            } else {
                                spawnDim = World.OVERWORLD; // no spawn --> use world spawn
                                spawnLoc = new BlockPos(curPlayer.level.getLevelData().getXSpawn(), curPlayer.level.getLevelData().getYSpawn(),
                                                        curPlayer.level.getLevelData().getZSpawn());

                            }

                            PowerUtils.teleport(curPlayer, event.world, spawnDim, spawnLoc);
                            if (data.isBurning(Metal.DURALUMIN)) {
                                data.drainMetals(Metal.DURALUMIN);
                            }
                            data.drainMetals(Metal.ELECTRUM);


                        } else if (data.isEnhanced() && data.isBurning(Metal.GOLD) && data.getAmount(Metal.GOLD) >= 9) { // These should be mutually exclusive
                            RegistryKey<World> deathDim = data.getDeathDim();
                            if (deathDim != null) {
                                PowerUtils.teleport(curPlayer, event.world, deathDim, data.getDeathLoc());
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
                            curPlayer.addEffect(new EffectInstance(Effects.DIG_SPEED, 10, 3, true, false));
                            curPlayer.aiStep();
                            curPlayer.aiStep();

                            if (event.world instanceof ServerWorld) {
                                int max = data.isEnhanced() ? 10 : 5;
                                BlockPos negative = new BlockPos(curPlayer.position()).offset(-max, -max, -max);
                                BlockPos positive = new BlockPos(curPlayer.position()).offset(max, max, max);
                                event.world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(negative, positive)).forEach(entity -> {
                                    entity.aiStep();
                                    entity.aiStep();
                                });
                                BlockPos.betweenClosedStream(negative, positive).forEach(bp -> {
                                    BlockState block = event.world.getBlockState(bp);
                                    TileEntity te = event.world.getBlockEntity(bp);
                                    for (int i = 0; i < max * 4 / (te == null ? 10 : 1); i++) {
                                        if (te instanceof ITickableTileEntity) {
                                            ((ITickableTileEntity) te).tick();
                                        } else if (block.isRandomlyTicking()) {
                                            block.randomTick((ServerWorld) event.world, bp, event.world.random);
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
                            event.world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(negative, positive)).forEach(entity -> {
                                entity.addEffect(new EffectInstance(Effects.SLOW_FALLING, 10, 0, true, false));
                                if (entity != curPlayer) {
                                    entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 10, slowness_amplifier, true, false));
                                }
                            });
                        }


                        /*********************************************
                         * TIN AND PEWTER                            *
                         *********************************************/
                        if (data.isBurning(Metal.TIN)) {
                            // Add night vision to tin-burners
                            curPlayer.addEffect(new EffectInstance(Effects.NIGHT_VISION, Short.MAX_VALUE, 5, true, false));
                            if (data.isEnhanced()) { // Tin and Duralumin is too much to handle
                                curPlayer.addEffect(new EffectInstance(Effects.BLINDNESS, 100, 150, true, false));
                                if (event.world.random.nextInt(50) == 0) {
                                    curPlayer.addEffect(new EffectInstance(Effects.CONFUSION, 100, 0, true, false));
                                }
                            } else { // Remove blindness from normal tin burners
                                if (curPlayer.hasEffect(Effects.BLINDNESS)) {
                                    curPlayer.removeEffect(Effects.BLINDNESS);
                                }
                            }
                        }
                        // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
                        if (!data.isBurning(Metal.TIN) && curPlayer.getEffect(Effects.NIGHT_VISION) != null && curPlayer.getEffect(Effects.NIGHT_VISION).getAmplifier() == 5) {

                            curPlayer.removeEffect(Effects.NIGHT_VISION);
                        }
                        if (data.isBurning(Metal.PEWTER)) {
                            //Add jump boost and speed to pewter burners
                            curPlayer.addEffect(new EffectInstance(Effects.JUMP, 10, 1, true, false));
                            curPlayer.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 10, 0, true, false));
                            curPlayer.addEffect(new EffectInstance(Effects.DIG_SPEED, 10, 1, true, false));

                            if (data.getDamageStored() > 0) {
                                if (event.world.random.nextInt(200) == 0) {
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
                            curPlayer.addEffect(new EffectInstance(Effects.INVISIBILITY, 20, 50, true, false));
                        }

                    }
                });
            });
        }
    }
}

