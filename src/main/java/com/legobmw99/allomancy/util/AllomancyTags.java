package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.ArrayList;
import java.util.List;

public final class AllomancyTags {
    public static final TagKey<Item> FLAKES_TAG = ItemTags.create(Allomancy.rl("metal_flakes"));
    public static final TagKey<Item> LERASIUM_CONVERSION = ItemTags.create(Allomancy.rl("converts_to_lerasium"));
    public static final TagKey<Item> OBSIDIAN_REPAIR = ItemTags.create(Allomancy.rl("obsidian_tool_materials"));
    public static final TagKey<Item> REPAIRS_MISTCLOAK = ItemTags.create(Allomancy.rl("repairs_wool_armor"));
    public static final TagKey<Item> REPAIRS_ALUMINUM = ItemTags.create(Allomancy.rl("repairs_aluminum_armor"));
    public static final TagKey<Item> TIN_FOIL_HATS = ItemTags.create(Allomancy.rl("tin_foil_hats"));
    public static final TagKey<Item> SPECIAL_EARRINGS = ItemTags.create(Allomancy.rl("well_seeking_helmets"));
    public static final TagKey<Item> ONE_HIT_WEAPONS =
            ItemTags.create(Allomancy.rl("duralumin_pewter_instakill_weapons"));

    public static final TagKey<Biome> SPAWNS_WELLS =
            TagKey.create(Registries.BIOME, Allomancy.rl("has_structure/well"));
    public static final TagKey<Structure> SEEKABLE = TagKey.create(Registries.STRUCTURE, Allomancy.rl("seekable"));

    public static final TagKey<EntityType<?>> HEMALURGIC_CHARGERS =
            TagKey.create(Registries.ENTITY_TYPE, Allomancy.rl("killing_charges_earring"));

    public static final TagKey<DamageType> IS_COIN_HIT =
            TagKey.create(Registries.DAMAGE_TYPE, Allomancy.rl("is_coin_hit"));

    public static final List<TagKey<BannerPattern>> PATTERN_TAGS = new ArrayList<>();

    private AllomancyTags() {}
}
