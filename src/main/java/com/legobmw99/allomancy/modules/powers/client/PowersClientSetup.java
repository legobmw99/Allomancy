package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticle;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PowersClientSetup {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, Allomancy.MODID);
    public static final Supplier<ParticleType<SoundParticleData>> SOUND_PARTICLE_TYPE = PARTICLES.register("sound_particle",
                                                                                                           () -> new ParticleType<>(true, SoundParticleData.DESERIALIZER) {
                                                                                                               @Override
                                                                                                               public Codec<SoundParticleData> codec() {
                                                                                                                   return SoundParticleData.CODEC;
                                                                                                               }
                                                                                                           });

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }

    public static void registerParticle(final RegisterParticleProvidersEvent event) {
        Allomancy.LOGGER.info("Allomancy: Registering custom particles");
        event.registerSprite(PowersClientSetup.SOUND_PARTICLE_TYPE.get(), new SoundParticle.Factory());
    }

    public static void clientInit(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            NeoForge.EVENT_BUS.register(ClientEventHandler.class);
        });
    }
}
