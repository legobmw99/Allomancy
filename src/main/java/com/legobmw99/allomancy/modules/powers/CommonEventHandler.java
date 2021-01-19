package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.network.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.modules.powers.network.UpdateEnhancedPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.PowerUtils;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.setup.Metal;
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
import java.util.List;
import java.util.Random;

public class CommonEventHandler {

    private final Random random = new Random();

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
                        ItemEntity entity = new ItemEntity(player.getEntityWorld(), player.getPositionVec().getX(), player.getPositionVec().getY(), player.getPositionVec().getZ(),
                                                           flakes);
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
                cap.setDeathLoc(oldCap.getDeathLoc(), oldCap.getDeathDim());
                if (!oldCap.isUninvested()) { // make sure the new player has the same power status
                    for (Metal mt : Metal.values()) {
                        if (oldCap.hasPower(mt)) {
                            cap.addPower(mt);
                        }
                    }
                }

                if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) ||
                    !event.isWasDeath()) { // if keepInventory is true, or they didn't die, allow them to keep their metals, too
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
    public void onSetSpawn(final PlayerSetSpawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);
            cap.setSpawnLoc(event.getNewSpawn(), event.getSpawnWorld());
            Network.sync(cap, player);
        }
    }

    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            AllomancyCapability cap = AllomancyCapability.forPlayer(player);
            cap.setDeathLoc(new BlockPos(player.getPositionVec()), player.world.getDimensionKey());
            Network.sync(cap, player);
        }
    }

    @SubscribeEvent
    public void onDamage(final LivingHurtEvent event) {
        // Increase outgoing damage for pewter burners
        if (event.getSource().getTrueSource() instanceof ServerPlayerEntity) {
            ServerPlayerEntity source = (ServerPlayerEntity) event.getSource().getTrueSource();
            AllomancyCapability cap = AllomancyCapability.forPlayer(source);

            if (cap.isBurning(Metal.PEWTER)) {
                if (cap.isEnhanced()) { // Duralumin OHK
                    event.setAmount(event.getAmount() + 500);
                } else {
                    event.setAmount(event.getAmount() + 2);
                }
            }

            if (cap.isBurning(Metal.CHROMIUM)) {
                if (event.getEntityLiving() instanceof PlayerEntity) {
                    PowerUtils.wipePlayer((PlayerEntity) event.getEntityLiving());
                }
            }
        }
        // Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            AllomancyCapability capHurt = AllomancyCapability.forPlayer(event.getEntityLiving());
            if (capHurt.isBurning(Metal.PEWTER)) {
                if (capHurt.isEnhanced()) { // Duralumin invuln
                    Allomancy.LOGGER.debug("Canceling Damage");
                    event.setAmount(0);
                    event.setCanceled(true);
                } else {
                    Allomancy.LOGGER.debug("Reducing Damage");

                    event.setAmount(event.getAmount() - 2);
                    // Note that they took damage, will come in to play if they stop burning
                    capHurt.setDamageStored(capHurt.getDamageStored() + 1);
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {

            World world = event.world;
            List<? extends PlayerEntity> list = world.getPlayers();
            for (int enti = list.size() - 1; enti >= 0; enti--) {
                PlayerEntity curPlayer = list.get(enti);
                AllomancyCapability cap = AllomancyCapability.forPlayer(curPlayer);
                if (!cap.isUninvested()) {

                    /*********************************************
                     * ALUMINUM AND DURALUMIN                    *
                     *********************************************/
                    if (cap.isBurning(Metal.ALUMINUM)) {
                        PowerUtils.wipePlayer(curPlayer);
                    }
                    if (cap.isBurning(Metal.DURALUMIN) && !cap.isEnhanced()) {
                        cap.setEnhanced(2);
                        Network.sync(new UpdateEnhancedPacket(2, curPlayer.getEntityId()), curPlayer);
                    } else if (!cap.isBurning(Metal.DURALUMIN) && cap.isEnhanced()) {
                        cap.decEnhanced();
                        if (!cap.isEnhanced()) { //Enhancement ran out this tick
                            Network.sync(new UpdateEnhancedPacket(false, curPlayer.getEntityId()), curPlayer);
                            cap.drainMetals(Arrays.stream(Metal.values()).filter(cap::isBurning).toArray(Metal[]::new));
                        }
                    }


                    // Run the necessary updates on the player's metals
                    // Ran AFTER duralumin and aluminum to make sure they function correctly
                    if (curPlayer instanceof ServerPlayerEntity) {
                        cap.updateMetalBurnTime((ServerPlayerEntity) curPlayer);
                    }


                    /*********************************************
                     * CHROMIUM (enhanced)                       *
                     *********************************************/
                    if (cap.isEnhanced() && cap.isBurning(Metal.CHROMIUM)) {
                        if (world instanceof ServerWorld) {
                            int max = 20;
                            BlockPos negative = new BlockPos(curPlayer.getPositionVec()).add(-max, -max, -max);
                            BlockPos positive = new BlockPos(curPlayer.getPositionVec()).add(max, max, max);
                            world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(negative, positive)).forEach(otherPlayer -> {
                                AllomancyCapability capOther = AllomancyCapability.forPlayer(otherPlayer);
                                capOther.drainMetals(Metal.values());
                            });
                        }
                    }


                    /*********************************************
                     * GOLD AND ELECTRUM (enhanced)              *
                     *********************************************/
                    if (cap.isEnhanced() && cap.isBurning(Metal.ELECTRUM) && cap.getAmount(Metal.ELECTRUM) >= 9) {
                        RegistryKey<World> spawnDim = cap.getSpawnDim();
                        BlockPos spawnLoc;

                        if (spawnDim != null) {
                            spawnLoc = cap.getSpawnLoc();
                        } else {
                            spawnDim = World.OVERWORLD; // no spawn --> use world spawn
                            spawnLoc = new BlockPos(curPlayer.world.getWorldInfo().getSpawnX(), curPlayer.world.getWorldInfo().getSpawnY(),
                                                    curPlayer.world.getWorldInfo().getSpawnZ());

                        }

                        PowerUtils.teleport(curPlayer, world, spawnDim, spawnLoc);
                        if (cap.isBurning(Metal.DURALUMIN)) {
                            cap.drainMetals(Metal.DURALUMIN);
                        }
                        cap.drainMetals(Metal.ELECTRUM);


                    } else if (cap.isEnhanced() && cap.isBurning(Metal.GOLD) && cap.getAmount(Metal.GOLD) >= 9) { // These should be mutually exclusive
                        RegistryKey<World> deathDim = cap.getDeathDim();
                        if (deathDim != null) {
                            PowerUtils.teleport(curPlayer, world, deathDim, cap.getDeathLoc());
                            if (cap.isBurning(Metal.DURALUMIN)) {
                                cap.drainMetals(Metal.DURALUMIN);
                            }
                            cap.drainMetals(Metal.GOLD);
                        }
                    }


                    /*********************************************
                     * BENDALLOY AND CADMIUM                     *
                     *********************************************/
                    if (cap.isBurning(Metal.BENDALLOY) && !cap.isBurning(Metal.CADMIUM)) {
                        curPlayer.addPotionEffect(new EffectInstance(Effects.HASTE, 10, 3, true, false));
                        curPlayer.livingTick();
                        curPlayer.livingTick();

                        if (world instanceof ServerWorld) {
                            int max = cap.isEnhanced() ? 10 : 5;
                            BlockPos negative = new BlockPos(curPlayer.getPositionVec()).add(-max, -max, -max);
                            BlockPos positive = new BlockPos(curPlayer.getPositionVec()).add(max, max, max);
                            world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive)).forEach(entity -> {
                                entity.livingTick();
                                entity.livingTick();
                            });
                            BlockPos.getAllInBox(negative, positive).forEach(bp -> {
                                BlockState block = world.getBlockState(bp);
                                TileEntity te = world.getTileEntity(bp);
                                for (int i = 0; i < max * 4 / (te == null ? 10 : 1); i++) {
                                    if (te instanceof ITickableTileEntity) {
                                        ((ITickableTileEntity) te).tick();
                                    } else if (block.ticksRandomly()) {
                                        block.randomTick((ServerWorld) world, bp, random);
                                    }
                                }
                            });
                        }
                    }
                    if (cap.isBurning(Metal.CADMIUM) && !cap.isBurning(Metal.BENDALLOY)) {
                        int max = cap.isEnhanced() ? 20 : 10;
                        BlockPos negative = new BlockPos(curPlayer.getPositionVec()).add(-max, -max, -max);
                        BlockPos positive = new BlockPos(curPlayer.getPositionVec()).add(max, max, max);
                        int slowness_amplifier = cap.isEnhanced() ? 255 : 2; // Duralumin freezes entities
                        world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(negative, positive)).forEach(entity -> {
                            entity.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 10, 0, true, false));
                            if (entity != curPlayer) {
                                entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 10, slowness_amplifier, true, false));
                            }
                        });
                    }


                    /*********************************************
                     * TIN AND PEWTER                            *
                     *********************************************/
                    if (cap.isBurning(Metal.TIN)) {
                        // Add night vision to tin-burners
                        curPlayer.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, Short.MAX_VALUE, 5, true, false));
                        if (cap.isEnhanced()) { // Tin and Duralumin is too much to handle
                            curPlayer.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 100, 150, true, false));
                            if (world.rand.nextInt(50) == 0) {
                                curPlayer.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 0, true, false));
                            }
                        } else { // Remove blindness from normal tin burners
                            if (curPlayer.isPotionActive(Effects.BLINDNESS)) {
                                curPlayer.removePotionEffect(Effects.BLINDNESS);
                            }
                        }
                    }
                    // Remove night vision from non-tin burners if duration < 10 seconds. Related to the above issue with flashing, only if the amplifier is 5
                    if ((!cap.isBurning(Metal.TIN)) &&
                        (curPlayer.getActivePotionEffect(Effects.NIGHT_VISION) != null && curPlayer.getActivePotionEffect(Effects.NIGHT_VISION).getAmplifier() == 5)) {
                        curPlayer.removePotionEffect(Effects.NIGHT_VISION);
                    }
                    if (cap.isBurning(Metal.PEWTER)) {
                        //Add jump boost and speed to pewter burners
                        curPlayer.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 10, 1, true, false));
                        curPlayer.addPotionEffect(new EffectInstance(Effects.SPEED, 10, 0, true, false));
                        curPlayer.addPotionEffect(new EffectInstance(Effects.HASTE, 10, 1, true, false));

                        if (cap.getDamageStored() > 0) {
                            if (world.rand.nextInt(200) == 0) {
                                cap.setDamageStored(cap.getDamageStored() - 1);
                            }
                        }

                    }
                    // Damage the player if they have stored damage and pewter cuts out
                    if (!cap.isBurning(Metal.PEWTER) && (cap.getDamageStored() > 0)) {
                        cap.setDamageStored(cap.getDamageStored() - 1);
                        curPlayer.attackEntityFrom(DamageSource.MAGIC, 2);
                    }


                    /*********************************************
                     * COPPER (enhanced)                      *
                     *********************************************/
                    if (cap.isEnhanced() && cap.isBurning(Metal.COPPER)) {
                        curPlayer.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 20, 50, true, false));
                    }


                }
            }
        }
    }
}
