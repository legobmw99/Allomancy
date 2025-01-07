package com.legobmw99.allomancy.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.combat.item.CoinBagItem;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.combat.item.ObsidianDaggerItem;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.function.Supplier;

public final class CombatSetup {
    private static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Allomancy.MODID);

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final TagKey<DamageType> IS_COIN_HIT =
            TagKey.create(Registries.DAMAGE_TYPE, Allomancy.rl("is_coin_hit"));
    public static final ResourceKey<DamageType> COIN_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, Allomancy.rl("coin"));
    public static final DeferredItem<CoinBagItem> COIN_BAG = ITEMS.registerItem("coin_bag", CoinBagItem::new);
    public static final DeferredItem<ObsidianDaggerItem> OBSIDIAN_DAGGER =
            ITEMS.registerItem("obsidian_dagger", ObsidianDaggerItem::new);
    public static final DeferredItem<KolossBladeItem> KOLOSS_BLADE =
            ITEMS.registerItem("koloss_blade", KolossBladeItem::new);

    public static final ResourceKey<EquipmentAsset> WOOL =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Allomancy.rl("wool"));


    public static final TagKey<Item> REPAIRS_MISTCLOAK = ItemTags.create(Allomancy.rl("repairs_wool_armor"));


    private static final ArmorMaterial WOOL_ARMOR =
            new ArmorMaterial(5, Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 4);
                map.put(ArmorType.HELMET, 0);
                map.put(ArmorType.BODY, 0);
            }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, REPAIRS_MISTCLOAK, WOOL);

    public static final DeferredItem<Item> MISTCLOAK = ITEMS.registerItem("mistcloak", (props) -> new Item(
            WOOL_ARMOR.humanoidProperties(props, ArmorType.CHESTPLATE)
                      // note: overrides normal armor, which is fine
                      .attributes(ItemAttributeModifiers
                                          .builder()
                                          .add(Attributes.MOVEMENT_SPEED,
                                               new AttributeModifier(Allomancy.rl("mistcloak_speed"), 0.25,
                                                                     AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                                               EquipmentSlotGroup.CHEST)
                                          .build())));


    // TODO: would be nice if this used the iron_darker override
    public static final ResourceKey<EquipmentAsset> ALUMINUM =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Allomancy.rl("aluminum"));


    public static final TagKey<Item> REPAIRS_ALUMINUM = ItemTags.create(Allomancy.rl("repairs_aluminum_armor"));

    private static final ArmorMaterial ALUMINUM_ARMOR =
            new ArmorMaterial(15, Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 0);
                map.put(ArmorType.LEGGINGS, 0);
                map.put(ArmorType.CHESTPLATE, 0);
                map.put(ArmorType.HELMET, 2);
                map.put(ArmorType.BODY, 0);
            }), 1, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, REPAIRS_ALUMINUM, ALUMINUM);


    public static final DeferredItem<Item> ALUMINUM_HELMET =

            ITEMS.registerItem("aluminum_helmet", (props) -> new Item(ALUMINUM_ARMOR
                                                                              .humanoidProperties(props,
                                                                                                  ArmorType.HELMET)
                                                                              .component(DataComponents.ENCHANTABLE,
                                                                                         null)) {
                @Override
                public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
                    return false;
                }
            });


    public static final Supplier<EntityType<ProjectileNuggetEntity>> NUGGET_PROJECTILE =
            ENTITIES.register("nugget_projectile", () -> EntityType.Builder
                    .<ProjectileNuggetEntity>of(ProjectileNuggetEntity::new, MobCategory.MISC)
                    .setShouldReceiveVelocityUpdates(true)
                    .setUpdateInterval(20)
                    .sized(0.25F, 0.25F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Allomancy.rl("nugget_projectile"))));

    private CombatSetup() {}

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        ITEMS.register(bus);
    }
}
