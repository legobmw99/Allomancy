package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

class ParticleDescriptions extends ParticleDescriptionProvider {

    protected ParticleDescriptions(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        sprite(PowersClientSetup.SOUND_PARTICLE_TYPE.get(),
               ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "sound_particle"));

    }
}
