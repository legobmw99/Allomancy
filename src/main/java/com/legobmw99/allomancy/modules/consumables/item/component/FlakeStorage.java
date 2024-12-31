package com.legobmw99.allomancy.modules.consumables.item.component;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.data.AllomancerData;
import com.legobmw99.allomancy.util.ItemDisplay;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.function.Consumer;

import static com.legobmw99.allomancy.modules.consumables.ConsumeSetup.FLAKE_STORAGE;

public final class FlakeStorage implements TooltipProvider, ConsumableListener {

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
    public void addToTooltip(Item.TooltipContext ctx, Consumer<Component> tooltip, TooltipFlag flag) {
        int count = 0;
        Metal last = Metal.IRON;
        for (Metal mt : Metal.values()) {
            if (this.contains(mt)) {
                count++;
                last = mt;
            }
        }
        switch (count) {
            case 0 -> {
            }
            case 1 -> tooltip.accept(
                    ItemDisplay.addColorToText("allomancy.flake_storage.lore_single", ChatFormatting.GRAY,
                                               Component.translatable("metals." + last.getName()).getString()));

            default -> {
                if (Screen.hasShiftDown()) {
                    for (Metal mt : Metal.values()) {
                        if (this.contains(mt)) {
                            tooltip.accept(ItemDisplay.addColorToText("metals." + mt.getName(), ChatFormatting.GRAY));
                        }
                    }
                } else {
                    tooltip.accept(
                            ItemDisplay.addColorToText("allomancy.flake_storage.lore_count", ChatFormatting.GRAY,
                                                       count));
                    tooltip.accept(
                            ItemDisplay.addColorToText("allomancy.flake_storage.lore_inst", ChatFormatting.GRAY));
                }
            }
        }
    }

    @Override
    public void onConsume(Level level, LivingEntity entity, ItemStack stack, Consumable consumable) {
        FlakeStorage storage = stack.get(FLAKE_STORAGE);

        if (storage == null || !entity.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            return;
        }
        var data = entity.getData(AllomancerAttachment.ALLOMANCY_DATA);
        if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE && storage.contains(Metal.GOLD)) {
            for (int i = 0; i < AllomancerData.MAX_STORAGE; i++) {
                data.incrementStored(Metal.GOLD);
            }
        }
        for (Metal mt : Metal.values()) {
            if (storage.contains(mt)) {
                data.incrementStored(mt);
            }
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
