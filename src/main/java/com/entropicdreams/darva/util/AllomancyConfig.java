package com.entropicdreams.darva.util;

import java.io.File;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class AllomancyConfig {

		public static void initProps (File confFile)
		{
			Configuration config = new Configuration(confFile);
			config.load();
			
			generateCopper = config.get("Worldgen Disabler", "Generate Copper", true).getBoolean(true); 
			generateTin = config.get("Worldgen Disabler", "Generate Tin", true).getBoolean(true); 
			generateLead = config.get("Worldgen Disabler", "Generate Lead", true).getBoolean(true); 
			generateZinc = config.get("Worldgen Disabler", "Generate Zinc", true).getBoolean(true); 
			
			copperDensity = config.get("Worldgen", "Copper Density", 2, "Density: Chances per chunk").getInt(2);
	        tinDensity = config.get("Worldgen", "Tin Density", 2).getInt(2);
	        leadDensity = config.get("Worldgen", "Lead Density", 2).getInt(2);
	        zincDensity = config.get("Worldgen", "Zinc Density", 2).getInt(2);
	        
			copperMinY = config.get("Worldgen", "Copper Min Y", 30).getInt(30);
			copperMaxY = config.get("Worldgen", "Copper Max Y", 50).getInt(50);
		    tinMinY = config.get("Worldgen", "Tin Min Y", 40).getInt(40);
		    tinMaxY = config.get("Worldgen", "Tin Max Y", 64).getInt(64);
		    leadMinY = config.get("Worldgen", "Lead Min Y", 20).getInt(20);
			leadMaxY = config.get("Worldgen", "Lead Max Y", 40).getInt(40);
		    zincMinY = config.get("Worldgen", "Zinc Min Y", 20).getInt(20);
		    zincMaxY = config.get("Worldgen", "Zinc Max Y", 40).getInt(40);
			config.save();
		}
		public static boolean generateCopper;
	    public static boolean generateTin;
	    public static boolean generateLead;
	    public static boolean generateZinc;
	    
	    public static int copperDensity;
	    public static int tinDensity;
	    public static int leadDensity;
	    public static int zincDensity;
	    
	    public static int copperMinY;
	    public static int copperMaxY;
	    public static int tinMinY;
	    public static int tinMaxY;
	    public static int leadMinY;
	    public static int leadMaxY;
	    public static int zincMinY;
	    public static int zincMaxY;    
}
