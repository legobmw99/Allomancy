package com.legobmw99.allomancy.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.combat.item.CoinBagItem;
import com.legobmw99.allomancy.modules.combat.item.KolossBladeItem;
import com.legobmw99.allomancy.modules.combat.item.MistcloakItem;
import com.legobmw99.allomancy.modules.combat.item.ObsidianDaggerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CombatSetup {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Allomancy.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);
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
    public static final DeferredItem<MistcloakItem> MISTCLOAK = ITEMS.register("mistcloak", MistcloakItem::new);

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        ITEMS.register(bus);
    }

    public static final Supplier<EntityType<ProjectileNuggetEntity>> NUGGET_PROJECTILE = ENTITIES.register("nugget_projectile", () -> EntityType.Builder
            .<ProjectileNuggetEntity>of(ProjectileNuggetEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(20)
//            .setCustomClientFactory((spawnEntity, world) -> new ProjectileNuggetEntity(world, spawnEntity.getEntity()))
            .sized(0.25F, 0.25F)
            .build("nugget_projectile"));
}
