package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.item.ItemAxe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class ItemGlassDagger extends ItemAxe {
    public ItemGlassDagger() {
        super(EnumHelper.addToolMaterial("GLASS", 1, 200, 1, 1, 10), 1.5f, 0);
        this.setCreativeTab(Registry.tabsAllomancy);
        this.setUnlocalizedName("glassdagger");
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "glassdagger"));
    }
}