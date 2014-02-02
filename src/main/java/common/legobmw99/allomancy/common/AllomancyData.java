package common.legobmw99.allomancy.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class AllomancyData implements IExtendedEntityProperties {

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

	public AllomancyData(EntityPlayer Player) {
	}

	public static AllomancyData forPlayer(Entity player) {
		return (AllomancyData) player.getExtendedProperties(IDENTIFIER);

	}

	public void updateBurn(int mat, boolean value) {
		if (this.MetalAmounts[mat] != 0) {
			this.MetalBurning[mat] = value;
		} else
			this.MetalBurning[mat] = false;
	}

	public void updateData(int[] value) {
		for (int i = 0; i < this.MetalAmounts.length; i++)
			this.MetalAmounts[i] = value[i];
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("ismistborn", AllomancyData.isMistborn);
		nbt.setInteger("iron", this.MetalAmounts[0]);
		nbt.setInteger("steel", this.MetalAmounts[1]);
		nbt.setInteger("tin", this.MetalAmounts[2]);
		nbt.setInteger("pewter", this.MetalAmounts[3]);
		nbt.setInteger("zinc", this.MetalAmounts[4]);
		nbt.setInteger("bronze", this.MetalAmounts[5]);
		nbt.setInteger("copper", this.MetalAmounts[6]);
		nbt.setInteger("brass", this.MetalAmounts[7]);
		nbt.setInteger("selected", this.selected);
		compound.setTag(IDENTIFIER, nbt);

	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = compound.getCompoundTag(IDENTIFIER);
		isMistborn = nbt.getBoolean("ismistborn");
		this.MetalAmounts[0] = nbt.getInteger("iron");
		this.MetalAmounts[1] = nbt.getInteger("steel");
		this.MetalAmounts[2] = nbt.getInteger("tin");
		this.MetalAmounts[3] = nbt.getInteger("pewter");
		this.MetalAmounts[4] = nbt.getInteger("zinc");
		this.MetalAmounts[5] = nbt.getInteger("bronze");
		this.MetalAmounts[6] = nbt.getInteger("copper");
		this.MetalAmounts[7] = nbt.getInteger("brass");
	}

	@Override
	public void init(Entity entity, World world) {

	}

	public int getSelected() {
		return this.selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		if ((this.selected > 4) || (this.selected < 0)) {
			this.selected = 0;
		}
		this.Dirty = true;
	}

}