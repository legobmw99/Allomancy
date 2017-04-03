package common.legobmw99.allomancy.util;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class AllomancyConfig {

	public static void initProps(File confFile) {
		Configuration config = new Configuration(confFile);
		config.load();

		generateCopper = config.get("Worldgen Disabler", "Generate Copper Ore",
				true).getBoolean(true);
		generateTin = config.get("Worldgen Disabler", "Generate Tin Ore", true)
				.getBoolean(true);
		generateLead = config.get("Worldgen Disabler", "Generate Lead Ore", true)
				.getBoolean(true);
		generateZinc = config.get("Worldgen Disabler", "Generate Zinc Ore", true)
				.getBoolean(true);

		copperDensity = config.get("Worldgen", "Copper Density", 5,
				"Density: Chances per chunk").getInt(5);
		tinDensity = config.get("Worldgen", "Tin Density", 5).getInt(5);
		leadDensity = config.get("Worldgen", "Lead Density", 5).getInt(5);
		zincDensity = config.get("Worldgen", "Zinc Density", 5).getInt(5);

		copperMinY = config.get("Worldgen", "Copper Min Y", 30).getInt(30);
		copperMaxY = config.get("Worldgen", "Copper Max Y", 50).getInt(50);
		tinMinY = config.get("Worldgen", "Tin Min Y", 40).getInt(40);
		tinMaxY = config.get("Worldgen", "Tin Max Y", 64).getInt(64);
		leadMinY = config.get("Worldgen", "Lead Min Y", 20).getInt(20);
		leadMaxY = config.get("Worldgen", "Lead Max Y", 40).getInt(40);
		zincMinY = config.get("Worldgen", "Zinc Min Y", 20).getInt(20);
		zincMaxY = config.get("Worldgen", "Zinc Max Y", 40).getInt(40);
		
		overlayPosition = config.get("Display", "Screen Overlay Position", 0).getInt(0);
		overlayWithText = config.get("Display", "Add Text to Overlay", false)
                .getBoolean(false);
		
		randomizeMistings = config.get("Gameplay","Spawn players as a random Misting",true).getBoolean(true);

		config.save();
	}

	public static boolean generateCopper;
	public static boolean generateTin;
	public static boolean generateLead;
	public static boolean generateZinc;
	
	public static boolean randomizeMistings;
	
	public static int overlayPosition;
	public static boolean overlayWithText;

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
