package com.legobmw99.allomancy;

import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.proxy.ClientProxy;
import com.legobmw99.allomancy.util.proxy.CommonProxy;
import com.legobmw99.allomancy.util.proxy.ServerProxy;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(Allomancy.MODID)
public class Allomancy {
	public static final String MODID = "allomancy";

	public static File configDirectory;

	public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());;

	private static final Logger LOGGER = LogManager.getLogger();

	public static Allomancy instance;

	@CapabilityInject(AllomancyCapability.class)
	public static final Capability<AllomancyCapability> PLAYER_CAP = null;


	public Allomancy(){
		instance = this;
		//ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,AllomancyConfig.SERVER);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);

	}

	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	public void serverInit(FMLServerStartingEvent event) {
		proxy.serverInit(event);
	}

	public void init(final FMLCommonSetupEvent event) {
		proxy.init(event);
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	
	

}
