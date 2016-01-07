package common.legobmw99.allomancy.blocks;

import common.legobmw99.allomancy.Allomancy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class OreBlock extends Block {
	public static Block oreTin;
	public static Block oreLead;
	public static Block oreCopper;
	public static Block oreZinc;
	
	public enum OreType {
		LEAD, TIN, COPPER, ZINC;
	}

	private OreType type;

	public OreBlock(OreType type) {
		super(Material.rock);
		this.setHardness(.5F);
		this.setStepSound(Block.soundTypeStone);
		switch (type) {
			case LEAD: this.setUnlocalizedName("allomancy_leadore");
                break;
			case TIN: this.setUnlocalizedName("allomancy_tinore");
				break;
			case ZINC: this.setUnlocalizedName("allomancy_zincore");
				break;
			case COPPER: this.setUnlocalizedName("allomancy:copper_ore");
				break;

		}

		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHarvestLevel("pick", 1);
	}

	public static void init() {
		oreTin = new OreBlock(OreType.TIN);
		GameRegistry.registerBlock(oreTin, "tinore");
		oreLead = new OreBlock(OreType.LEAD);
		GameRegistry.registerBlock(oreLead, "leadore");
		oreCopper = new OreBlock(OreType.COPPER);
		GameRegistry.registerBlock(oreCopper, "copperore");
		oreZinc = new OreBlock(OreType.ZINC);
		GameRegistry.registerBlock(oreZinc, "zincore");
	}


}
