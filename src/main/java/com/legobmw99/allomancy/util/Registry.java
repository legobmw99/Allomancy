package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.block.IronLeverBlock;
import com.legobmw99.allomancy.items.CoinBagItem;
import com.legobmw99.allomancy.items.LerasiumItem;
import com.legobmw99.allomancy.items.MistcloakItem;
import com.legobmw99.allomancy.items.VialItem;
import com.legobmw99.allomancy.network.packets.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

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


    public static final String[] flake_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze",
            "lead"};

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

    public static KeyBinding burn;

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
            return "wool";
        }

        @Override
        public float getToughness() {
            return 0;
        }
    };


    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", 70, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
    }

    public static void registerPackets() {
        int index = 0;
        NETWORK.registerMessage(index++, AllomancyPowerPacket.class, AllomancyPowerPacket::encode, AllomancyPowerPacket::decode, AllomancyPowerPacket.Handler::handle);
        NETWORK.registerMessage(index++, UpdateBurnPacket.class, UpdateBurnPacket::encode, UpdateBurnPacket::decode, UpdateBurnPacket.Handler::handle);
        NETWORK.registerMessage(index++, AllomancyCapabilityPacket.class, AllomancyCapabilityPacket::encode, AllomancyCapabilityPacket::decode, AllomancyCapabilityPacket.Handler::handle);
        NETWORK.registerMessage(index++, ChangeEmotionPacket.class, ChangeEmotionPacket::encode, ChangeEmotionPacket::decode, ChangeEmotionPacket.Handler::handle);
        NETWORK.registerMessage(index++, GetCapabilitiesPacket.class, GetCapabilitiesPacket::encode, GetCapabilitiesPacket::decode, GetCapabilitiesPacket.Handler::handle);
        NETWORK.registerMessage(index++, TryPushPullEntity.class, TryPushPullEntity::encode, TryPushPullEntity::decode, TryPushPullEntity.Handler::handle);
        NETWORK.registerMessage(index, TryPushPullBlock.class, TryPushPullBlock::encode, TryPushPullBlock::decode, TryPushPullBlock.Handler::handle);

    }

    /*@OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders() {
        //Use renderSnowball for nugget projectiles
        RenderingRegistry.registerEntityRenderingHandler(EntityGoldNugget.class, EntityRenderFactories.GOLD_FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityIronNugget.class, EntityRenderFactories.IRON_FACTORY);
    }


    //only does furnace recipes, rest are handled in JSON
    public static void setupRecipes(Register<IRecipe> event) {
        event.getRegistry().register(new RecipeItemVial());
    }*/

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        Allomancy.LOGGER.debug("Registering items");
        Item.Properties prop_generic = new Item.Properties().group(allomancy_group);
        Item.Properties prop_single = new Item.Properties().group(allomancy_group).maxStackSize(1);

        event.getRegistry().registerAll(
                new Item(new Item.Properties().group(allomancy_group).maxStackSize(1).containerItem(allomantic_grinder)).setRegistryName(new ResourceLocation(Allomancy.MODID, "allomantic_grinder")),
                new CoinBagItem(prop_single),
                new MistcloakItem(WoolArmor, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)),
                new LerasiumItem(),
                // Register VialItem and its subtypes
                new VialItem(),
                // Register ingots and add them to the ore dictionary
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ingot")),
                new Item(prop_generic).setRegistryName(new ResourceLocation(Allomancy.MODID, "bronze_ingot"))
        );


        // Register flakes
        for (int i = 0; i < flake_metals.length; i++) {
            event.getRegistry().register(
                    new Item(new Item.Properties().group(allomancy_group).maxDamage(0)).setRegistryName(new ResourceLocation(Allomancy.MODID, flake_metals[i] + "_flakes")));
        }

        //Register ore block items
        event.getRegistry().registerAll(
                new BlockItem(tin_ore, prop_generic).setRegistryName(tin_ore.getRegistryName()),
                new BlockItem(lead_ore, prop_generic).setRegistryName(lead_ore.getRegistryName()),
                new BlockItem(copper_ore, prop_generic).setRegistryName(copper_ore.getRegistryName()),
                new BlockItem(zinc_ore, prop_generic).setRegistryName(zinc_ore.getRegistryName()),
                new BlockItem(iron_lever, prop_generic).setRegistryName(iron_lever.getRegistryName())
        );
    }

    @SubscribeEvent
    public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
        Allomancy.LOGGER.info("Registering Blocks");
        event.getRegistry().registerAll(
                new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F)).setRegistryName(new ResourceLocation(Allomancy.MODID, "tin_ore")),
                new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F)).setRegistryName(new ResourceLocation(Allomancy.MODID, "lead_ore")),
                new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F)).setRegistryName(new ResourceLocation(Allomancy.MODID, "copper_ore")),
                new Block(Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F)).setRegistryName(new ResourceLocation(Allomancy.MODID, "zinc_ore")),
                new IronLeverBlock()
        );

    }

}
