package com.legobmw99.allomancy.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ExtrasSetup {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Allomancy.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);
    public static final RegistryObject<IronButtonBlock> IRON_BUTTON = BLOCKS.register("iron_button", IronButtonBlock::new);
    public static final RegistryObject<IronLeverBlock> IRON_LEVER = BLOCKS.register("iron_lever", IronLeverBlock::new);
    public static final RegistryObject<Item> IRON_BUTTON_ITEM = ITEMS.register("iron_button", () -> new BlockItem(IRON_BUTTON.get(), new Item.Properties().group(ItemGroup.REDSTONE)));
    public static final RegistryObject<Item> IRON_LEVER_ITEM = ITEMS.register("iron_lever", () -> new BlockItem(IRON_LEVER.get(), new Item.Properties().group(ItemGroup.REDSTONE)));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
