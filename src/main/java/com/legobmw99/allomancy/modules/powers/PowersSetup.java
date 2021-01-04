package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.modules.powers.client.ClientEventHandler;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerCommand;
import com.legobmw99.allomancy.modules.powers.command.AllomancyPowerType;
import com.legobmw99.allomancy.modules.powers.handlers.CommonEventHandler;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class PowersSetup {

    public static void clientInit(final FMLClientSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        PowersClientSetup.initKeyBindings();
    }

    public static void registerCommands(final RegisterCommandsEvent e) {
        AllomancyPowerCommand.register(e.getDispatcher());
    }

    public static void init(final FMLCommonSetupEvent e) {
        //Register our ArgumentType so it can be sent over network
        ArgumentTypes.register("allomancy_power", AllomancyPowerType.class, new ArgumentSerializer<>(AllomancyPowerType::powerType));
        AllomancyCapability.register();
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    public static void register() {
        PowersClientSetup.register();
    }
}
