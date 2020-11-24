package com.legobmw99.allomancy.modules.powers.client.particle;

import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.registry.Registry;

public class SoundParticleData implements IParticleData {


    public static final IParticleData.IDeserializer<SoundParticleData> DESERIALIZER = new IParticleData.IDeserializer<SoundParticleData>() {

        @Override
        public SoundParticleData deserialize(ParticleType<SoundParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new SoundParticleData(SoundCategory.AMBIENT);
        }

        @Override
        public SoundParticleData read(ParticleType<SoundParticleData> particleTypeIn, PacketBuffer buffer) {
            return new SoundParticleData(buffer.readEnumValue(SoundCategory.class));
        }
    };
    private final SoundCategory type;

    public SoundParticleData(SoundCategory type) {
        this.type = type;
    }

    protected SoundCategory getSoundType() {
        return this.type;
    }

    @Override
    public ParticleType<?> getType() {
        return PowersClientSetup.SOUND_PARTICLE_TYPE.get();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeEnumValue(this.type);
    }

    @Override
    public String getParameters() {
        return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + this.type.toString();
    }
}
