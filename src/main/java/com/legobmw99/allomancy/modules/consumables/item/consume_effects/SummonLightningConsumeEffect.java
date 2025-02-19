package com.legobmw99.allomancy.modules.consumables.item.consume_effects;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public record SummonLightningConsumeEffect() implements ConsumeEffect {

    public static final SummonLightningConsumeEffect INSTANCE = new SummonLightningConsumeEffect();
    public static final MapCodec<SummonLightningConsumeEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, SummonLightningConsumeEffect> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public Type<SummonLightningConsumeEffect> getType() {
        return ConsumeSetup.SUMMON_LIGHTNING_ON_CONSUME.get();
    }

    @Override
    public boolean apply(Level level, ItemStack stack, LivingEntity entity) {
        LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
        lightning.setVisualOnly(true);
        lightning.moveTo(entity.position().add(0, 3, 0));
        level.addFreshEntity(lightning);
        return true;
    }
}
