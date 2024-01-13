package com.legobmw99.allomancy.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.combat.item.CoinBagItem;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.combat.item.MistcloakItem;
import com.legobmw99.allomancy.modules.combat.item.ObsidianDaggerItem;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CombatSetup {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Allomancy.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final TagKey<DamageType> IS_COIN_HIT = TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Allomancy.MODID, "is_coin_hit"));
    public static final ResourceKey<DamageType> COIN_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Allomancy.MODID, "coin"));
    public static final DeferredItem<CoinBagItem> COIN_BAG = ITEMS.register("coin_bag", CoinBagItem::new);
    public static final DeferredItem<ObsidianDaggerItem> OBSIDIAN_DAGGER = ITEMS.register("obsidian_dagger", ObsidianDaggerItem::new);
    public static final DeferredItem<KolossBladeItem> KOLOSS_BLADE = ITEMS.register("koloss_blade", KolossBladeItem::new);
    public static final ArmorMaterial WoolArmor = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return 50;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return type == ArmorItem.Type.CHESTPLATE ? 4 : 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 15;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(Items.GRAY_WOOL);
        }

        @Override
        public String getName() {
            return "allomancy:wool";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }

    };

    public static final ArmorMaterial AluminumArmor = new ArmorMaterial() {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return 155;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return type == ArmorItem.Type.HELMET ? 2 : 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()));
        }

        @Override
        public String getName() {
            return "allomancy:aluminum";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    };
    public static final DeferredItem<MistcloakItem> MISTCLOAK = ITEMS.register("mistcloak", MistcloakItem::new);

    public static final DeferredItem<ArmorItem> ALUMINUM_HELMET = ITEMS.register("aluminum_helmet",
                                                                                 () -> new ArmorItem(AluminumArmor, ArmorItem.Type.HELMET, new Item.Properties()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        ITEMS.register(bus);
    }

    public static final Supplier<EntityType<ProjectileNuggetEntity>> NUGGET_PROJECTILE = ENTITIES.register("nugget_projectile", () -> EntityType.Builder
            .<ProjectileNuggetEntity>of(ProjectileNuggetEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(20)
            .sized(0.25F, 0.25F)
            .build("nugget_projectile"));
}
