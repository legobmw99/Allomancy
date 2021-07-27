package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AllomancerDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final DefaultAllomancerData data = new DefaultAllomancerData();
    private final LazyOptional<IAllomancerData> dataOptional = LazyOptional.of(() -> this.data);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return AllomancerCapability.PLAYER_CAP.orEmpty(cap, this.dataOptional.cast());
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (AllomancerCapability.PLAYER_CAP == null) {
            return new CompoundNBT();
        } else {
            return (CompoundNBT) AllomancerCapability.PLAYER_CAP.writeNBT(this.data, null);
        }

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (AllomancerCapability.PLAYER_CAP != null) {
            AllomancerCapability.PLAYER_CAP.readNBT(this.data, null, nbt);
        }
    }

    public void invalidate() {
        this.dataOptional.invalidate();
    }
}
