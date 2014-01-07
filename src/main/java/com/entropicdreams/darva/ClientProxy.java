package com.entropicdreams.darva;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import com.entropicdreams.darva.handlers.DamageHandler;
import com.entropicdreams.darva.handlers.PowerTickHandler;
import com.entropicdreams.darva.handlers.SoundHandler;
import com.entropicdreams.darva.handlers.renderHandler;
import com.entropicdreams.darva.handlers.keyhandlers.BurnFirstKeyBind;
import com.entropicdreams.darva.handlers.keyhandlers.BurnSecondKeyBind;
import com.entropicdreams.darva.handlers.keyhandlers.SwitchMetalKeybind;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	@Override
	public void RegisterTickHandlers() {
		PowerTickHandler pth;
		pth = new PowerTickHandler();
		MinecraftForge.EVENT_BUS.register(pth);
		// TODO Auto-generated method stub

		renderHandler rh = new renderHandler();
		TickRegistry.registerTickHandler(rh, Side.CLIENT);
		TickRegistry.registerTickHandler(pth, Side.SERVER);
		TickRegistry.registerTickHandler(pth, Side.CLIENT);
		ModMain.instance.MPC = new MetalParticleController();
		TickRegistry.registerTickHandler(ModMain.instance.MPC, Side.CLIENT);
		
		
		KeyBinding[] key = {new KeyBinding("Select Metal", Keyboard.KEY_R)};
        boolean[] repeat = {false};
        KeyBindingRegistry.registerKeyBinding(new SwitchMetalKeybind(key, repeat));		 
        
		KeyBinding[] key2 = {new KeyBinding("Burn First", Keyboard.KEY_F)};
        KeyBindingRegistry.registerKeyBinding(new BurnFirstKeyBind(key2, repeat));
        KeyBinding[] key3 = {new KeyBinding("Burn Second", Keyboard.KEY_G)};
        KeyBindingRegistry.registerKeyBinding(new BurnSecondKeyBind(key3, repeat));
        
		MinecraftForge.EVENT_BUS.register(new SoundHandler());

		RenderingRegistry.registerEntityRenderingHandler(FlyingItem.class, new FlyingItemRenderer());
	}

}
