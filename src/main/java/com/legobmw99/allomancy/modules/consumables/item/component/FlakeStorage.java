package com.legobmw99.allomancy.modules.consumables.item.component;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.util.ItemDisplay;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Consumer;

public final class FlakeStorage implements TooltipProvider {

    private final EnumSet<Metal> flakes;

    private FlakeStorage(EnumSet<Metal> flakes) {
        this.flakes = flakes;
    }

    public boolean contains(Metal mt) {
        return this.flakes.contains(mt);
    }

    public static final Codec<FlakeStorage> CODEC = Codec.list(Metal.CODEC).xmap(metals -> {
        var storage = new FlakeStorage.Mutable();
        for (Metal mt : metals) {
            storage.add(mt);
        }
        return storage.toImmutable();
    }, flakeStorage -> flakeStorage.flakes.stream().toList());


    public static final StreamCodec<ByteBuf, FlakeStorage> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public FlakeStorage decode(ByteBuf buf) {
            Mutable storage = new Mutable();
            for (Metal mt : Metal.values()) {
                if (buf.readBoolean()) {
                    storage.add(mt);
                }
            }
            return storage.toImmutable();
        }

        @Override
        public void encode(ByteBuf buf, FlakeStorage storage) {
            for (Metal mt : Metal.values()) {
                buf.writeBoolean(storage.contains(mt));
            }
        }
    };

    @Override
    public int hashCode() {
        return this.flakes.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof FlakeStorage flakeStorage && this.flakes.equals(flakeStorage.flakes);
        }
    }

    @Override
    public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {

        if (Screen.hasShiftDown()) {
            for (Metal mt : Metal.values()) {
                if (this.contains(mt)) {
                    pTooltipAdder.accept(ItemDisplay.addColorToText("metals." + mt.getName(), ChatFormatting.GRAY));
                }
            }
        } else {
            int count = (int) Arrays.stream(Metal.values()).filter(this::contains).count();
            pTooltipAdder.accept(
                    ItemDisplay.addColorToText("item.allomancy.vial.lore_count", ChatFormatting.GRAY, count));
            pTooltipAdder.accept(ItemDisplay.addColorToText("item.allomancy.vial.lore_inst", ChatFormatting.GRAY));
        }

    }

    public static class Mutable {
        private final EnumSet<Metal> flakes = EnumSet.noneOf(Metal.class);

        public void add(Metal mt) {
            this.flakes.add(mt);
        }

        public void addAll(FlakeStorage other) {
            if (other != null) {
                this.flakes.addAll(other.flakes);
            }
        }

        public FlakeStorage toImmutable() {
            if (this.flakes.isEmpty()) {
                return null;
            }
            return new FlakeStorage(this.flakes);
        }
    }

}
