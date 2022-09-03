package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BannerPattern;

public class BannerTag extends TagsProvider<BannerPattern> {

    public BannerTag(DataGenerator pGenerator, net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.BANNER_PATTERN, Allomancy.MODID, existingFileHelper);
    }

    protected void addTags() {

        for (Metal mt : Metal.values()) {
            Allomancy.LOGGER.debug("Creating banner tag for " + mt.getName());
            this.tag(ExtrasSetup.PATTERN_KEYS.get(mt.getIndex())).add(ExtrasSetup.PATTERNS.get(mt.getIndex()).get());
        }

    }

    @Override
    public String getName() {
        return "Allomancy Banner Tags";
    }
}
