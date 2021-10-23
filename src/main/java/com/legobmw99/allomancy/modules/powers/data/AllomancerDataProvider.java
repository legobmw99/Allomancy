package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AllomancerDataProvider implements ICapabilitySerializable<CompoundTag> {

    private final DefaultAllomancerData data = new DefaultAllomancerData();
    private final LazyOptional<IAllomancerData> dataOptional = LazyOptional.of(() -> this.data);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return AllomancerCapability.PLAYER_CAP.orEmpty(cap, this.dataOptional.cast());
    }

    @Override
    public CompoundTag serializeNBT() {
        if (AllomancerCapability.PLAYER_CAP == null) {
            return new CompoundTag();
        } else {
            return this.data.save();
        }

    }

    @Override
    public void deserializeNBT(CompoundTag allomancy_data) {
        if (AllomancerCapability.PLAYER_CAP != null) {
            this.data.load(allomancy_data);
        }
    }

    public void invalidate() {
        this.dataOptional.invalidate();
    }
}
