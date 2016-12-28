package common.legobmw99.allomancy.blocks;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.Registry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class OreBlock extends Block {
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	
	public enum OreType {
		LEAD, TIN, COPPER, ZINC
    }

	private OreType type;

	public OreBlock(OreType type) {
		super(Material.ROCK);
		this.setHardness(.5F);
		switch (type) {
			case LEAD: this.setUnlocalizedName("oreLead");
                break;
			case TIN: this.setUnlocalizedName("oreTin");
				break;
			case ZINC: this.setUnlocalizedName("oreZinc");
				break;
			case COPPER: this.setUnlocalizedName("oreCopper");
				break;

		}
		this.setCreativeTab(Registry.tabsAllomancy);
		this.setHarvestLevel("pick", 1);
	}

	public static void init() {
		oreTin = new OreBlock(OreType.TIN);
		GameRegistry.register(oreTin, new ResourceLocation(Allomancy.MODID,"oreTin"));
		GameRegistry.register(new ItemBlock(oreTin).setRegistryName(oreTin.getRegistryName()));
		OreDictionary.registerOre("oreTin", oreTin);
		
		oreLead = new OreBlock(OreType.LEAD);
		GameRegistry.register(oreLead,  new ResourceLocation(Allomancy.MODID,"oreLead"));
		GameRegistry.register(new ItemBlock(oreLead).setRegistryName(oreLead.getRegistryName()));
		OreDictionary.registerOre("oreLead", oreLead);
		
		oreCopper = new OreBlock(OreType.COPPER);
		GameRegistry.register(oreCopper,  new ResourceLocation(Allomancy.MODID,"oreCopper"));
		GameRegistry.register(new ItemBlock(oreCopper).setRegistryName(oreCopper.getRegistryName()));
		OreDictionary.registerOre("oreCopper", oreCopper);
		
		oreZinc = new OreBlock(OreType.ZINC);
		GameRegistry.register(oreZinc,  new ResourceLocation(Allomancy.MODID,"oreZinc"));
		GameRegistry.register(new ItemBlock(oreZinc).setRegistryName(oreZinc.getRegistryName()));
		OreDictionary.registerOre("oreZinc", oreZinc);

	}


}
