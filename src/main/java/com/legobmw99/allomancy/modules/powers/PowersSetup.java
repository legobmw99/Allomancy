package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.client.ClientEventHandler;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PowersSetup {


    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Allomancy.MODID);
    private static final Supplier<SingletonArgumentInfo<AllomancyPowerType>> CONTAINER_CLASS = COMMAND_ARGUMENT_TYPES.register("allomancy_power",
                                                                                                                               () -> ArgumentTypeInfos.registerByClass(
                                                                                                                                       AllomancyPowerType.class,
                                                                                                                                       SingletonArgumentInfo.contextFree(
                                                                                                                                               AllomancyPowerType::allomancyPowerType)));

    public static void clientInit(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            NeoForge.EVENT_BUS.register(new ClientEventHandler());
        });
    }


    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }


    public static void init(final FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            NeoForge.EVENT_BUS.register(CommonEventHandler.class);
        });
    }

    public static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }
}
