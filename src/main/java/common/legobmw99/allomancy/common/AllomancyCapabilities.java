package common.legobmw99.allomancy.common;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import common.legobmw99.allomancy.Allomancy;

public class AllomancyCapabilities implements
		ICapabilitySerializable<NBTTagCompound> {
	
	public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "Allomancy_Data");
	public static int[] MaxBurnTime = { 1800, 1800, 3600, 600, 1800, 1800, 2400, 1600 };
	public static final int matIron = 0;
	public static final int matSteel = 1;
	public static final int matTin = 2;
	public static final int matPewter = 3;
	public static final int matZinc = 4;
	public static final int matBrass = 5;
	public static final int matCopper = 6;
	public static final int matBronze = 7;

	private boolean isMistborn;

	private int selected = 0;
	private int damageStored = 0;
	private int[] BurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
	private int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
	private boolean[] MetalBurning = { false, false, false, false, false, false,
			false, false };

	private EntityPlayer player;

	public static AllomancyCapabilities forPlayer(Entity player) {
		return player.getCapability(Allomancy.PLAYER_CAP, null);
	}

	public AllomancyCapabilities(EntityPlayer player) {
		this.player = player;
	}
	public int getSelected() {
		return this.selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		if ((this.selected > 4) || (this.selected < 0)) {
			this.selected = 0;
		}
	}

	public boolean isMistborn() {
		return isMistborn;
	}

	public void setMistborn(boolean ismb) {
		this.isMistborn = ismb;
	}

	public boolean getMetalBurning(int metal) {
		return MetalBurning[metal];
	}

	public void setMetalBurning(int metal, boolean metalBurning) {
		MetalBurning[metal] = metalBurning;
	}

	public int getDamageStored() {
		return damageStored;
	}

	public void setDamageStored(int damageStored) {
		this.damageStored = damageStored;
	}

	public int getMetalAmounts(int metal) {
		return MetalAmounts[metal];
	}

	public void setMetalAmounts(int metal, int metalAmounts) {
		MetalAmounts[metal] = metalAmounts;
	}

	public int getBurnTime(int metal) {
		return BurnTime[metal];
	}

	public void setBurnTime(int metal, int burnTime) {
		BurnTime[metal] = burnTime;
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(AllomancyCapabilities.class,
				new AllomancyCapabilities.Storage(),
				new AllomancyCapabilities.Factory());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("ismistborn", this.isMistborn());
		nbt.setInteger("iron", this.getMetalAmounts(0));
		nbt.setInteger("steel", this.getMetalAmounts(1));
		nbt.setInteger("tin", this.getMetalAmounts(2));
		nbt.setInteger("pewter", this.getMetalAmounts(3));
		nbt.setInteger("zinc", this.getMetalAmounts(4));
		nbt.setInteger("bronze", this.getMetalAmounts(5));
		nbt.setInteger("copper", this.getMetalAmounts(6));
		nbt.setInteger("brass", this.getMetalAmounts(7));
		nbt.setInteger("selected", this.getSelected());
		return nbt;

	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		this.setMistborn(compound.getBoolean("ismistborn"));
		this.MetalAmounts[0] = compound.getInteger("iron");
		this.MetalAmounts[1] = compound.getInteger("steel");
		this.MetalAmounts[2] = compound.getInteger("tin");
		this.MetalAmounts[3] = compound.getInteger("pewter");
		this.MetalAmounts[4] = compound.getInteger("zinc");
		this.MetalAmounts[5] = compound.getInteger("bronze");
		this.MetalAmounts[6] = compound.getInteger("copper");
		this.MetalAmounts[7] = compound.getInteger("brass");
		this.selected = compound.getInteger("selected");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return Allomancy.PLAYER_CAP != null
				&& capability == Allomancy.PLAYER_CAP;

	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return Allomancy.PLAYER_CAP != null
				&& capability == Allomancy.PLAYER_CAP ? (T) this : null;
	}



	public static class Storage implements
			Capability.IStorage<AllomancyCapabilities> {

		@Override
		public NBTBase writeNBT(Capability<AllomancyCapabilities> capability,
				AllomancyCapabilities instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT(Capability<AllomancyCapabilities> capability,
				AllomancyCapabilities instance, EnumFacing side, NBTBase nbt) {

		}

	}

	public static class Factory implements Callable<AllomancyCapabilities> {
		@Override
		public AllomancyCapabilities call() throws Exception {
			return null;
		}
	}

}
