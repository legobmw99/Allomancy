package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;

public class AllomancerData implements IAllomancerData {
    private static final int[] MAX_BURN_TIME =
            {1800, 1800, 3600, 600, 1800, 1800, 2400, 1600, 100, 20, 300, 40, 1000, 10000, 3600, 160};

    // serialized to disk
    private final EnumMap<Metal, Boolean> allomantic_powers;
    private final EnumMap<Metal, Integer> metal_amounts;
    private final EnumMap<Metal, Boolean> burning_metals;
    private GlobalPos spawn_pos = null;
    private GlobalPos seeking_pos = null;
    // available on the client but not serialized
    private int enhanced_time = 0;

    // only available on the server
    private final int[] burn_time = Arrays.copyOf(MAX_BURN_TIME, Metal.values().length);
    private int damage_stored = 0;

    public AllomancerData() {
        allomantic_powers = new EnumMap<>(Metal.class);
        metal_amounts = new EnumMap<>(Metal.class);
        burning_metals = new EnumMap<>(Metal.class);

        for (Metal mt : Metal.values()) {
            allomantic_powers.put(mt, false);
            metal_amounts.put(mt, 0);
            burning_metals.put(mt, false);
        }
    }


    private AllomancerData(EnumMap<Metal, Boolean> powers,
                           EnumMap<Metal, Boolean> burning,
                           EnumMap<Metal, Integer> amounts,
                           Pair<Optional<GlobalPos>, Optional<GlobalPos>> positions) {
        this.allomantic_powers = powers;
        this.burning_metals = burning;
        this.metal_amounts = amounts;

        positions.getFirst().ifPresent(spawn_pos -> this.spawn_pos = spawn_pos);
        positions.getSecond().ifPresent(seeking_pos -> this.seeking_pos = seeking_pos);
    }

