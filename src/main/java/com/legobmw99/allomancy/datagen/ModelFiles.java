package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;

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

    private static final ExtendedModelTemplate FLAT_HANDHELD_LARGE = ModelTemplates.FLAT_ITEM
            .extend()
            .transform(ItemDisplayContext.GROUND, builder -> builder.scale(1.2f).translation(0, 5, 0))
            .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                       builder -> builder.scale(1.2f).rotation(0, -90, 55).translation(0, 8, 0.5f))
            .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                       builder -> builder.scale(1.2f).rotation(0, 90, -55).translation(0, 8, 0.5f))
            .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                       builder -> builder.scale(1).rotation(0, -90, 25).translation(0.5f, 5, 0.5f))
            .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                       builder -> builder.scale(1).rotation(0, 90, -25).translation(0.5f, 5, 0.5f))
            .build();

    private static void createItemModels(ItemModelGenerators itemModels) {

        itemModels.generateTrimmableItem(CombatSetup.MISTCLOAK.get(), CombatSetup.WOOL,
                                         ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, false);

        itemModels.generateTrimmableItem(CombatSetup.ALUMINUM_HELMET.get(), CombatSetup.ALUMINUM,
                                         ItemModelGenerators.TRIM_PREFIX_HELMET, false);

        itemModels.generateFlatItem(CombatSetup.COIN_BAG.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(CombatSetup.HORSESHOE.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(CombatSetup.OBSIDIAN_DAGGER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        itemModels.generateFlatItem(CombatSetup.KOLOSS_BLADE.get(), FLAT_HANDHELD_LARGE);

        itemModels.generateFlatItem(ConsumeSetup.ALLOMANTIC_GRINDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ConsumeSetup.LERASIUM_NUGGET.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ExtrasSetup.BRONZE_EARRING.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ExtrasSetup.CHARGED_BRONZE_EARRING.get(), ModelTemplates.FLAT_ITEM);

        for (var ritem : WorldSetup.RAW_ORE_ITEMS) {
            if (ritem != null) {
                itemModels.generateFlatItem(ritem.get(), ModelTemplates.FLAT_ITEM);
            }
        }

        for (int i = 0; i < WorldSetup.METAL_ITEM_LEN; i++) {

            Item flake = WorldSetup.FLAKES.get(i).get();
            itemModels.generateFlatItem(flake, ModelTemplates.FLAT_ITEM);


            if (i <= Metal.BENDALLOY.getIndex()) {
                Item pattern_item = ExtrasSetup.PATTERN_ITEMS.get(i).get();
                itemModels.generateFlatItem(pattern_item, ModelTemplates.FLAT_ITEM);

                if (Metal.getMetal(i).isVanilla()) {
                    continue;
                }
            }

            Item nugget = WorldSetup.NUGGETS.get(i).get();
            itemModels.generateFlatItem(nugget, ModelTemplates.FLAT_ITEM);

            Item ingot = WorldSetup.INGOTS.get(i).get();
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
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_lever");

        createLever(blockModels, ExtrasSetup.IRON_LEVER.get(), TextureMapping.getBlockTexture(Blocks.IRON_BLOCK));
        createIronButtonBlocks(blockModels);

        blockModels.createNonTemplateModelBlock(WorldSetup.LIQUID_LERASIUM.get(), Blocks.WATER);
        Stream
                .of(WorldSetup.ORE_BLOCKS, WorldSetup.DEEPSLATE_ORE_BLOCKS, WorldSetup.RAW_ORE_BLOCKS,
                    WorldSetup.STORAGE_BLOCKS)
                .flatMap(Collection::stream)
                .forEach(rblock -> {
                    if (rblock != null) {
                        blockModels.createTrivialCube(rblock.get());

                    }
                });
    }

    private static void createIronButtonBlocks(BlockModelGenerators blockModels) {

        var block = ExtrasSetup.IRON_BUTTON.get();
        TextureMapping iron = TextureMapping.defaultTexture(Blocks.IRON_BLOCK);
        Identifier extended = ModelTemplates.BUTTON.create(block, iron, blockModels.modelOutput);
        Identifier sunken = ModelTemplates.BUTTON_PRESSED.create(block, iron, blockModels.modelOutput);
        Identifier inventory = ModelTemplates.BUTTON_INVENTORY.create(block, iron, blockModels.modelOutput);

        var sunken_variant = BlockModelGenerators.plainVariant(sunken);
        var extended_variant = BlockModelGenerators.plainVariant(extended);

        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_button");
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createButton(block, extended_variant, sunken_variant));
        blockModels.registerSimpleItemModel(block, inventory);

        var inverted = ExtrasSetup.INVERTED_IRON_BUTTON.get();
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:inverted_iron_button");
        blockModels.blockStateOutput.accept(
                BlockModelGenerators.createButton(inverted, sunken_variant, extended_variant));
        blockModels.registerSimpleItemModel(inverted, inventory);
    }

    private static void createLever(BlockModelGenerators blockModels, Block block, Identifier base) {
        // annoyingly, this isn't an existing model template or helper
        TextureSlot base_slot = TextureSlot.create("base");
        TextureSlot lever_slot = TextureSlot.create("lever");
        ModelTemplate lever_template = ModelTemplates.create("lever", TextureSlot.PARTICLE, base_slot, lever_slot);
        ModelTemplate lever_on_template =
                ModelTemplates.create("lever_on", "_on", TextureSlot.PARTICLE, base_slot, lever_slot);

        TextureMapping textures = new TextureMapping();
        textures.put(TextureSlot.PARTICLE, base);
        textures.put(base_slot, base);
        textures.put(lever_slot, TextureMapping.getBlockTexture(block));

        Identifier lever = lever_template.create(block, textures, blockModels.modelOutput);
        Identifier lever_on = lever_on_template.create(block, textures, blockModels.modelOutput);

        var lever_variant = BlockModelGenerators.plainVariant(lever);
        var lever_on_variant = BlockModelGenerators.plainVariant(lever_on);


        blockModels.blockStateOutput.accept(MultiVariantGenerator
                                                    .dispatch(block)
                                                    .with(BlockModelGenerators.createBooleanModelDispatch(
                                                            BlockStateProperties.POWERED, lever_variant,
                                                            lever_on_variant))
                                                    .with(PropertyDispatch
                                                                  .modify(BlockStateProperties.ATTACH_FACE,
                                                                          BlockStateProperties.HORIZONTAL_FACING)
                                                                  .select(AttachFace.CEILING, Direction.NORTH,
                                                                          BlockModelGenerators.X_ROT_180.then(
                                                                                  BlockModelGenerators.Y_ROT_180))
                                                                  .select(AttachFace.CEILING, Direction.EAST,
                                                                          BlockModelGenerators.X_ROT_180.then(
                                                                                  BlockModelGenerators.Y_ROT_270))
                                                                  .select(AttachFace.CEILING, Direction.SOUTH,
                                                                          BlockModelGenerators.X_ROT_180)
                                                                  .select(AttachFace.CEILING, Direction.WEST,
                                                                          BlockModelGenerators.X_ROT_180.then(
                                                                                  BlockModelGenerators.Y_ROT_90))
                                                                  .select(AttachFace.FLOOR, Direction.NORTH,
                                                                          BlockModelGenerators.NOP)
                                                                  .select(AttachFace.FLOOR, Direction.EAST,
                                                                          BlockModelGenerators.Y_ROT_90)
                                                                  .select(AttachFace.FLOOR, Direction.SOUTH,
                                                                          BlockModelGenerators.Y_ROT_180)
                                                                  .select(AttachFace.FLOOR, Direction.WEST,
                                                                          BlockModelGenerators.Y_ROT_270)
                                                                  .select(AttachFace.WALL, Direction.NORTH,
                                                                          BlockModelGenerators.X_ROT_90)
                                                                  .select(AttachFace.WALL, Direction.EAST,
                                                                          BlockModelGenerators.X_ROT_90.then(
                                                                                  BlockModelGenerators.Y_ROT_90))
                                                                  .select(AttachFace.WALL, Direction.SOUTH,
                                                                          BlockModelGenerators.X_ROT_90.then(
                                                                                  BlockModelGenerators.Y_ROT_180))
                                                                  .select(AttachFace.WALL, Direction.WEST,
                                                                          BlockModelGenerators.X_ROT_90.then(
                                                                                  BlockModelGenerators.Y_ROT_270))));


        blockModels.registerSimpleFlatItemModel(block);
    }

}
