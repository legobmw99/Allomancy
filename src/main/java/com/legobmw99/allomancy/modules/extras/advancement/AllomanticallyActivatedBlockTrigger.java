package com.legobmw99.allomancy.modules.extras.advancement;

import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class AllomanticallyActivatedBlockTrigger extends SimpleCriterionTrigger<AllomanticallyActivatedBlockTrigger.TriggerInstance> {

    public void trigger(ServerPlayer player, BlockPos blockPos, boolean isPush) {
        ServerLevel serverlevel = player.level();
        BlockState blockstate = serverlevel.getBlockState(blockPos);
        LootParams lootparams = new LootParams.Builder(serverlevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
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

    public record TriggerInstance(Optional<Holder<LootItemCondition>> player,
                                  Optional<Holder<LootItemCondition>> location,
                                  Optional<Boolean> isPush) implements SimpleInstance {
        static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(builder -> builder
                .group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                       LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::location),
                       Codec.BOOL.optionalFieldOf("is_push").forGetter(TriggerInstance::isPush))
                .apply(builder, TriggerInstance::new));

        public static Criterion<TriggerInstance> activatedBlock(HolderGetter<Block> lookup, Block block) {
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(
                            Holder.direct(MatchBlock.blockMatches(lookup, block).build())), Optional.empty()));
        }

        public static Criterion<TriggerInstance> activatedBlock(HolderGetter<Block> lookup,
                                                                Block block,
                                                                boolean isPush) {
            return ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER
                    .get()
                    .createCriterion(new TriggerInstance(Optional.empty(), Optional.of(
                            Holder.direct(MatchBlock.blockMatches(lookup, block).build())), Optional.of(isPush)));
        }

        public static Criterion<TriggerInstance> pushBlock(HolderGetter<Block> lookup, Block block) {
            return activatedBlock(lookup, block, true);
        }

        public static Criterion<TriggerInstance> pullBlock(HolderGetter<Block> lookup, Block block) {
            return activatedBlock(lookup, block, false);
        }

        boolean matches(LootContext ctx, boolean is_push) {
            return (this.isPush.isEmpty() || this.isPush.get() == is_push) &&
                   (this.location.isEmpty() || this.location.get().value().test(ctx));
        }

    }
}
