package common.legobmw99.allomancy.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		RenderingRegistry.addNewArmourRendererPrefix("Mistcloak");
	}
}
