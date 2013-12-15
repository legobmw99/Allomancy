package com.entropicdreams.darva;

import com.entropicdreams.darva.handlers.PowerTickHandler;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	
	public void RegisterTickHandlers()
	{
		System.out.println("here2.");
		//derp
		TickRegistry.registerTickHandler(new PowerTickHandler(), Side.SERVER);
		System.out.println("here3.");
	}
}
