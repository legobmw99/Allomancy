package com.legobmw99.allomancy.setup;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.block.IronButtonBlock;
import com.legobmw99.allomancy.block.IronLeverBlock;
import com.legobmw99.allomancy.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.entity.IronNuggetEntity;
import com.legobmw99.allomancy.item.*;
import com.legobmw99.allomancy.item.recipe.VialItemRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Registry {
    public static final String[] allomanctic_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze"};
    protected static final String[] flake_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze", "lead"};


    /*
        REGISTRIES
     */
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Allomancy.MODID);
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, Allomancy.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, Allomancy.MODID);

    /*
        BLOCK REGISTRATION
     */
    private static final Block.Properties bprop_generic = Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2);
    public static final RegistryObject<IronButtonBlock> IRON_BUTTON = BLOCKS.register("iron_button", IronButtonBlock::new);
    public static final RegistryObject<IronLeverBlock> IRON_LEVER = BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore", () -> new Block(bprop_generic));
    public static final RegistryObject<Block> LEAD_ORE = BLOCKS.register("lead_ore", () -> new Block(bprop_generic));
    public static final RegistryObject<Block> COPPER_ORE = BLOCKS.register("copper_ore", () -> new Block(bprop_generic));
    public static final RegistryObject<Block> ZINC_ORE = BLOCKS.register("zinc_ore", () -> new Block(bprop_generic));

    /*
        ITEM REGISTRATION
     */
    public static ItemGroup allomancy_group = new ItemGroup(Allomancy.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registry.MISTCLOAK.get());
        }
    };
    private static final Item.Properties prop_generic = new Item.Properties().group(allomancy_group).maxStackSize(64);
    public static final RegistryObject<GrinderItem> ALLOMANTIC_GRINDER = ITEMS.register("allomantic_grinder", GrinderItem::new);
    public static final RegistryObject<MistcloakItem> MISTCLOAK = ITEMS.register("mistcloak", MistcloakItem::new);
    public static final RegistryObject<CoinBagItem> COIN_BAG = ITEMS.register("coin_bag", CoinBagItem::new);
    public static final RegistryObject<LerasiumItem> LERASIUM_NUGGET = ITEMS.register("lerasium_nugget", LerasiumItem::new);
    public static final RegistryObject<VialItem> VIAL = ITEMS.register("vial", VialItem::new);
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> COPPER_INGOT = ITEMS.register("copper_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> ZINC_INGOT = ITEMS.register("zinc_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> BRASS_INGOT = ITEMS.register("brass_ingot", () -> new Item(prop_generic));
    public static final RegistryObject<Item> TIN_ORE_ITEM = ITEMS.register("tin_ore", () -> new BlockItem(TIN_ORE.get(), prop_generic));
    public static final RegistryObject<Item> LEAD_ORE_ITEM = ITEMS.register("lead_ore", () -> new BlockItem(LEAD_ORE.get(), prop_generic));
    public static final RegistryObject<Item> COPPER_ORE_ITEM = ITEMS.register("copper_ore", () -> new BlockItem(COPPER_ORE.get(), prop_generic));
    public static final RegistryObject<Item> ZINC_ORE_ITEM = ITEMS.register("zinc_ore", () -> new BlockItem(ZINC_ORE.get(), prop_generic));
    public static final RegistryObject<Item> IRON_BUTTON_ITEM = ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties().group(ItemGroup.REDSTONE)));
    public static final RegistryObject<Item> IRON_LEVER_ITEM = ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties().group(ItemGroup.REDSTONE)));
    public static final List<RegistryObject<Item>> FLAKES = new ArrayList<>();

    static {
        for (String flake_metal : flake_metals) {
            String name = flake_metal + "_flakes";
            FLAKES.add(ITEMS.register(name, () -> new Item(prop_generic)));
        }
    }

    /*
        RECIPE REGISTRATION
     */
    public static final RegistryObject<SpecialRecipeSerializer<VialItemRecipe>> VIAL_RECIPE_SERIALIZER = RECIPES.register("vial_filling", VialItemRecipe.Serializer::new);

    /*
        ENTITY REGISTRATION
    */
    public static final RegistryObject<EntityType<?>> IRON_NUGGET = ENTITIES.register("iron_nugget", () ->
            EntityType.Builder.<IronNuggetEntity>create(IronNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
                    .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new IronNuggetEntity(world)).size(0.25F, 0.25F).build("iron_nugget"));
    public static final RegistryObject<EntityType<?>> GOLD_NUGGET = ENTITIES.register("gold_nugget", () ->
            EntityType.Builder.<GoldNuggetEntity>create(GoldNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
                    .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new GoldNuggetEntity(world)).size(0.25F, 0.25F).build("gold_nugget"));


    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
