package com.legobmw99.allomancy.modules.world.loot;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public record PlayerInvestmentCondition(Metal power) implements LootItemCondition {

    public static final MapCodec<PlayerInvestmentCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Metal.CODEC.fieldOf("power").forGetter(PlayerInvestmentCondition::power))
            .apply(instance, PlayerInvestmentCondition::new));


    @Override
    public LootItemConditionType getType() {
        return WorldSetup.PLAYER_INVESTMENT.get();
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY, LootContextParams.ATTACKING_ENTITY);
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof Player player) {
            return AllomancerAttachment.get(player).hasPower(this.power);
        }
        if (lootContext.getOptionalParameter(LootContextParams.ATTACKING_ENTITY) instanceof Player player) {
            return AllomancerAttachment.get(player).hasPower(this.power);
        }

        return false;
    }

    public static class Builder implements LootItemCondition.Builder {
        private final Metal metal;

        public Builder(Metal metal) {
            this.metal = metal;
        }

        public PlayerInvestmentCondition build() {
            return new PlayerInvestmentCondition(this.metal);
        }
    }
}
