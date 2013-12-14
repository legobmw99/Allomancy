package com.entropicdreams.darva;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.entropicdreams.darva.handlers.PacketHandler;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


public class AllomancyData implements IExtendedEntityProperties {

	public enum Allomantic_Material
	{
		iron,
		steel,
		tin,
		pewter,
		zinc,
		bronze,
		copper,
		brass,
		
	}
	
	public static final String IDENTIFIER = "Allomancy_Data";
	public boolean Dirty = true;
	
	private int Iron = 0;
	private int Steel= 0;
	private int Tin= 0;
	private int Pewter= 0;
	private int Zinc= 0;
	private int Bronze= 0;
	private int Copper= 0;
	private int Brass= 0;
	private int selected = 0;
	private boolean bIron = false;
	private boolean bSteel = false;
	private boolean bTin = false;
	private boolean bPewter = false;
	private boolean bZinc = false;
	private boolean bBronze = false;
	private boolean bCopper = false;
	private boolean bBrass = false;
	
	private final EntityPlayer player;
	
	public AllomancyData(EntityPlayer Player)
	{
		player = Player;
	}
	public static AllomancyData forPlayer(Entity player)
    {
        return (AllomancyData)player.getExtendedProperties(IDENTIFIER);
    }
    
	public void updateData(Packet250CustomPayload packet)
	{
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			inputStream.readInt();
			Brass = inputStream.readInt();
			Bronze = inputStream.readInt();
			Copper = inputStream.readInt();
			Iron = inputStream.readInt();
			Pewter = inputStream.readInt();
			selected = inputStream.readInt();
			Steel = inputStream.readInt();
			Tin = inputStream.readInt();
			Zinc = inputStream.readInt();
			Dirty = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Clear the type byte.
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("iron", Iron);
		nbt.setInteger("steel", Steel);
		nbt.setInteger("tin",Tin);
		nbt.setInteger("pewter",Pewter);
		nbt.setInteger("zinc",Zinc);
		nbt.setInteger("bronze",Bronze);
		nbt.setInteger("copper",Copper);
		nbt.setInteger("brass",Brass);
		nbt.setInteger("selected",selected);
		compound.setCompoundTag(IDENTIFIER, nbt);
		System.out.println("wrote");
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		System.out.println("read");
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = compound.getCompoundTag(IDENTIFIER);
		Iron = nbt.getInteger("iron");
		Steel= nbt.getInteger("steel");
		Tin= nbt.getInteger("tin");
		Pewter = nbt.getInteger("pewter");
		Zinc = nbt.getInteger("zinc");
		Bronze = nbt.getInteger("bronze");
		Copper = nbt.getInteger("copper");
		Brass = nbt.getInteger("brass");
		selected = nbt.getInteger("selected");		
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}



	public boolean isbIron() {
		return bIron;
	}
	public void setbIron(boolean bIron) {
		this.bIron = bIron;
	}
	public boolean isbSteel() {
		return bSteel;
	}
	public void setbSteel(boolean bSteel) {
		this.bSteel = bSteel;
	}
	public boolean isbTin() {
		return bTin;
	}
	public void setbTin(boolean bTin) {
		this.bTin = bTin;
	}
	public boolean isbPewter() {
		return bPewter;
	}
	public void setbPewter(boolean bPewter) {
		this.bPewter = bPewter;
	}
	public boolean isbZinc() {
		return bZinc;
	}
	public void setbZinc(boolean bZinc) {
		this.bZinc = bZinc;
	}
	public boolean isbBronze() {
		return bBronze;
	}
	public void setbBronze(boolean bBronze) {
		this.bBronze = bBronze;
	}
	public boolean isbCopper() {
		return bCopper;
	}
	public void setbCopper(boolean bCopper) {
		this.bCopper = bCopper;
	}
	public boolean isbBrass() {
		return bBrass;
	}
	public void setbBrass(boolean bBrass) {
		this.bBrass = bBrass;
	}
	public EntityPlayer getPlayer() {
		return player;
	}
	public int getIron() {
		return Iron;
	}



	public void setIron(int iron) {
		Iron = iron;
		Dirty = true;
	}



	public int getSteel() {
		return Steel;
	}



	public void setSteel(int steel) {
		Steel = steel;
		Dirty = true;
	}



	public int getTin() {
		return Tin;
	}



	public void setTin(int tin) {
		Tin = tin;
		Dirty = true;
	}



	public int getPewter() {
		return Pewter;
	}



	public void setPewter(int pewter) {
		Pewter = pewter;
		Dirty = true;
	}



	public int getZinc() {
		return Zinc;
	}



	public void setZinc(int zinc) {
		Zinc = zinc;
		Dirty = true;
	}



	public int getBronze() {
		return Bronze;
	}



	public void setBronze(int bronze) {
		Bronze = bronze;
		Dirty = true;
	}



	public int getCopper() {
		return Copper;
	}



	public void setCopper(int copper) {
		Copper = copper;
		Dirty = true;
	}



	public int getBrass() {
		return Brass;
	}



	public void setBrass(int brass) {
		Brass = brass;
		Dirty = true;
	}



	public int getSelected() {
		return selected;
	}



	public void setSelected(int selected) {
		this.selected = selected;
		if (this.selected > 4 || this.selected < 0) 
		{
			this.selected = 0;
		}
		Dirty = true;
	}

}
