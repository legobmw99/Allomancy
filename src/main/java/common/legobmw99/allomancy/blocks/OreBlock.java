package common.legobmw99.allomancy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class OreBlock extends Block {
	public enum OreType {
		LEAD, TIN, COPPER, ZINC;
	}

	private OreType type;
	private IIcon texture;

	public OreBlock(Material material, OreType type) {
		super(material);

		this.setHardness(.5F);

		this.setStepSound(Block.soundTypeStone);

		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHarvestLevel("pick", 1);
	}


	public void registerIcon(IIconRegister iconRegister) {
		switch (this.type) {
		case LEAD:
			this.texture = iconRegister.registerIcon("allomancy/ore:leadore");
		case TIN:
			this.texture = iconRegister.registerIcon("allomancy/ore:tinore");
		case ZINC:
			this.texture = iconRegister.registerIcon("allomancy/ore:zincrore");
		case COPPER:
			this.texture = iconRegister.registerIcon("allomancy/ore:copperore");

		}
	}

	// getIcon()
	@Override
	public IIcon getIcon(int side, int meta) {
		return this.texture;
	}
}
