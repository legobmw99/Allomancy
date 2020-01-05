package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;

public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        singleTextureBlock(MaterialsSetup.ZINC_ORE.get(), "zinc_ore", "block/zinc_ore");
        singleTextureBlock(MaterialsSetup.TIN_ORE.get(), "tin_ore", "block/tin_ore");
        singleTextureBlock(MaterialsSetup.COPPER_ORE.get(), "copper_ore", "block/copper_ore");
        singleTextureBlock(MaterialsSetup.LEAD_ORE.get(), "lead_ore", "block/lead_ore");

    }

    private void singleTextureBlock(Block block, String model, String texture) {
        Allomancy.LOGGER.debug("Creating Block Data for " + block.getRegistryName());
        ModelFile mf = cubeAll(model, modLoc(texture));
        simpleBlock(block, mf);
    }

    @Override
    public String getName() {
        return "Allomancy Blockstates";
    }
}
