package common.legobmw99.allomancy;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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
	public static final int matCopper = 4;
	public static final int matBronze = 5;
	public static final int matZinc = 6;
	public static final int matBrass = 7;

	public static boolean isMistborn = false;

	public static final String IDENTIFIER = "Allomancy_Data";
	public boolean Dirty = true;
	public int selected = 0;

	public int damageStored = 0;
	public int[] BurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
	public int[] MaxBurnTime = { 1800, 1800, 3600, 1500, 1800, 1800, 2400, 2400 };
	public int[] MetalAmounts = { 0, 0, 0, 0, 0, 0, 0, 0 };
	public boolean[] MetalBurning = { false, false, false, false, false, false,
			false, false };

	public AllomancyData(EntityPlayer Player) {
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

			if (this.MetalAmounts[Material] != 0) {
				this.MetalBurning[Material] = value;
			} else {
				this.MetalBurning[Material] = false;
			}

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
			for (int i = 0; i < this.MetalAmounts.length; i++) {
				this.MetalAmounts[i] = inputStream.readInt();
			}
			this.Dirty = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Clear the type byte.
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
		compound.setCompoundTag(IDENTIFIER, nbt);
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
		// TODO Auto-generated method stub

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
