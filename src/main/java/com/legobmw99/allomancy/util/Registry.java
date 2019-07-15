package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.block.IronButtonBlock;
import com.legobmw99.allomancy.block.IronLeverBlock;
import com.legobmw99.allomancy.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.entity.IronNuggetEntity;
import com.legobmw99.allomancy.item.*;
import com.legobmw99.allomancy.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.network.packets.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registry {

    // Item Holders
    @ObjectHolder("allomancy:allomantic_grinder")
    public static Item allomantic_grinder;
    @ObjectHolder("allomancy:tin_ingot")
    public static Item tin_ingot;
    @ObjectHolder("allomancy:lead_ingot")
    public static Item lead_ingot;
    @ObjectHolder("allomancy:copper_ingot")
    public static Item copper_ingot;
    @ObjectHolder("allomancy:zinc_ingot")
    public static Item zinc_ingot;
    @ObjectHolder("allomancy:bronze_ingot")
    public static Item bronze_ingot;
    @ObjectHolder("allomancy:brass_ingot")
    public static Item brass_ingot;
    @ObjectHolder("allomancy:coin_bag")
    public static Item coin_bag;
    @ObjectHolder("allomancy:mistcloak")
    public static MistcloakItem mistcloak;
    @ObjectHolder("allomancy:lerasium_nugget")
    public static LerasiumItem lerasium_nugget;
    @ObjectHolder("allomancy:vial")
    public static VialItem vial;
    public static Item[] flakes;


    // Block Holders
    @ObjectHolder("allomancy:tin_ore")
    public static Block tin_ore;
    @ObjectHolder("allomancy:lead_ore")
    public static Block lead_ore;
    @ObjectHolder("allomancy:copper_ore")
    public static Block copper_ore;
    @ObjectHolder("allomancy:zinc_ore")
    public static Block zinc_ore;
    @ObjectHolder("allomancy:iron_lever")
    public static IronLeverBlock iron_lever;
    @ObjectHolder("allomancy:iron_button")
    public static IronButtonBlock iron_button;

    //Recipe holder
    @ObjectHolder("allomancy:vial_filling")
    public static SpecialRecipeSerializer<VialItemRecipe> vial_recipe_serializer;

    //EntityType holders
    @ObjectHolder("allomancy:iron_nugget")
    public static EntityType<IronNuggetEntity> iron_nugget;
    @ObjectHolder("allomancy:gold_nugget")
    public static EntityType<GoldNuggetEntity> gold_nugget;

    @OnlyIn(Dist.CLIENT)
    public static KeyBinding burn;

    public static final String[] allomanctic_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze"};

    protected static final String[] flake_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze", "lead"};

    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(Allomancy.MODID, "networking"))
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .networkProtocolVersion(() -> "1.0.0")
            .simpleChannel();

    public static ItemGroup allomancy_group = new ItemGroup(Allomancy.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Registry.mistcloak);
        }
    };


    public static IArmorMaterial WoolArmor = new IArmorMaterial() {
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 50;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return slotIn == EquipmentSlotType.CHEST ? 4 : 0;
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(Items.GRAY_WOOL);
        }

        @Override
        public String getName() {
            return "allomancy:wool";
        }

        @Override
        public float getToughness() {
            return 0;
        }
    };


    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", GLFW.GLFW_KEY_F, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
    }

    public static void registerPackets() {
        int index = 0;
        NETWORK.registerMessage(index++, AllomancyCapabilityPacket.class, AllomancyCapabilityPacket::encode, AllomancyCapabilityPacket::decode, AllomancyCapabilityPacket::handle);
        NETWORK.registerMessage(index++, UpdateBurnPacket.class, UpdateBurnPacket::encode, UpdateBurnPacket::decode, UpdateBurnPacket::handle);
        NETWORK.registerMessage(index++, ChangeEmotionPacket.class, ChangeEmotionPacket::encode, ChangeEmotionPacket::decode, ChangeEmotionPacket::handle);
        NETWORK.registerMessage(index++, TryPushPullEntity.class, TryPushPullEntity::encode, TryPushPullEntity::decode, TryPushPullEntity::handle);
        NETWORK.registerMessage(index, TryPushPullBlock.class, TryPushPullBlock::encode, TryPushPullBlock::decode, TryPushPullBlock::handle);

    }


    @SubscribeEvent
    public static void onRegisterRecipes(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new VialItemRecipe.Serializer());
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        Item.Properties prop_generic = new Item.Properties().group(allomancy_group).maxStackSize(64);
        event.getRegistry().registerAll(
                new GrinderItem(),
                new CoinBagItem(),
                new MistcloakItem(),
                new LerasiumItem(),
                new VialItem(),
                // Register ingots and add them to the ore dictionary
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "bronze_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "brass_ingot"))

        );


        // Register flakes
        for (int i = 0; i < flake_metals.length; i++) {
            event.getRegistry().register(
                    new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, flake_metals[i] + "_flakes")));
        }


        //Register ore block items
        event.getRegistry().registerAll(
                new BlockItem(tin_ore, prop_generic).setRegistryName(tin_ore.getRegistryName()),
                new BlockItem(lead_ore, prop_generic).setRegistryName(lead_ore.getRegistryName()),
                new BlockItem(copper_ore, prop_generic).setRegistryName(copper_ore.getRegistryName()),
                new BlockItem(zinc_ore, prop_generic).setRegistryName(zinc_ore.getRegistryName()),
                new BlockItem(iron_lever, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(iron_lever.getRegistryName()),
                new BlockItem(iron_button, new Item.Properties().group(ItemGroup.REDSTONE)).setRegistryName(iron_button.getRegistryName())

        );
        flakes = getFlakeItems();
    }


    private static Item[] getFlakeItems() {
        Item[] flakes = new Item[9];
        for (int i = 0; i < flakes.length; i++) {
            flakes[i] = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Allomancy.MODID, flake_metals[i] + "_flakes"));
        }
        return flakes;
    }

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        Block.Properties prop = Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2);
        event.getRegistry().registerAll(
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ore")),
                new Block(prop).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ore")),
                new IronLeverBlock(),
                new IronButtonBlock()
        );

    }

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders() {
        //Use renderSnowball for nugget projectiles
        RenderingRegistry.registerEntityRenderingHandler(GoldNuggetEntity.class, manager -> new SpriteRenderer<GoldNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IronNuggetEntity.class, manager -> new SpriteRenderer<IronNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
    }


    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(EntityType.Builder.<IronNuggetEntity>create(IronNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
                .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new IronNuggetEntity(iron_nugget, world)).size(0.25F, 0.25F).build("iron_nugget")
                .setRegistryName(Allomancy.MODID, "iron_nugget"));
        event.getRegistry().register(EntityType.Builder.<GoldNuggetEntity>create(GoldNuggetEntity::new, EntityClassification.MISC).setShouldReceiveVelocityUpdates(true)
                .setUpdateInterval(20).setCustomClientFactory((spawnEntity, world) -> new GoldNuggetEntity(gold_nugget, world)).size(0.25F, 0.25F).build("gold_nugget")
                .setRegistryName(Allomancy.MODID, "gold_nugget"));

    }

}
