package com.legobmw99.allomancy.modules.extras.advancement;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * Triggered when a player is affected by Allomancy from another player
 * This currently only means: chromium, nicrosil, iron, steel
 */
public class MetalUsedOnPlayerTrigger extends SimpleCriterionTrigger<MetalUsedOnPlayerTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Metal mt, boolean enhanced) {
        this.trigger(player, p_48112_ -> p_48112_.matches(mt, enhanced));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Metal mt,
                                  Optional<Boolean> enhanced) implements SimpleInstance {
        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                       Metal.CODEC.fieldOf("metal").forGetter(TriggerInstance::mt),
                       Codec.BOOL.optionalFieldOf("enhanced").forGetter(TriggerInstance::enhanced))
                .apply(builder, TriggerInstance::new));


        public static Criterion<TriggerInstance> instance(ContextAwarePredicate player, Metal mt) {
            return ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.ofNullable(player), mt, Optional.empty()));
        }

        public static Criterion<TriggerInstance> instance(ContextAwarePredicate player, Metal mt, boolean enhanced) {
            return ExtrasSetup.METAL_USED_ON_PLAYER_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.ofNullable(player), mt, Optional.of(enhanced)));
        }


        boolean matches(Metal mt, boolean enhanced) {
            return this.mt == mt && (this.enhanced().isEmpty() || this.enhanced().get() == enhanced);
        }


    }
}
