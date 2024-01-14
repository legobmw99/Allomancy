package com.legobmw99.allomancy.modules.materials.world;

import com.google.common.base.Suppliers;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LerasiumLootModifier extends LootModifier {

    public final int chance_one_in;
    public static final Supplier<Codec<LerasiumLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(
            inst -> codecStart(inst).and(Codec.INT.fieldOf("chance_one_in").forGetter(t -> t.chance_one_in)).apply(inst, LerasiumLootModifier::new)));

    public LerasiumLootModifier(LootItemCondition[] conditionsIn, int chance_one_in) {
        super(conditionsIn);
        this.chance_one_in = chance_one_in;
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextInt(this.chance_one_in) == 0) {
            generatedLoot.add(new ItemStack(ConsumeSetup.LERASIUM_NUGGET.get()));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
