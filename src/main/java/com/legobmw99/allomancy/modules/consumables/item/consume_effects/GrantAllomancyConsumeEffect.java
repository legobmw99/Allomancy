package com.legobmw99.allomancy.modules.consumables.item.consume_effects;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.Network;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public final class GrantAllomancyConsumeEffect implements ConsumeEffect {
    private final EnumSet<Metal> powers;

    private GrantAllomancyConsumeEffect(EnumSet<Metal> powers) {this.powers = powers;}

    public static GrantAllomancyConsumeEffect makeMistborn() {
        return new GrantAllomancyConsumeEffect(EnumSet.allOf(Metal.class));
    }

    public static final MapCodec<GrantAllomancyConsumeEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(Codec.list(Metal.CODEC).fieldOf("powers").forGetter(eff -> eff.powers.stream().toList()))
            .apply(inst, powers -> {
                var effect = new Mutable();
                for (Metal mt : powers) {
                    effect.add(mt);
                }
                return effect.toImmutable();
            }));

    public static final StreamCodec<RegistryFriendlyByteBuf, GrantAllomancyConsumeEffect> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(RegistryFriendlyByteBuf buffer, GrantAllomancyConsumeEffect value) {
                    buffer.writeEnumSet(value.powers, Metal.class);
                }

                @Override
                public GrantAllomancyConsumeEffect decode(RegistryFriendlyByteBuf buffer) {
                    return new GrantAllomancyConsumeEffect(buffer.readEnumSet(Metal.class));
                }
            };


    @Override
    public Type<GrantAllomancyConsumeEffect> getType() {
        return ConsumeSetup.GRANT_ALLOMANCY_ON_CONSUME.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        boolean addedPower = false;
        if (entity instanceof Player player) {
            var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
            for (Metal mt : powers) {
                if (!data.hasPower(mt)) {
                    data.addPower(mt);
                    addedPower = true;
                }
            }
        }
        if (addedPower && entity instanceof ServerPlayer player) {
            Network.syncAllomancerData(player);
        }
        return addedPower;
    }

    public static class Mutable {
        private final EnumSet<Metal> flakes = EnumSet.noneOf(Metal.class);

        public Mutable add(Metal mt) {
            this.flakes.add(mt);
            return this;
        }

        public GrantAllomancyConsumeEffect toImmutable() {
            if (this.flakes.isEmpty()) {
                return null;
            }
            return new GrantAllomancyConsumeEffect(this.flakes);
        }
    }

}
