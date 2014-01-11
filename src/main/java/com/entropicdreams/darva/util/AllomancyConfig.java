package com.entropicdreams.darva.util;

import java.io.File;

import com.entropicdreams.darva.items.ItemGrinder;
import com.entropicdreams.darva.items.ItemMistcloak;
import com.entropicdreams.darva.items.ItemVial;

import cpw.mods.fml.common.Loader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
			
			copperDensity = config.get("Worldgen", "Copper Density", 5, "Density: Chances per chunk").getInt(5);
	        tinDensity = config.get("Worldgen", "Tin Density", 5).getInt(5);
	        leadDensity = config.get("Worldgen", "Lead Density", 5).getInt(5);
	        zincDensity = config.get("Worldgen", "Zinc Density", 5).getInt(5);
	        
	        oreCopper = config.getBlock("Copper Ore", 242).getInt(242);
	        oreTin = config.getBlock("Tin Ore", 240).getInt(240);
	        oreLead = config.getBlock("Lead Ore", 241).getInt(241);
	        oreZinc = config.getBlock("Zinc Lead", 243).getInt(243);
	        
			copperMinY = config.get("Worldgen", "Copper Min Y", 30).getInt(30);
			copperMaxY = config.get("Worldgen", "Copper Max Y", 50).getInt(50);
		    tinMinY = config.get("Worldgen", "Tin Min Y", 40).getInt(40);
		    tinMaxY = config.get("Worldgen", "Tin Max Y", 64).getInt(64);
		    leadMinY = config.get("Worldgen", "Lead Min Y", 20).getInt(20);
			leadMaxY = config.get("Worldgen", "Lead Max Y", 40).getInt(40);
		    zincMinY = config.get("Worldgen", "Zinc Min Y", 20).getInt(20);
		    zincMaxY = config.get("Worldgen", "Zinc Max Y", 40).getInt(40);
		    
		    itemGrinder = config.getItem("Item Grinder", 500).getInt(500);
			itemTinIngot = config.getItem("Tin Ingot", 801).getInt(801);
			itemTinFlakes = config.getItem("Tin Flakes", 802).getInt(802);
			itemLeadIngot = config.getItem("Lead Ingot", 803).getInt(803);
			itemLeadFlakes = config.getItem("Lead Flakes", 804).getInt(804);
			itemCopperIngot = config.getItem("Copper Ingot", 805).getInt(805);
			itemCopperFlakes = config.getItem("Copper Flakes", 806).getInt(806);
			itemZincIngot = config.getItem("Zinc Ingot", 807).getInt(807);
			itemZincFlakes = config.getItem("Zinc Flakes", 808).getInt(808);
			itemIronFlakes = config.getItem("Iron Flakes", 809).getInt(809);
			itemSteelFlakes = config.getItem("Steel Flakes", 810).getInt(810);
			itemBrassFlakes = config.getItem("Brass Flakes", 811).getInt(811);
			itemPewterFlakes =config.getItem("Pewter Flakes", 812).getInt(812);
			itemBronzeFlakes = config.getItem("Bronze Flakes", 813).getInt(813);
			Mistcloak = config.getItem("Mistcloak", 814).getInt(814);
			itemVial = config.getItem("Vials", 815).getInt(815);
		    
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
	    
	    public static int oreCopper; 
	    public static int oreTin;
	    public static int oreLead;
	    public static int oreZinc;
	    
	    public static int copperMinY;
	    public static int copperMaxY;
	    public static int tinMinY;
	    public static int tinMaxY;
	    public static int leadMinY;
	    public static int leadMaxY;
	    public static int zincMinY;
	    public static int zincMaxY;    
	    
	    public static int itemGrinder;
	    public static int itemTinIngot;
	    public static int itemTinFlakes;
	    public static int itemLeadIngot;
	    public static int itemLeadFlakes;
	    public static int itemCopperIngot;
	    public static int itemCopperFlakes;
	    public static int itemZincIngot;
	    public static int itemZincFlakes;
	    public static int itemIronFlakes;
	    public static int itemSteelFlakes;
	    public static int itemBrassFlakes;
	    public static int itemPewterFlakes;
	    public static int itemBronzeFlakes;
	    public static int Mistcloak;
	    public static int itemVial;
}
