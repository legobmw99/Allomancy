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
import net.minecraftforge.common.util.INBTSerializable;

public class AllomancyCapabilites implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
	
	public static final int matIron = 0;
	public static final int matSteel = 1;
	public static final int matTin = 2;
	public static final int matPewter = 3;
	public static final int matZinc = 4;
	public static final int matBrass = 5;
	public static final int matCopper = 6;
	public static final int matBronze = 7;

	public static boolean isMistborn = false;

	public static final String IDENTIFIER = "Allomancy_Data";
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

	public static AllomancyCapabilites forPlayer(Entity player) {
		return player.getCapability(Allomancy.PLAYER_CAP, null);
	}

	
    public static void register()
    {
    	CapabilityManager.INSTANCE.register(AllomancyCapabilites.class, new AllomancyCapabilites.Storage(), new AllomancyCapabilites.Factory());    
    	}
	
	@Override
	public NBTTagCompound serializeNBT() {
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
		nbt.setTag(IDENTIFIER, nbt);
		return nbt;

	}

    
	@Override
	public void deserializeNBT(NBTTagCompound compound) {
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

	public int getSelected() {
		return this.selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		if ((this.selected > 4) || (this.selected < 0)) {
			this.selected = 0;
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return Allomancy.PLAYER_CAP != null && capability == Allomancy.PLAYER_CAP;

	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return Allomancy.PLAYER_CAP != null && capability == Allomancy.PLAYER_CAP ? (T)this : null;
	}

	 public static class Storage implements Capability.IStorage<AllomancyCapabilites>
	    {

	        @Override
	        public NBTBase writeNBT(Capability<AllomancyCapabilites> capability, AllomancyCapabilites instance, EnumFacing side)
	        {
	            return null;
	        }

	        @Override
	        public void readNBT(Capability<AllomancyCapabilites> capability, AllomancyCapabilites instance, EnumFacing side, NBTBase nbt)
	        {

	        }

	    }

	    public static class Factory implements Callable<AllomancyCapabilites>
	    {
	        @Override
	        public AllomancyCapabilites call() throws Exception
	        {
	            return null;
	        }
	    }
}
	
