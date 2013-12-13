package com.entropicdreams.darva;

import com.entropicdreams.darva.handlers.renderHandler;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	@Override
	public void RegisterTickHandlers() {
		// TODO Auto-generated method stub
		super.RegisterTickHandlers();
		renderHandler rh = new renderHandler();
		TickRegistry.registerTickHandler(rh, Side.CLIENT);
	}

}
