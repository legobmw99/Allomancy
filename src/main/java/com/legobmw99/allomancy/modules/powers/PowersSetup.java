package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.ClientEventHandler;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PowersSetup {


    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Allomancy.MODID);
    private static final RegistryObject<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS = COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                                                                                                                     () -> ArgumentTypeInfos.registerByClass(
                                                                                                                                             AllomancyPowerType.class,
                                                                                                                                             SingletonArgumentInfo.contextFree(
                                                                                                                                                     AllomancyPowerType::allomancyPowerType)));

    public static void clientInit(final FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }


    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }


    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            MinecraftForge.EVENT_BUS.register(CommonEventHandler.class);
        });
    }

    public static void register() {
        COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        PowersClientSetup.register();
    }
}
