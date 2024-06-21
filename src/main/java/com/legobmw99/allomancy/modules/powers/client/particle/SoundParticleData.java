package com.legobmw99.allomancy.modules.powers.client.particle;

import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public class SoundParticleData implements ParticleOptions {

    public static final MapCodec<SoundParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.INT.fieldOf("type").forGetter(d -> d.getSoundType().ordinal()))
            .apply(instance, s -> new SoundParticleData(SoundSource.values()[s])));

    public static final StreamCodec<FriendlyByteBuf, SoundParticleData> STREAM_CODEC =
            StreamCodec.composite(NeoForgeStreamCodecs.enumCodec(SoundSource.class), SoundParticleData::getSoundType,
                                  SoundParticleData::new);


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

}
