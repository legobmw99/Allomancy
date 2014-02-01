package com.entropicdreams.darva;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class AllomancyData implements IExtendedEntityProperties {

	public static final int matIron = 0;
	public static final int matSteel = 1;
	public static final int matTin = 2;
	public static final int matPewter = 3;
	public static final int matCopper = 4;
	public static final int matBronze = 5;
	public static final int matZinc = 6;
	public static final int matBrass = 7;
	
    public static boolean isMistborn = false;

	public static final String IDENTIFIER = "Allomancy_Data";
	public boolean Dirty = true;
	public int selected = 0;

	public int damageStored = 0;
	public int[] BurnTime = { 1200, 1200, 2400, 1000, 1200, 1200, 1600, 1600 };
	public int[] MaxBurnTime = { 1200, 1200, 2400, 1000, 1200, 1200, 1600, 1600 };
	public int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
	public boolean[] MetalBurning = { false, false, false, false, false, false,
			false, false };
	private final EntityPlayer player;

	public AllomancyData(EntityPlayer Player) {
		player = Player;
	}

	public static AllomancyData forPlayer(Entity player) {

		return (AllomancyData) player.getExtendedProperties(IDENTIFIER);
	}

	public void updateBurn(Packet250CustomPayload packet) {
		DataInputStream inputStream = new DataInputStream(
				new ByteArrayInputStream(packet.data));
		int Material = -1;
		boolean value;
		try {
			inputStream.readInt(); // Throw away packet type info.
			Material = inputStream.readInt();
			value = inputStream.readBoolean();

			if (MetalAmounts[Material] != 0) {
				MetalBurning[Material] = value;
			} else
				MetalBurning[Material] = false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateData(Packet250CustomPayload packet) {
		DataInputStream inputStream = new DataInputStream(
				new ByteArrayInputStream(packet.data));
		try {
			inputStream.readInt();
			for (int i = 0; i < MetalAmounts.length; i++)
				MetalAmounts[i] = inputStream.readInt();
			Dirty = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Clear the type byte.
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
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
		compound.setCompoundTag(IDENTIFIER, nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
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
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
		if (this.selected > 4 || this.selected < 0) {
			this.selected = 0;
		}
		Dirty = true;
	}

}
