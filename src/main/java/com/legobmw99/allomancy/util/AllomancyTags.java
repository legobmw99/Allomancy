package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class AllomancyTags {
    public static final TagKey<Item> LERASIUM_CONVERSION = ItemTags.create(Allomancy.rl("converts_to_lerasium"));


    public static final TagKey<Biome> SPAWNS_WELLS =
            TagKey.create(Registries.BIOME, Allomancy.rl("has_structure/well"));
    public static final TagKey<Structure> SEEKABLE = TagKey.create(Registries.STRUCTURE, Allomancy.rl("seekable"));

    public static final TagKey<EntityType<?>> HEMALURGIC_CHARGERS =
            TagKey.create(Registries.ENTITY_TYPE, Allomancy.rl("killing_charges_earring"));

    public static final TagKey<DamageType> IS_COIN_HIT =
            TagKey.create(Registries.DAMAGE_TYPE, Allomancy.rl("is_coin_hit"));


    private AllomancyTags() {}
}