package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;
import java.util.stream.Stream;

class ModelFiles extends ModelProvider {

    ModelFiles(PackOutput gen) {
        super(gen, Allomancy.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        createBlockModels(blockModels);
        createItemModels(itemModels);
    }

    private static void createItemModels(ItemModelGenerators itemModels) {


        itemModels.generateFlatItem(CombatSetup.MISTCLOAK.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateTrimmableItem(CombatSetup.ALUMINUM_HELMET.get(), CombatSetup.ALUMINUM, "helmet", false);

        itemModels.generateFlatItem(CombatSetup.COIN_BAG.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(CombatSetup.OBSIDIAN_DAGGER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(CombatSetup.KOLOSS_BLADE.get(),
                                    ModelTemplates.createItem("allomancy:handheld_large", TextureSlot.LAYER0));

        itemModels.generateFlatItem(ConsumeSetup.ALLOMANTIC_GRINDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ConsumeSetup.LERASIUM_NUGGET.get(), ModelTemplates.FLAT_ITEM);


        for (var ritem : MaterialsSetup.RAW_ORE_ITEMS) {
            itemModels.generateFlatItem(ritem.get(), ModelTemplates.FLAT_ITEM);
        }

        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {

            Item flake = MaterialsSetup.FLAKES.get(i).get();
            itemModels.generateFlatItem(flake, ModelTemplates.FLAT_ITEM);


            if (i <= Metal.BENDALLOY.getIndex()) {
                Item pattern_item = ExtrasSetup.PATTERN_ITEMS.get(i).get();
                itemModels.generateFlatItem(pattern_item, ModelTemplates.FLAT_ITEM);

                if (Metal.getMetal(i).isVanilla()) {
                    continue;
                }
            }

            Item nugget = MaterialsSetup.NUGGETS.get(i).get();
            itemModels.generateFlatItem(nugget, ModelTemplates.FLAT_ITEM);

            Item ingot = MaterialsSetup.INGOTS.get(i).get();
            itemModels.generateFlatItem(ingot, ModelTemplates.FLAT_ITEM);

        }

        Allomancy.LOGGER.debug("Creating Item Model for allomancy:vial (filled)");

        Item vial = ConsumeSetup.VIAL.get();
        ItemModel.Unbaked base_vial =
                ItemModelUtils.plainModel(itemModels.createFlatItemModel(vial, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked full_vial =
                ItemModelUtils.plainModel(itemModels.createFlatItemModel(vial, "_filled", ModelTemplates.FLAT_ITEM));

        itemModels.generateBooleanDispatch(vial, ItemModelUtils.hasComponent(ConsumeSetup.FLAKE_STORAGE.get()),
                                           full_vial, base_vial);

    }

    private static void createBlockModels(BlockModelGenerators blockModels) {

        createIronLeverBlock(blockModels);
        createIronButtonBlocks(blockModels);

        Stream
                .of(MaterialsSetup.ORE_BLOCKS, MaterialsSetup.DEEPSLATE_ORE_BLOCKS, MaterialsSetup.RAW_ORE_BLOCKS,
                    MaterialsSetup.STORAGE_BLOCKS)
                .flatMap(Collection::stream)
                .forEach(rblock -> {
                    if (rblock != null) {
                        blockModels.createTrivialCube(rblock.get());

                    }
                });
    }

    private static void createIronButtonBlocks(BlockModelGenerators blockModels) {
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_button");

        var block = ExtrasSetup.IRON_BUTTON.get();
        TextureMapping iron = TextureMapping.defaultTexture(Blocks.IRON_BLOCK);
        ResourceLocation extended = ModelTemplates.BUTTON.create(block, iron, blockModels.modelOutput);
        ResourceLocation sunken = ModelTemplates.BUTTON_PRESSED.create(block, iron, blockModels.modelOutput);
        ResourceLocation inventory = ModelTemplates.BUTTON_INVENTORY.create(block, iron, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createButton(block, extended, sunken));
        blockModels.registerSimpleItemModel(block, inventory);

        var inverted = ExtrasSetup.INVERTED_IRON_BUTTON.get();
        blockModels.blockStateOutput.accept(BlockModelGenerators.createButton(inverted, sunken, extended));
        blockModels.registerSimpleItemModel(inverted, inventory);
    }

    private static void createIronLeverBlock(BlockModelGenerators blockModels) {
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_lever");

        // annoyingly, this isn't an existing model template or helper
        TextureSlot base_slot = TextureSlot.create("base");
        TextureSlot lever_slot = TextureSlot.create("lever");
        ModelTemplate lever = ModelTemplates.create("lever", TextureSlot.PARTICLE, base_slot, lever_slot);
        ModelTemplate lever_off =
                ModelTemplates.create("lever_on", "_off", TextureSlot.PARTICLE, base_slot, lever_slot);

        TextureMapping iron_lever_textures = new TextureMapping();
        iron_lever_textures.put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.IRON_BLOCK));
        iron_lever_textures.put(base_slot, TextureMapping.getBlockTexture(Blocks.IRON_BLOCK));
        iron_lever_textures.put(lever_slot,
                                ResourceLocation.fromNamespaceAndPath(Allomancy.MODID, "block/iron_lever"));

        ResourceLocation iron_lever =
                lever.create(ExtrasSetup.IRON_LEVER.get(), iron_lever_textures, blockModels.modelOutput);
        ResourceLocation iron_lever_off =
                lever_off.create(ExtrasSetup.IRON_LEVER.get(), iron_lever_textures, blockModels.modelOutput);


        blockModels.blockStateOutput.accept(MultiVariantGenerator
                                                    .multiVariant(ExtrasSetup.IRON_LEVER.get())
                                                    .with(BlockModelGenerators.createBooleanModelDispatch(
                                                            BlockStateProperties.POWERED, iron_lever, iron_lever_off))
                                                    .with(PropertyDispatch
                                                                  .properties(BlockStateProperties.ATTACH_FACE,
                                                                              BlockStateProperties.HORIZONTAL_FACING)
                                                                  .select(AttachFace.CEILING, Direction.NORTH, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R180)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R180))
                                                                  .select(AttachFace.CEILING, Direction.EAST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R180)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R270))
                                                                  .select(AttachFace.CEILING, Direction.SOUTH, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R180))
                                                                  .select(AttachFace.CEILING, Direction.WEST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R180)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R90))
                                                                  .select(AttachFace.FLOOR, Direction.NORTH,
                                                                          Variant.variant())
                                                                  .select(AttachFace.FLOOR, Direction.EAST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R90))
                                                                  .select(AttachFace.FLOOR, Direction.SOUTH, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R180))
                                                                  .select(AttachFace.FLOOR, Direction.WEST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R270))
                                                                  .select(AttachFace.WALL, Direction.NORTH, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R90))
                                                                  .select(AttachFace.WALL, Direction.EAST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R90)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R90))
                                                                  .select(AttachFace.WALL, Direction.SOUTH, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R90)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R180))
                                                                  .select(AttachFace.WALL, Direction.WEST, Variant
                                                                          .variant()
                                                                          .with(VariantProperties.X_ROT,
                                                                                VariantProperties.Rotation.R90)
                                                                          .with(VariantProperties.Y_ROT,
                                                                                VariantProperties.Rotation.R270))));


        blockModels.registerSimpleFlatItemModel(ExtrasSetup.IRON_LEVER.get());


    }

}
