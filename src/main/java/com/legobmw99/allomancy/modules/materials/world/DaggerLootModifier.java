package com.legobmw99.allomancy.modules.materials.world;

import com.google.common.base.Suppliers;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DaggerLootModifier extends LootModifier {

    public final int chance_one_in;
    public static final Supplier<MapCodec<DaggerLootModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
                    .and(Codec.INT.fieldOf("chance_one_in").forGetter(t -> t.chance_one_in))
                    .apply(inst, DaggerLootModifier::new)));

    public DaggerLootModifier(LootItemCondition[] conditionsIn, int chance_one_in) {
        super(conditionsIn);
        this.chance_one_in = chance_one_in;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
                                                          LootContext context) {
        if (context.getRandom().nextInt(this.chance_one_in) == 0) {
            ItemStack dagger = new ItemStack(CombatSetup.OBSIDIAN_DAGGER.get());
            dagger.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
            generatedLoot.add(dagger);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
