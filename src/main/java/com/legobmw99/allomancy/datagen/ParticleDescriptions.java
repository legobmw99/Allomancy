package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.ParticleDescriptionProvider;

class ParticleDescriptions extends ParticleDescriptionProvider {

    protected ParticleDescriptions(PackOutput output) {
        super(output);
    }

    @Override
    protected void addDescriptions() {
        spriteSet(PowersClientSetup.SOUND_PARTICLE_TYPE.get(), Allomancy.id("sound_particle"));
    }
}
