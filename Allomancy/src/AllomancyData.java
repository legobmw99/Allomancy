import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;


public class AllomancyData implements IExtendedEntityProperties {

	public static final String IDENTIFIER = "Allomancy_Data";
	public boolean Dirty = true;
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(Entity entity, World world) {
		// TODO Auto-generated method stub

	}

}
