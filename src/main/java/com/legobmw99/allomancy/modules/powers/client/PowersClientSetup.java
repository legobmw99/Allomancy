package com.legobmw99.allomancy.modules.powers.client;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticle;
import com.legobmw99.allomancy.modules.powers.client.particle.SoundParticleData;
import com.legobmw99.allomancy.setup.Metal;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

public class PowersClientSetup {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Allomancy.MODID);
    public static final RegistryObject<ParticleType<SoundParticleData>> SOUND_PARTICLE_TYPE = PARTICLES.register("sound_particle", () -> new ParticleType<SoundParticleData>(true,
                                                                                                                                                                             SoundParticleData.DESERIALIZER) {
        @Override
        public Codec<SoundParticleData> func_230522_e_() {
            return null;
        }
    });
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding hud;

    @OnlyIn(Dist.CLIENT)
    public static KeyBinding burn;

    public static boolean enable_more_keybinds;

    @OnlyIn(Dist.CLIENT)
    public static final KeyBinding[] powers = new KeyBinding[Metal.values().length];

    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", GLFW.GLFW_KEY_V, "key.categories.allomancy");
        hud = new KeyBinding("key.hud", GLFW.GLFW_KEY_UNKNOWN, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
        ClientRegistry.registerKeyBinding(hud);

        enable_more_keybinds = PowersConfig.enable_more_keybinds.get();

        if (enable_more_keybinds) {
            for (int i = 0; i < powers.length; i++) {
                powers[i] = new KeyBinding("metals." + Metal.getMetal(i).name().toLowerCase(), GLFW.GLFW_KEY_UNKNOWN, "key.categories.allomancy");
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
        Minecraft.getInstance().particles.registerFactory(PowersClientSetup.SOUND_PARTICLE_TYPE.get(), SoundParticle.Factory::new);
    }

}
