package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.IAllomancyData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AllomancyDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final DefaultAllomancyData data = new DefaultAllomancyData();
    private final LazyOptional<IAllomancyData> dataOptional = LazyOptional.of(() -> this.data);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.dataOptional.cast();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (AllomancyCapability.PLAYER_CAP == null) {
            return new CompoundNBT();
        } else {
            return (CompoundNBT) AllomancyCapability.PLAYER_CAP.writeNBT(this.data, null);
        }

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (AllomancyCapability.PLAYER_CAP != null) {
            AllomancyCapability.PLAYER_CAP.readNBT(this.data, null, nbt);
        }
    }

    public void invalidate() {
        this.dataOptional.invalidate();
    }
}
