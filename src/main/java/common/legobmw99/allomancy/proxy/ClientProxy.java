package common.legobmw99.allomancy.proxy;

import net.minecraftforge.fml.client.registry.RenderingRegistry;


public class ClientProxy extends CommonProxy {
	@Override
	public void init() {
		RenderingRegistry.addNewArmourRendererPrefix("Mistcloak");
	}
}
