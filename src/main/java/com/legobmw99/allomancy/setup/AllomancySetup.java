package com.legobmw99.allomancy.setup;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.network.Network;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class AllomancySetup {
    public static final String[] allomanctic_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze"};
    public static final String[] flake_metals = {"iron", "steel", "tin", "pewter", "zinc", "brass", "copper", "bronze", "lead"};


    public static ItemGroup allomancy_group = new ItemGroup(Allomancy.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(CombatSetup.MISTCLOAK.get());
        }
    };

    public static Item.Properties createStandardItemProperties() {
        return new Item.Properties().group(allomancy_group).maxStackSize(64);
    }

    public static Block.Properties createStandardBlockProperties() {
        return Block.Properties.create(Material.ROCK).hardnessAndResistance(2.0F).harvestTool(ToolType.PICKAXE).harvestLevel(2);
    }

    public static void clientInit(final FMLClientSetupEvent e) {
        CombatSetup.clientInit(e);
        PowersSetup.clientInit(e);
    }

    public static void serverInit(final FMLServerStartingEvent e) {
        PowersSetup.serverInit(e);
    }

    public static void init(final FMLCommonSetupEvent e) {
        PowersSetup.init(e);
        MaterialsSetup.init(e);
        Network.registerPackets();
    }
}
