package com.legobmw99.allomancy.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.combat.item.CoinBagItem;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.combat.item.ObsidianDaggerItem;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public final class CombatSetup {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Allomancy.MODID);

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final TagKey<DamageType> IS_COIN_HIT = TagKey.create(Registries.DAMAGE_TYPE,
                                                                       ResourceLocation.fromNamespaceAndPath(
                                                                               Allomancy.MODID, "is_coin_hit"));
    public static final ResourceKey<DamageType> COIN_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
                                                                                 ResourceLocation.fromNamespaceAndPath(
                                                                                         Allomancy.MODID, "coin"));
    public static final DeferredItem<CoinBagItem> COIN_BAG = ITEMS.register("coin_bag", CoinBagItem::new);
    public static final DeferredItem<ObsidianDaggerItem> OBSIDIAN_DAGGER =
            ITEMS.register("obsidian_dagger", ObsidianDaggerItem::new);
    public static final DeferredItem<KolossBladeItem> KOLOSS_BLADE =
            ITEMS.register("koloss_blade", KolossBladeItem::new);
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, Allomancy.MODID);

    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> WOOL_ARMOR =

            ARMOR_MATERIALS.register("wool",
                                     () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                         map.put(ArmorItem.Type.BOOTS, 0);
                                         map.put(ArmorItem.Type.LEGGINGS, 0);
                                         map.put(ArmorItem.Type.CHESTPLATE, 4);
                                         map.put(ArmorItem.Type.HELMET, 0);
                                         map.put(ArmorItem.Type.BODY, 0);
                                     }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(Items.GRAY_WOOL),
                                                             List.of(new ArmorMaterial.Layer(
                                                                     ResourceLocation.fromNamespaceAndPath(
                                                                             Allomancy.MODID, "wool"))), 0, 0));

    private static final DeferredHolder<ArmorMaterial, ArmorMaterial> ALUMINUM_ARMOR =
            ARMOR_MATERIALS.register("aluminum",
                                     () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                                         map.put(ArmorItem.Type.BOOTS, 0);
                                         map.put(ArmorItem.Type.LEGGINGS, 0);
                                         map.put(ArmorItem.Type.CHESTPLATE, 0);
                                         map.put(ArmorItem.Type.HELMET, 2);
                                         map.put(ArmorItem.Type.BODY, 0);
                                     }), 0, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(
                                             MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex())),
                                                             List.of(new ArmorMaterial.Layer(
                                                                     ResourceLocation.fromNamespaceAndPath(
                                                                             Allomancy.MODID, "aluminum"))), 0, 0));


    private static final Item.Properties MISTCLOAK_PROPS =

            new Item.Properties().attributes(ItemAttributeModifiers
                                                     .builder()
                                                     .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                                                                  ResourceLocation.fromNamespaceAndPath(Allomancy.MODID,
                                                                                                        "mistcloak_speed"),
                                                                  0.25,
                                                                  AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                                          EquipmentSlotGroup.CHEST)
                                                     .build());

    public static final DeferredItem<ArmorItem> MISTCLOAK =
            ITEMS.register("mistcloak", () -> new ArmorItem(WOOL_ARMOR, ArmorItem.Type.CHESTPLATE, MISTCLOAK_PROPS));

    public static final DeferredItem<ArmorItem> ALUMINUM_HELMET = ITEMS.register("aluminum_helmet",
                                                                                 () -> new ArmorItem(ALUMINUM_ARMOR,
                                                                                                     ArmorItem.Type.HELMET,
                                                                                                     new Item.Properties()));


    public static final Supplier<EntityType<ProjectileNuggetEntity>> NUGGET_PROJECTILE =
            ENTITIES.register("nugget_projectile", () -> EntityType.Builder
                    .<ProjectileNuggetEntity>of(ProjectileNuggetEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setUpdateInterval(20)
                    .sized(0.25F, 0.25F)
                    .build("nugget_projectile"));

    private CombatSetup() {}

    public static void register(IEventBus bus) {
        ARMOR_MATERIALS.register(bus);
        ENTITIES.register(bus);
        ITEMS.register(bus);
    }
}
