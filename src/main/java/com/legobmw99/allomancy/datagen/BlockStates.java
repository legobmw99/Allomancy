package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.*;

import java.util.function.BiConsumer;

public class BlockStates extends BlockStateProvider {

    public BlockStates(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        singleTextureBlock(MaterialsSetup.ALUMINUM_ORE.get(), "aluminum_ore", "block/aluminum_ore");
        singleTextureBlock(MaterialsSetup.CADMIUM_ORE.get(), "cadmium_ore", "block/cadmium_ore");
        singleTextureBlock(MaterialsSetup.CHROMIUM_ORE.get(), "chromium_ore", "block/chromium_ore");
        singleTextureBlock(MaterialsSetup.COPPER_ORE.get(), "copper_ore", "block/copper_ore");
        singleTextureBlock(MaterialsSetup.LEAD_ORE.get(), "lead_ore", "block/lead_ore");
        singleTextureBlock(MaterialsSetup.SILVER_ORE.get(), "silver_ore", "block/silver_ore");
        singleTextureBlock(MaterialsSetup.TIN_ORE.get(), "tin_ore", "block/tin_ore");
        singleTextureBlock(MaterialsSetup.ZINC_ORE.get(), "zinc_ore", "block/zinc_ore");

        createIronLeverBlock();
        createIronButtonBlock();

    }


    private void singleTextureBlock(Block block, String model, String texture) {
        Allomancy.LOGGER.debug("Creating Block Data for " + block.getRegistryName());
        simpleBlock(block);
    }



    private void createIronButtonBlock() {
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_button");
        ModelFile inventory = models().withExistingParent("allomancy:iron_button_inventory", mcLoc("block/button_inventory")).texture("texture", mcLoc("block/iron_block"));
        ModelFile button = models().withExistingParent("allomancy:iron_button", mcLoc("block/button")).texture("texture", mcLoc("block/iron_block"));
        ModelFile pressed = models().withExistingParent("allomancy:iron_button_pressed", mcLoc("block/button_pressed")).texture("texture", mcLoc("block/iron_block"));

        VariantBlockStateBuilder builder = getVariantBuilder(ExtrasSetup.IRON_BUTTON.get());
        for (Boolean powered : IronButtonBlock.POWERED.getAllowedValues()) {
            ModelFile model = powered ? pressed : button;
            for (AttachFace face : IronButtonBlock.FACE.getAllowedValues()) {
                int xangle = (face == AttachFace.CEILING) ? 180 : (face == AttachFace.WALL) ? 90 : 0;
                boolean uvlock = face == AttachFace.WALL;
                for (Direction dir : IronButtonBlock.HORIZONTAL_FACING.getAllowedValues()) {
                    int yangle = (int) dir.getHorizontalAngle();
                    yangle = face != AttachFace.CEILING ? (yangle + 180) % 360 : yangle;
                    builder.partialState()
                            .with(IronButtonBlock.POWERED, powered)
                            .with(IronButtonBlock.FACE, face)
                            .with(IronButtonBlock.HORIZONTAL_FACING, dir)
                            .modelForState()
                                .modelFile(model)
                                .uvLock(uvlock)
                                .rotationY(yangle)
                                .rotationX(xangle)
                            .addModel();
                }
            }
        }
    }

    private void createIronLeverBlock() {
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_lever");

        BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder> base_generator = (dir, facebuilder) -> {
            switch (dir) {
                case UP:
                    facebuilder.uvs(5, 4, 11, 12).texture("#base").end();
                    break;
                case DOWN:
                    facebuilder.uvs(5, 4, 11, 12).texture("#base").cullface(Direction.DOWN).end();
                    break;
                case NORTH:
                case SOUTH:
                    facebuilder.uvs(5, 0, 11, 3).texture("#base").end();
                    break;
                case WEST:
                case EAST:
                    facebuilder.uvs(4, 0, 12, 3).texture("#base").end();
                    break;
            }
        };

        BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder> lever_generator = (dir, facebuilder) -> {
            switch (dir) {
                case UP:
                    facebuilder.uvs(7, 6, 9, 8).texture("#lever").end();
                    break;
                case NORTH:
                case SOUTH:
                case WEST:
                case EAST:
                    facebuilder.uvs(7, 6, 9, 16).texture("#lever").end();
                    break;
                case DOWN:
                    facebuilder.end();
                    break;
            }
        };

        ModelFile lever_on = models().getBuilder("allomancy:iron_lever").ao(false)
                .texture("particle", mcLoc("block/iron_block"))
                .texture("base", mcLoc("block/iron_block"))
                .texture("lever", modLoc("block/iron_lever"))
                .element()
                    .from(5, 0, 4).to(11, 3, 12)
                    .allFaces(base_generator)
                .end()
                .element()
                    .from(7, 1, 7).to(9, 11, 9)
                    .rotation()
                        .origin(8, 1, 8).axis(Direction.Axis.X).angle(-45F).end()
                    .allFaces(lever_generator)
                .end();

        ModelFile lever_off = models().getBuilder("allomancy:iron_lever_off").ao(false)
                .texture("particle", mcLoc("block/iron_block"))
                .texture("base", mcLoc("block/iron_block"))
                .texture("lever", modLoc("block/iron_lever"))
                .element()
                    .from(5, 0, 4).to(11, 3, 12)
                    .allFaces(base_generator)
                .end()
                .element()
                    .from(7, 1, 7).to(9, 11, 9)
                    .rotation()
                        .origin(8, 1, 8).axis(Direction.Axis.X).angle(45).end()
                    .allFaces(lever_generator)
                .end();

        VariantBlockStateBuilder builder = getVariantBuilder(ExtrasSetup.IRON_LEVER.get());
        for (Boolean powered : IronLeverBlock.POWERED.getAllowedValues()) {
            ModelFile model = powered ? lever_on : lever_off;
            for (AttachFace face : IronLeverBlock.FACE.getAllowedValues()) {
                int xangle = (face == AttachFace.CEILING) ? 180 : (face == AttachFace.WALL) ? 90 : 0;

                for (Direction dir : IronLeverBlock.HORIZONTAL_FACING.getAllowedValues()) {
                    int yangle = (int) dir.getHorizontalAngle();
                    yangle = face != AttachFace.CEILING ? (yangle + 180) % 360 : yangle;

                    builder.partialState()
                            .with(IronLeverBlock.POWERED, powered)
                            .with(IronLeverBlock.FACE, face)
                            .with(IronLeverBlock.HORIZONTAL_FACING, dir)
                            .modelForState()
                                .modelFile(model)
                                .rotationY(yangle)
                                .rotationX(xangle)
                            .addModel();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Allomancy Blockstates";
    }
}
