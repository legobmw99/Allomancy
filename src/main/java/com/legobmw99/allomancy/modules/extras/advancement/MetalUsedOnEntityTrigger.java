package com.legobmw99.allomancy.modules.extras.advancement;

import com.legobmw99.allomancy.api.enums.Metal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

// TODO https://docs.neoforged.net/docs/resources/server/advancements/
// would also neet MetalUsedOnPlayer for "tin foil hat"

public class MetalUsedOnEntityTrigger extends SimpleCriterionTrigger<MetalUsedOnEntityTrigger.TriggerInstance> {
    @Override
    public Codec codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Entity entity, Metal mt) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, p_48112_ -> p_48112_.matches(lootcontext, mt));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate,
                                  Metal mt) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                       ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "entity").forGetter(TriggerInstance::entityPredicate),
                       Codec.INT.fieldOf("metal").forGetter(t -> t.mt().ordinal()))
                .apply(builder, TriggerInstance::new));

        TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entityPredicate, int mt) {
            this(player, entityPredicate, Metal.values()[mt]);
        }

        boolean matches(LootContext entity, Metal mt) {
            return this.mt == mt && (this.entityPredicate.isEmpty() || this.entityPredicate.get().matches(entity));
        }
    }
}
