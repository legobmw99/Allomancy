package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticle;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.mojang.serialization.Codec;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

public class PowersClientSetup {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Allomancy.MODID);
    public static final RegistryObject<ParticleType<SoundParticleData>> SOUND_PARTICLE_TYPE = PARTICLES.register("sound_particle",
                                                                                                                 () -> new ParticleType<>(true, SoundParticleData.DESERIALIZER) {
                                                                                                                     @Override
                                                                                                                     public Codec<SoundParticleData> codec() {
                                                                                                                         return null;
                                                                                                                     }
                                                                                                                 });
    @OnlyIn(Dist.CLIENT)
    public static KeyMapping hud;

    @OnlyIn(Dist.CLIENT)
    public static KeyMapping burn;

    public static boolean enable_more_keybinds;

    @OnlyIn(Dist.CLIENT)
    public static KeyMapping[] powers;

    public static void initKeyBindings() {
        burn = new KeyMapping("key.burn", GLFW.GLFW_KEY_V, "key.categories.allomancy");
        hud = new KeyMapping("key.hud", GLFW.GLFW_KEY_UNKNOWN, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
        ClientRegistry.registerKeyBinding(hud);

        enable_more_keybinds = PowersConfig.enable_more_keybinds.get();

        if (enable_more_keybinds) {
            powers = new KeyMapping[Metal.values().length];
            for (int i = 0; i < powers.length; i++) {
                powers[i] = new KeyMapping("metals." + Metal.getMetal(i).name().toLowerCase(), GLFW.GLFW_KEY_UNKNOWN, "key.categories.allomancy");
                ClientRegistry.registerKeyBinding(powers[i]);
            }
        }

    }

    public static void register() {
        PARTICLES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticle(ParticleFactoryRegisterEvent event) {
        Allomancy.LOGGER.info("Allomancy: Registering custom particles");
        Minecraft.getInstance().particleEngine.register(PowersClientSetup.SOUND_PARTICLE_TYPE.get(), SoundParticle.Factory::new);
    }

}
