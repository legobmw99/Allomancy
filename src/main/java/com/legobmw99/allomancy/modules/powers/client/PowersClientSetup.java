package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalSelectScreen;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticle;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.modules.powers.client.util.Inputs;
import com.legobmw99.allomancy.modules.powers.client.util.Rendering;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class PowersClientSetup {
    private static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Allomancy.MODID);
    public static final Supplier<ParticleType<SoundParticleData>> SOUND_PARTICLE_TYPE =
            PARTICLES.register("sound_particle", () -> new ParticleType<>(true) {


                @Override
                public MapCodec<SoundParticleData> codec() {
                    return SoundParticleData.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, SoundParticleData> streamCodec() {
                    return SoundParticleData.STREAM_CODEC;
                }
            });

    private PowersClientSetup() {}

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);

        bus.addListener(PowersClientSetup::registerParticle);
        bus.addListener(MetalOverlay::registerGUI);
        bus.addListener(Inputs::registerKeyBinding);
        bus.addListener(Rendering::registerPipeline);
        bus.addListener(MetalSelectScreen.SelectionWheelRenderer::registerPipeline);
        bus.addListener(MetalSelectScreen.SelectionWheelRenderer::registerPiP);
    }

    private static void registerParticle(final RegisterParticleProvidersEvent event) {
        Allomancy.LOGGER.info("Allomancy: Registering custom particles");
        event.registerSprite(SOUND_PARTICLE_TYPE.get(), new SoundParticle.Factory());
    }


}
