package com.legobmw99.allomancy.modules.powers.client.particle;

import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;

public class SoundParticleData implements ParticleOptions {

    public static final Codec<SoundParticleData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(Codec.INT.fieldOf("type").forGetter(d -> d.getSoundType().ordinal())).apply(instance, s -> new SoundParticleData(SoundSource.values()[s])));
    public static final ParticleOptions.Deserializer<SoundParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {

        @Override
        public SoundParticleData fromCommand(ParticleType<SoundParticleData> particleTypeIn, StringReader reader) {
            return new SoundParticleData(SoundSource.AMBIENT);
        }

        @Override
        public SoundParticleData fromNetwork(ParticleType<SoundParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new SoundParticleData(buffer.readEnum(SoundSource.class));
        }
    };
    private final SoundSource type;

    public SoundParticleData(SoundSource type) {
        this.type = type;
    }

    protected SoundSource getSoundType() {
        return this.type;
    }

    @Override
    public ParticleType<?> getType() {
        return PowersClientSetup.SOUND_PARTICLE_TYPE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.type);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + this.type.toString();
    }
}
