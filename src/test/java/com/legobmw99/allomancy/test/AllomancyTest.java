package com.legobmw99.allomancy.test;

import com.legobmw99.allomancy.test.modules.consumables.recipe.GrinderCraftingTest;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.testframework.conf.ClientConfiguration;
import net.neoforged.testframework.conf.Feature;
import net.neoforged.testframework.conf.FrameworkConfiguration;
import net.neoforged.testframework.impl.MutableTestFramework;
import net.neoforged.testframework.summary.GitHubActionsStepSummaryDumper;
import org.lwjgl.glfw.GLFW;

@Mod(AllomancyTest.MODID)
public class AllomancyTest {
    public static final String MODID = "allomancy_test";

    public AllomancyTest(IEventBus bus, ModContainer container) {
        final MutableTestFramework framework = FrameworkConfiguration
                .builder(rl("tests"))
                .clientConfiguration(() -> {
                    ClientConfiguration.Builder builder = ClientConfiguration.builder();
                    builder.toggleOverlayKey(GLFW.GLFW_KEY_J);
                    builder.openManagerKey(GLFW.GLFW_KEY_N);
                    return builder.build();
                })
                .enable(Feature.CLIENT_SYNC, Feature.CLIENT_MODIFICATIONS, Feature.MAGIC_ANNOTATIONS,
                        Feature.GAMETEST)
                .dumpers(new GitHubActionsStepSummaryDumper())
                .build()
                .create();

        GrinderCraftingTest.register(framework.tests()::register);

        framework.init(bus, container);
    }


    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
