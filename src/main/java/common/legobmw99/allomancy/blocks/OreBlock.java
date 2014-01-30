package common.legobmw99.allomancy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;


public class OreBlock extends Block {
	public enum OreType
	{
		LEAD, TIN, COPPER, ZINC;
	}
	private OreType type;
	private IIcon texture;
	public OreBlock(Material material, OreType type) {
		super(material);
		//This.SetHardness
		this.func_149711_c(.5F);
		//setStepSound(Block.soundStoneFootstep)
		this.func_149672_a(Block.field_149780_i);
		//setCreativeTabs
		this.func_149647_a(CreativeTabs.tabBlock);
		this.setHarvestLevel("pick", 1);
	}
	//registerIcons()
	  public void registerIcon (IIconRegister iconRegister)
      {
		  switch (type)
          {
		  case LEAD:
			  texture = iconRegister.registerIcon("allomancy:leadore");
		  case TIN:
			  texture = iconRegister.registerIcon("allomancy:tinore");
		  case ZINC:
			  texture = iconRegister.registerIcon("allomancy:zincrore");
		  case COPPER:
			  texture = iconRegister.registerIcon("allomancy:copperore");

          }
      }
	  //getIcon()
	  public IIcon func_149691_a(int side, int meta)
      {
              return texture;
      }
}
