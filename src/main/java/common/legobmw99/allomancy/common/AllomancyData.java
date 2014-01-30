package common.legobmw99.allomancy.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class AllomancyData implements IExtendedEntityProperties{

	public static final int matIron = 0;
	public static final int matSteel = 1;
	public static final int matTin = 2;
	public static final int matPewter = 3;
	public static final int matZinc = 4;
	public static final int matBronze = 5;
	public static final int matCopper = 6;
	public static final int matBrass = 7;
	
    public static boolean isMistborn = false;

	public static final String IDENTIFIER = "Allomancy_Data";
	public boolean Dirty = true;
	public int selected = 0;

	public int damageStored = 0;
	public int[] BurnTime = { 600, 600, 1200, 400, 600, 600, 800, 800 };
	public int[] MaxBurnTime = { 600, 600, 1200, 400, 600, 600, 800, 800 };
	public int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
	public boolean[] MetalBurning = { false, false, false, false, false, false,
			false, false };
	private final EntityPlayer player;

	public AllomancyData(EntityPlayer Player) 
	{
		player = Player;
	}

	public static AllomancyData forPlayer(Entity player) 
	{
		return (AllomancyData) player.getExtendedProperties(IDENTIFIER);
		
	}
	public void updateBurn(int mat, boolean value)
	{
		if (MetalAmounts[mat] != 0) {
			MetalBurning[mat] = value;
		} else
			MetalBurning[mat] = false;
	}

	public void updateData(int[] value)
	{
		for (int i = 0; i < MetalAmounts.length; i++)
			MetalAmounts[i] = value[i];
	}
	@Override
	public void saveNBTData(NBTTagCompound compound) 
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("ismistborn", AllomancyData.isMistborn);
		nbt.setInteger("iron", MetalAmounts[0]);
		nbt.setInteger("steel", MetalAmounts[1]);
		nbt.setInteger("tin", MetalAmounts[2]);
		nbt.setInteger("pewter", MetalAmounts[3]);
		nbt.setInteger("zinc", MetalAmounts[4]);
		nbt.setInteger("bronze", MetalAmounts[5]);
		nbt.setInteger("copper", MetalAmounts[6]);
		nbt.setInteger("brass", MetalAmounts[7]);
		nbt.setInteger("selected", selected);
		compound.setTag(IDENTIFIER, nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = compound.getCompoundTag(IDENTIFIER);
		isMistborn = nbt.getBoolean("ismistborn");
		MetalAmounts[0] = nbt.getInteger("iron");
		MetalAmounts[1] = nbt.getInteger("steel");
		MetalAmounts[2] = nbt.getInteger("tin");
		MetalAmounts[3] = nbt.getInteger("pewter");
		MetalAmounts[4] = nbt.getInteger("zinc");
		MetalAmounts[5] = nbt.getInteger("bronze");
		MetalAmounts[6] = nbt.getInteger("copper");
		MetalAmounts[7] = nbt.getInteger("brass");
	}

	@Override
	public void init(Entity entity, World world) 
	{

	}

	public int getSelected() 
	{
		return selected;
	}

	public void setSelected(int selected) 
	{
		this.selected = selected;
		if (this.selected > 4 || this.selected < 0) {
			this.selected = 0;
		}
		Dirty = true;
	}

}