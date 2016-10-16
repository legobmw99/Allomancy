package common.legobmw99.allomancy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import common.legobmw99.allomancy.common.Registry;

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
		GameRegistry.registerBlock(oreTin, "oreTin");
		OreDictionary.registerOre("oreTin", oreTin);
		oreLead = new OreBlock(OreType.LEAD);
		GameRegistry.registerBlock(oreLead, "oreLead");
		OreDictionary.registerOre("oreLead", oreLead);
		oreCopper = new OreBlock(OreType.COPPER);
		GameRegistry.registerBlock(oreCopper, "oreCopper");
		OreDictionary.registerOre("oreCopper", oreCopper);
		oreZinc = new OreBlock(OreType.ZINC);
		GameRegistry.registerBlock(oreZinc, "oreZinc");
		OreDictionary.registerOre("oreZinc", oreZinc);

	}


}
