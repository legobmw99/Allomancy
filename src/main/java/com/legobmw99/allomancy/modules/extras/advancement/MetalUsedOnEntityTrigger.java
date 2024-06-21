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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Optional;

/**
 * Triggered when a player affects a mob with Allomancy
 * This currently only means: zinc, brass, iron, steel
 */
public class MetalUsedOnEntityTrigger extends SimpleCriterionTrigger<MetalUsedOnEntityTrigger.TriggerInstance> {
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Entity entity, Metal mt, boolean enhanced) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, p_48112_ -> p_48112_.matches(lootcontext, mt, enhanced));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<ContextAwarePredicate> entityPredicate, Metal mt,
                                  Optional<Boolean> enhanced) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                       EntityPredicate.ADVANCEMENT_CODEC
                               .optionalFieldOf("entity")
                               .forGetter(TriggerInstance::entityPredicate),
                       Metal.CODEC.fieldOf("metal").forGetter(TriggerInstance::mt),
                       Codec.BOOL.optionalFieldOf("enhanced").forGetter(TriggerInstance::enhanced))
                .apply(builder, TriggerInstance::new));

        public static Criterion<TriggerInstance> instance(Optional<ContextAwarePredicate> player,
                                                          Optional<ContextAwarePredicate> entityPredicate,
                                                          Metal mt) {
            return ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(player, entityPredicate, mt, Optional.empty()));
        }

        public static Criterion<TriggerInstance> instance(Optional<ContextAwarePredicate> player,
                                                          Optional<ContextAwarePredicate> entityPredicate,
                                                          Metal mt,
                                                          boolean enhanced) {
            return ExtrasSetup.METAL_USED_ON_ENTITY_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(player, entityPredicate, mt, Optional.of(enhanced)));
        }

        boolean matches(LootContext entity, Metal mt, boolean enhanced) {
            return this.mt == mt && (this.entityPredicate.isEmpty() || this.entityPredicate.get().matches(entity)) &&
                   (this.enhanced().isEmpty() || this.enhanced().get() == enhanced);
        }
    }
}
