package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.extras.block.IronButtonBlock;
import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;

public class BlockStates extends BlockStateProvider {

    public BlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Allomancy.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var rblock : MaterialsSetup.ORE_BLOCKS) {
            Block block = rblock.get();
            String path = rblock.getId().getPath();
            singleTextureBlock(block, path, "block/" + path);
        }

        for (var rblock : MaterialsSetup.DEEPSLATE_ORE_BLOCKS) {
            Block block = rblock.get();
            String path = rblock.getId().getPath();
            singleTextureBlock(block, path, "block/" + path);
        }

        for (RegistryObject<Block> rblock : MaterialsSetup.RAW_ORE_BLOCKS) {
            Block block = rblock.get();
            String path = rblock.getId().getPath();
            singleTextureBlock(block, path, "block/" + path);
        }

        for (RegistryObject<Block> rblock : MaterialsSetup.STORAGE_BLOCKS) {
            if (rblock != null) {
                Block block = rblock.get();
                String path = rblock.getId().getPath();
                singleTextureBlock(block, path, "block/" + path);
            }
        }

        createIronLeverBlock();
        createIronButtonBlock();

    }


    private void singleTextureBlock(Block block, String model, String texture) {
        Allomancy.LOGGER.debug("Creating Block Data for " + ForgeRegistries.BLOCKS.getKey(block));
        simpleBlock(block);
    }


    private void createIronButtonBlock() {
        Allomancy.LOGGER.debug("Creating Block Data for allomancy:iron_button");
        ModelFile inventory = models().withExistingParent("allomancy:iron_button_inventory", mcLoc("block/button_inventory")).texture("texture", mcLoc("block/iron_block"));
        ModelFile button = models().withExistingParent("allomancy:iron_button", mcLoc("block/button")).texture("texture", mcLoc("block/iron_block"));
        ModelFile pressed = models().withExistingParent("allomancy:iron_button_pressed", mcLoc("block/button_pressed")).texture("texture", mcLoc("block/iron_block"));

        VariantBlockStateBuilder builder = getVariantBuilder(ExtrasSetup.IRON_BUTTON.get());
        for (Boolean powered : IronButtonBlock.POWERED.getPossibleValues()) {
            ModelFile model = powered ? pressed : button;
            for (AttachFace face : IronButtonBlock.FACE.getPossibleValues()) {
                int xangle = (face == AttachFace.CEILING) ? 180 : (face == AttachFace.WALL) ? 90 : 0;
                boolean uvlock = face == AttachFace.WALL;
                for (Direction dir : IronButtonBlock.FACING.getPossibleValues()) {
                    int yangle = (int) dir.toYRot();
                    yangle = face != AttachFace.CEILING ? (yangle + 180) % 360 : yangle;
                    builder
                            .partialState()
                            .with(IronButtonBlock.POWERED, powered)
                            .with(IronButtonBlock.FACE, face)
                            .with(IronButtonBlock.FACING, dir)
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
                case UP -> facebuilder.uvs(5, 4, 11, 12).texture("#base").end();
                case DOWN -> facebuilder.uvs(5, 4, 11, 12).texture("#base").cullface(Direction.DOWN).end();
                case NORTH, SOUTH -> facebuilder.uvs(5, 0, 11, 3).texture("#base").end();
                case WEST, EAST -> facebuilder.uvs(4, 0, 12, 3).texture("#base").end();
            }
        };

        BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder> lever_generator = (dir, facebuilder) -> {
            switch (dir) {
                case UP -> facebuilder.uvs(7, 6, 9, 8).texture("#lever").end();
                case NORTH, SOUTH, WEST, EAST -> facebuilder.uvs(7, 6, 9, 16).texture("#lever").end();
                case DOWN -> facebuilder.end();
            }
        };

        ModelFile lever_on = models()
                .getBuilder("allomancy:iron_lever")
                .ao(false)
                .texture("particle", mcLoc("block/iron_block"))
                .texture("base", mcLoc("block/iron_block"))
                .texture("lever", modLoc("block/iron_lever"))
                .element()
                .from(5, 0, 4)
                .to(11, 3, 12)
                .allFaces(base_generator)
                .end()
                .element()
                .from(7, 1, 7)
                .to(9, 11, 9)
                .rotation()
                .origin(8, 1, 8)
                .axis(Direction.Axis.X)
                .angle(-45F)
                .end()
                .allFaces(lever_generator)
                .end();

        ModelFile lever_off = models()
                .getBuilder("allomancy:iron_lever_off")
                .ao(false)
                .texture("particle", mcLoc("block/iron_block"))
                .texture("base", mcLoc("block/iron_block"))
                .texture("lever", modLoc("block/iron_lever"))
                .element()
                .from(5, 0, 4)
                .to(11, 3, 12)
                .allFaces(base_generator)
                .end()
                .element()
                .from(7, 1, 7)
                .to(9, 11, 9)
                .rotation()
                .origin(8, 1, 8)
                .axis(Direction.Axis.X)
                .angle(45)
                .end()
                .allFaces(lever_generator)
                .end();

        VariantBlockStateBuilder builder = getVariantBuilder(ExtrasSetup.IRON_LEVER.get());
        for (Boolean powered : IronLeverBlock.POWERED.getPossibleValues()) {
            ModelFile model = powered ? lever_on : lever_off;
            for (AttachFace face : IronLeverBlock.FACE.getPossibleValues()) {
                int xangle = (face == AttachFace.CEILING) ? 180 : (face == AttachFace.WALL) ? 90 : 0;

                for (Direction dir : IronLeverBlock.FACING.getPossibleValues()) {
                    int yangle = (int) dir.toYRot();
                    yangle = face != AttachFace.CEILING ? (yangle + 180) % 360 : yangle;

                    builder
                            .partialState()
                            .with(IronLeverBlock.POWERED, powered)
                            .with(IronLeverBlock.FACE, face)
                            .with(IronLeverBlock.FACING, dir)
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
