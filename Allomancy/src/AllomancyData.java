import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


public class AllomancyData implements IExtendedEntityProperties {

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
	
	public static AllomancyData forPlayer(Entity player)
    {
        return (AllomancyData)player.getExtendedProperties(IDENTIFIER);
    }
    
	
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		compound.setInteger("iron", Iron);
		compound.setInteger("steel", Steel);
		compound.setInteger("tin",Tin);
		compound.setInteger("pewter",Pewter);
		compound.setInteger("zinc",Zinc);
		compound.setInteger("bronze",Bronze);
		compound.setInteger("copper",Copper);
		compound.setInteger("brass",Brass);
		compound.setInteger("selected",selected);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		Iron = compound.getInteger("iron");
		Steel= compound.getInteger("steel");
		Tin= compound.getInteger("tin");
		Pewter = compound.getInteger("pewter");
		Zinc = compound.getInteger("zinc");
		Bronze = compound.getInteger("bronze");
		Copper = compound.getInteger("copper");
		Brass = compound.getInteger("brass");
		selected = compound.getInteger("selected");		
		
	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

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
		if (selected > 4 || selected < 0) 
		{
			selected = 0;
		}
		Dirty = true;
	}

}