    // a bit esoteric to match the previous nbt serialization
    public static final MapCodec<AllomancerData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(metalMapCodec(Codec.BOOL).fieldOf("abilities").forGetter(data -> data.allomantic_powers),
                   metalMapCodec(Codec.BOOL).fieldOf("metal_burning").forGetter(data -> data.burning_metals),
                   metalMapCodec(Codec.INT).fieldOf("metal_storage").forGetter(data -> data.metal_amounts), Codec
                           .pair(GlobalPos.CODEC.lenientOptionalFieldOf("spawn_pos").codec(),
                                 GlobalPos.CODEC.lenientOptionalFieldOf("seeking_pos").codec())
                           .lenientOptionalFieldOf("positions", new Pair<>(Optional.empty(), Optional.empty()))
                           .forGetter(data -> new Pair<>(Optional.ofNullable(data.spawn_pos),
                                                         Optional.ofNullable(data.seeking_pos)))

            )
            .apply(instance, AllomancerData::new));

    public static final StreamCodec<FriendlyByteBuf, AllomancerData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AllomancerData decode(FriendlyByteBuf buffer) {
            var data = new AllomancerData();
            for (Metal mt : Metal.values()) {
                data.allomantic_powers.put(mt, buffer.readBoolean());
            }
            for (Metal mt : Metal.values()) {
                data.burning_metals.put(mt, buffer.readBoolean());
            }
            for (Metal mt : Metal.values()) {
                data.metal_amounts.put(mt, buffer.readInt());
            }

            if (buffer.readBoolean()) {
                data.spawn_pos = buffer.readGlobalPos();
            }

            if (buffer.readBoolean()) {
                data.seeking_pos = buffer.readGlobalPos();
            }

            data.enhanced_time = buffer.readInt();

            return data;
        }

        @Override
        public void encode(FriendlyByteBuf buffer, AllomancerData data) {
            for (Metal mt : Metal.values()) {
                buffer.writeBoolean(data.allomantic_powers.getOrDefault(mt, false));
            }
            for (Metal mt : Metal.values()) {
                buffer.writeBoolean(data.burning_metals.getOrDefault(mt, false));
            }
            for (Metal mt : Metal.values()) {
                buffer.writeInt(data.metal_amounts.getOrDefault(mt, 0));
            }

            if (data.spawn_pos != null) {
                buffer.writeBoolean(true);
                buffer.writeGlobalPos(data.spawn_pos);
            } else {
                buffer.writeBoolean(false);
            }

            if (data.seeking_pos != null) {
                buffer.writeBoolean(true);
                buffer.writeGlobalPos(data.seeking_pos);
            } else {
                buffer.writeBoolean(false);
            }

            buffer.writeInt(data.enhanced_time);
        }
    };


    public boolean tickBurning() {
        boolean sync = false;
        for (Metal metal : Metal.values()) {
            if (this.isBurning(metal)) {
                if (!this.hasPower(metal)) {
                    // put out any metals that the player shouldn't be able to burn
                    this.setBurning(metal, false);
                    sync = true;
                } else {
                    this.burn_time[metal.getIndex()]--;

                    if (this.burn_time[metal.getIndex()] <= 0) {
                        if (this.getStored(metal) <= 0) {
                            this.setBurning(metal, false);
                        } else {
                            this.decrementStored(metal);
                        }
                        sync = true;
                        this.burn_time[metal.getIndex()] = MAX_BURN_TIME[metal.getIndex()];
                    }
                }
            }
        }
        return sync;
    }

    public boolean hasPower(Metal metal) {
        return this.allomantic_powers.getOrDefault(metal, false);
    }

    public int getPowerCount() {
        int count = 0;
        for (boolean power : this.allomantic_powers.values()) {
            if (power) {
                count++;
            }
        }
        return count;
    }

    public Metal[] getPowers() {
        return Arrays.stream(Metal.values()).filter(this::hasPower).toArray(Metal[]::new);
    }

    public boolean isMistborn() {
        for (boolean power : this.allomantic_powers.values()) {
            if (!power) {
                return false;
            }
        }
        return true;
    }

    public void setMistborn() {
        for (Metal mt : Metal.values()) {
            this.allomantic_powers.put(mt, true);
        }
    }

    public boolean isUninvested() {
        for (boolean power : this.allomantic_powers.values()) {
            if (power) {
                return false;
            }
        }
        return true;
    }

    public void setUninvested() {
        for (Metal mt : Metal.values()) {
            this.allomantic_powers.put(mt, false);
        }
    }

    public void addPower(Metal metal) {
        this.allomantic_powers.put(metal, true);
    }

    public void revokePower(Metal metal) {
        this.allomantic_powers.put(metal, false);
    }

    public boolean isBurning(Metal metal) {
        return this.burning_metals.getOrDefault(metal, false);
    }

    public void setBurning(Metal metal, boolean metalBurning) {
        this.burning_metals.put(metal, metalBurning);
    }

    public int getStored(Metal metal) {
        return this.metal_amounts.getOrDefault(metal, 0);
    }

    public void incrementStored(Metal metal) {
        int current = this.metal_amounts.getOrDefault(metal, 0);
        if (current < MAX_STORAGE) {
            this.metal_amounts.put(metal, current + 1);
        }
    }

    public void decrementStored(Metal metal) {
        int current = this.metal_amounts.getOrDefault(metal, 0);
        if (current > 0) {
            this.metal_amounts.put(metal, current - 1);
        }
    }

    public void drainMetals(Metal... metals) {
        for (Metal mt : metals) {
            this.metal_amounts.put(mt, 0);
            this.burn_time[mt.getIndex()] = MAX_BURN_TIME[mt.getIndex()];
            this.setBurning(mt, false);
        }
    }

    public int getDamageStored() {
        return this.damage_stored;
    }

    public void setDamageStored(int damageStored) {
        this.damage_stored = damageStored;
    }

    public void setSpawnLoc(BlockPos pos, ResourceKey<Level> dim) {
        if (pos != null && dim != null) {
            this.spawn_pos = GlobalPos.of(dim, pos);
        }
    }

    public @Nullable GlobalPos getSpawnLoc() {
        return this.spawn_pos;
    }

    public void setSpecialSeekingLoc(BlockPos pos, ResourceKey<Level> dim) {
        if (pos != null && dim != null) {
            this.seeking_pos = GlobalPos.of(dim, pos);
        } else {
            this.seeking_pos = null;
        }
    }

    public @Nullable GlobalPos getSpecialSeekingLoc() {
        return this.seeking_pos;
    }

    public void decrementEnhanced() {
        if (isEnhanced()) {
            this.enhanced_time--;
        }
    }

    public boolean isEnhanced() {
        return this.enhanced_time > 0;
    }

    public void setEnhanced(int time) {
        this.enhanced_time = time;
    }

    private static <V> Codec<EnumMap<Metal, V>> metalMapCodec(Codec<V> v) {
        return Codec.unboundedMap(Metal.CODEC, v).xmap(EnumMap::new, Function.identity());
    }
}

