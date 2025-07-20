package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticle;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PowersClientSetup {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Allomancy.MODID);
    public static final RegistryObject<ParticleType<SoundParticleData>> SOUND_PARTICLE_TYPE =
            PARTICLES.register("sound_particle", () -> new ParticleType<>(true, SoundParticleData.DESERIALIZER) {
                @Override
                public Codec<SoundParticleData> codec() {
                    return null;
                }
            });

    public static void register() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void registerParticle(final RegisterParticleProvidersEvent event) {
        Allomancy.LOGGER.info("Allomancy: Registering custom particles");
        event.registerSprite(PowersClientSetup.SOUND_PARTICLE_TYPE.get(), new SoundParticle.Factory());
    }

    public static void clientInit(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> MinecraftForge.EVENT_BUS.register(ClientEventHandler.class));
    }
}
