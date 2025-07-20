package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.extras.command.AllomancyPowerType;
import com.legobmw99.allomancy.modules.extras.item.BronzeEarringItem;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExtrasSetup {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);
    private static final DeferredRegister<BannerPattern> BP =
            DeferredRegister.create(Registries.BANNER_PATTERN, Allomancy.MODID);


    public static final RegistryObject<BronzeEarringItem> BRONZE_EARRING =
            ITEMS.register("bronze_earring", BronzeEarringItem::new);


    public static final RegistryObject<BronzeEarringItem> CHARGED_BRONZE_EARRING =
            ITEMS.register("charged_bronze_earring", BronzeEarringItem::new);

    public static final RegistryObject<IronButtonBlock> IRON_BUTTON =
            BLOCKS.register("iron_button", () -> new IronButtonBlock(true));

    public static final RegistryObject<Item> IRON_BUTTON_ITEM =
            ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties()));
    public static final RegistryObject<IronButtonBlock> INVERTED_IRON_BUTTON =
            BLOCKS.register("inverted_iron_button", () -> new IronButtonBlock(false));

    public static final RegistryObject<Item> INVERTED_IRON_BUTTON_ITEM = ITEMS.register("inverted_iron_button",
                                                                                        () -> new BlockItem(
                                                                                                INVERTED_IRON_BUTTON.get(),
                                                                                                new Item.Properties()));
    public static final RegistryObject<IronLeverBlock> IRON_LEVER =
            BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final RegistryObject<Item> IRON_LEVER_ITEM =
            ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties()));

    public static final List<RegistryObject<BannerPattern>> PATTERNS = new ArrayList<>();
    public static final List<TagKey<BannerPattern>> PATTERN_KEYS = new ArrayList<>();

    public static final List<RegistryObject<BannerPatternItem>> PATTERN_ITEMS = new ArrayList<>();
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
            DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Allomancy.MODID);
    private static final Supplier<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS =
            COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                            () -> ArgumentTypeInfos.registerByClass(AllomancyPowerType.class,
                                                                                    SingletonArgumentInfo.contextFree(
                                                                                            AllomancyPowerType::allomancyPowerType)));


    static {
        for (Metal mt : Metal.values()) {

            String name = mt.getName();
            var pattern = BP.register(mt.getName(), () -> new BannerPattern("ALLOMANCY" + mt.name()));
            PATTERNS.add(pattern);
            var pattern_key = TagKey.create(Registries.BANNER_PATTERN, new ResourceLocation(Allomancy.MODID, name));
            PATTERN_KEYS.add(pattern_key);

            var pattern_item = ITEMS.register(name + "_pattern", () -> new BannerPatternItem(pattern_key,
                                                                                             new Item.Properties().stacksTo(
                                                                                                     1)));
            PATTERN_ITEMS.add(pattern_item);
        }
    }

    public static void register() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BP.register(bus);

        COMMAND_ARGUMENT_TYPES.register(bus);
    }

    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }

}