package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class AllomancerCapability {

    @CapabilityInject(IAllomancerData.class)
    public static final Capability<IAllomancerData> PLAYER_CAP = null;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    public static void register() {
        CapabilityManager.INSTANCE.register(IAllomancerData.class, new Storage(), DefaultAllomancerData::new);
    }

    public static class Storage implements Capability.IStorage<IAllomancerData> {

        @Override
        public INBT writeNBT(Capability<IAllomancerData> capability, IAllomancerData data, Direction side) {
            CompoundNBT allomancy_data = new CompoundNBT();

            CompoundNBT abilities = new CompoundNBT();
            for (Metal mt : Metal.values()) {
                abilities.putBoolean(mt.getName(), data.hasPower(mt));
            }
            allomancy_data.put("abilities", abilities);


            CompoundNBT metal_storage = new CompoundNBT();
            for (Metal mt : Metal.values()) {
                metal_storage.putInt(mt.getName(), data.getAmount(mt));
            }
            allomancy_data.put("metal_storage", metal_storage);


            CompoundNBT metal_burning = new CompoundNBT();
            for (Metal mt : Metal.values()) {
                metal_burning.putBoolean(mt.getName(), data.isBurning(mt));
            }
            allomancy_data.put("metal_burning", metal_burning);

            CompoundNBT position = new CompoundNBT();
            BlockPos death_pos = data.getDeathLoc();
            if (death_pos != null) {
                position.putString("death_dimension", data.getDeathDim().location().toString());
                position.putInt("death_x", death_pos.getX());
                position.putInt("death_y", death_pos.getY());
                position.putInt("death_z", death_pos.getZ());
            }
            BlockPos spawn_pos = data.getSpawnLoc();
            if (spawn_pos != null) {
                position.putString("spawn_dimension", data.getSpawnDim().location().toString());
                position.putInt("spawn_x", spawn_pos.getX());
                position.putInt("spawn_y", spawn_pos.getY());
                position.putInt("spawn_z", spawn_pos.getZ());
            }
            allomancy_data.put("position", position);

            return allomancy_data;
        }

        @Override
        public void readNBT(Capability<IAllomancerData> capability, IAllomancerData data, Direction side, INBT nbt) {
            CompoundNBT allomancy_data = (CompoundNBT) nbt;
            CompoundNBT abilities = (CompoundNBT) allomancy_data.get("abilities");
            for (Metal mt : Metal.values()) {
                if (abilities.getBoolean(mt.getName())) {
                    data.addPower(mt);
                } else {
                    data.revokePower(mt);
                }
            }

            CompoundNBT metal_storage = (CompoundNBT) allomancy_data.get("metal_storage");
            for (Metal mt : Metal.values()) {
                data.setAmount(mt, metal_storage.getInt(mt.getName()));
            }

            CompoundNBT metal_burning = (CompoundNBT) allomancy_data.get("metal_burning");
            for (Metal mt : Metal.values()) {
                data.setBurning(mt, metal_burning.getBoolean(mt.getName()));
            }

            CompoundNBT position = (CompoundNBT) allomancy_data.get("position");
            if (position.contains("death_dimension")) {
                data.setDeathLoc(new BlockPos(position.getInt("death_x"), position.getInt("death_y"), position.getInt("death_z")), position.getString("death_dimension"));
            }
            if (position.contains("spawn_dimension")) {
                data.setSpawnLoc(new BlockPos(position.getInt("spawn_x"), position.getInt("spawn_y"), position.getInt("spawn_z")), position.getString("spawn_dimension"));
            }


        }
    }
}
