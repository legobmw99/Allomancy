package com.legobmw99.allomancy.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.combat.item.CoinBagItem;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.combat.item.MistcloakItem;
import com.legobmw99.allomancy.modules.combat.item.ObsidianDaggerItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CombatSetup {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);
    public static final RegistryObject<CoinBagItem> COIN_BAG = ITEMS.register("coin_bag", CoinBagItem::new);
    public static final RegistryObject<ObsidianDaggerItem> OBSIDIAN_DAGGER = ITEMS.register("obsidian_dagger", ObsidianDaggerItem::new);
    public static final RegistryObject<KolossBladeItem> KOLOSS_BLADE = ITEMS.register("koloss_blade", KolossBladeItem::new);
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
    public static final RegistryObject<MistcloakItem> MISTCLOAK = ITEMS.register("mistcloak", MistcloakItem::new);

    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<EntityType<ProjectileNuggetEntity>> NUGGET_PROJECTILE = ENTITIES.register("nugget_projectile", () -> EntityType.Builder
            .<ProjectileNuggetEntity>of(ProjectileNuggetEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(20)
            .setCustomClientFactory((spawnEntity, world) -> new ProjectileNuggetEntity(world, spawnEntity.getEntity()))
            .sized(0.25F, 0.25F)
            .build("nugget_projectile"));
}
