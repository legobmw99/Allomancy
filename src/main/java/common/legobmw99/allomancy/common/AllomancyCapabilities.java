package common.legobmw99.allomancy.common;

import java.util.concurrent.Callable;

import common.legobmw99.allomancy.Allomancy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public class AllomancyCapabilities implements
		ICapabilitySerializable<NBTTagCompound> {

	public static final int matIron = 0;
	public static final int matSteel = 1;
	public static final int matTin = 2;
	public static final int matPewter = 3;
	public static final int matZinc = 4;
	public static final int matBrass = 5;
	public static final int matCopper = 6;
	public static final int matBronze = 7;

	public static boolean isMistborn = false;

	public static final ResourceLocation IDENTIFIER = new ResourceLocation(
			Allomancy.MODID, "Allomancy_Data");
	public int selected = 0;
	public int damageStored = 0;
	public int[] BurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
	public int[] MaxBurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
	public static int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
	public boolean[] MetalBurning = { false, false, false, false, false, false,
			false, false };

	public EntityPlayer player;

	public void AllomancyData(EntityPlayer player) {
		this.player = player;
	}

	public static AllomancyCapabilities forPlayer(Entity player) {
		return player.getCapability(Allomancy.PLAYER_CAP, null);
	}

	public AllomancyCapabilities(EntityPlayer player) {
		this.player = player;
	}

	public static void register() {
		CapabilityManager.INSTANCE.register(AllomancyCapabilities.class,
				new AllomancyCapabilities.Storage(),
				new AllomancyCapabilities.Factory());
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("ismistborn", AllomancyCapabilities.isMistborn);
		nbt.setInteger("iron", this.MetalAmounts[0]);
		nbt.setInteger("steel", this.MetalAmounts[1]);
		nbt.setInteger("tin", this.MetalAmounts[2]);
		nbt.setInteger("pewter", this.MetalAmounts[3]);
		nbt.setInteger("zinc", this.MetalAmounts[4]);
		nbt.setInteger("bronze", this.MetalAmounts[5]);
		nbt.setInteger("copper", this.MetalAmounts[6]);
		nbt.setInteger("brass", this.MetalAmounts[7]);
		nbt.setInteger("selected", this.selected);
		return nbt;

	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		isMistborn = compound.getBoolean("ismistborn");
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

	public int getSelected() {
		return this.selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		if ((this.selected > 4) || (this.selected < 0)) {
			this.selected = 0;
		}
	}

}
