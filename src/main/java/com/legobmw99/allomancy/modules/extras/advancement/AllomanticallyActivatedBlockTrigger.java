package com.legobmw99.allomancy.modules.extras.advancement;

import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.Arrays;
import java.util.Optional;

public class AllomanticallyActivatedBlockTrigger extends SimpleCriterionTrigger<AllomanticallyActivatedBlockTrigger.TriggerInstance> {

    public void trigger(ServerPlayer player, BlockPos blockPos, boolean isPush) {
        ServerLevel serverlevel = player.serverLevel();
        BlockState blockstate = serverlevel.getBlockState(blockPos);
        LootParams lootparams = new LootParams.Builder(serverlevel)
                .withParameter(LootContextParams.ORIGIN, blockPos.getCenter())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.BLOCK_STATE, blockstate)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                .create(LootContextParamSets.ADVANCEMENT_LOCATION);
        LootContext lootcontext = new LootContext.Builder(lootparams).create(Optional.empty());
        this.trigger(player, p_286596_ -> p_286596_.matches(lootcontext, isPush));
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> location, Optional<Boolean> isPush) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                       ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "entity").forGetter(TriggerInstance::location),
                       ExtraCodecs.strictOptionalField(Codec.BOOL, "is_push").forGetter(TriggerInstance::isPush))
                .apply(builder, TriggerInstance::new));

        public static Criterion<TriggerInstance> activatedBlock(Block block) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).build());
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.empty()));
        }

        public static Criterion<TriggerInstance> activatedBlock(LootItemCondition.Builder... p_301013_) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(
                    Arrays.stream(p_301013_).map(LootItemCondition.Builder::build).toArray(LootItemCondition[]::new));
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.empty()));
        }

        public static Criterion<TriggerInstance> pushBlock(Block block) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).build());
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.of(true)));
        }

        public static Criterion<TriggerInstance> pushBlock(LootItemCondition.Builder... p_301013_) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(
                    Arrays.stream(p_301013_).map(LootItemCondition.Builder::build).toArray(LootItemCondition[]::new));
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.of(true)));
        }

        public static Criterion<TriggerInstance> pullBlock(Block block) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).build());
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.of(false)));
        }

        public static Criterion<TriggerInstance> pullBlock(LootItemCondition.Builder... p_301013_) {
            ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(
                    Arrays.stream(p_301013_).map(LootItemCondition.Builder::build).toArray(LootItemCondition[]::new));
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate), Optional.of(false)));
        }

        public boolean matches(LootContext ctx, boolean is_push) {
            return (this.isPush.isEmpty() || this.isPush.get() == is_push) && (this.location.isEmpty() || this.location.get().matches(ctx));
        }

    }
}
