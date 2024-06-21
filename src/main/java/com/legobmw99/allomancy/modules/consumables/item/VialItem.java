package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.data.AllomancerData;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

import static com.legobmw99.allomancy.modules.consumables.ConsumeSetup.FLAKE_STORAGE;

public class VialItem extends Item {

    public static final CustomModelData FILLED_MODEL_DATA = new CustomModelData(1);

    public VialItem() {
        super(new Item.Properties().stacksTo(32).rarity(Rarity.COMMON));
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity livingEntity) {
        FlakeStorage storage = stack.get(FLAKE_STORAGE);

        if (storage == null || !livingEntity.hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
            return stack;
        }


        var data = livingEntity.getData(AllomancerAttachment.ALLOMANCY_DATA);
        for (Metal mt : Metal.values()) {
            if (storage.contains(mt) && data.getStored(mt) < AllomancerData.MAX_STORAGE) {
                data.incrementStored(mt);
            }
        }


        if (!((Player) (livingEntity)).getAbilities().instabuild) {
            stack.shrink(1);

            if (!((Player) livingEntity).getInventory().add(new ItemStack(ConsumeSetup.VIAL.get(), 1))) {
                world.addFreshEntity(
                        new ItemEntity(world, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                                       new ItemStack(ConsumeSetup.VIAL.get(), 1)));
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
        return 6;
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);

        var data = playerIn.getData(AllomancerAttachment.ALLOMANCY_DATA);
        //If all the ones being filled are full, don't allow
        int filling = 0, full = 0;
        FlakeStorage storage = itemStackIn.get(FLAKE_STORAGE);
        if (storage != null) {
            for (Metal mt : Metal.values()) {
                if (storage.contains(mt)) {
                    filling++;
                    if (data.getStored(mt) >= AllomancerData.MAX_STORAGE) {
                        full++;
                    }
                }
            }

            if (filling != full) {
                playerIn.startUsingItem(hand);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStackIn);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStackIn);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack,
                                Item.TooltipContext ctx,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, ctx, tooltip, flagIn);

        FlakeStorage storage = stack.get(FLAKE_STORAGE);
        if (storage != null) {
            boolean full_display = Screen.hasShiftDown();
            int count = 0;
            for (Metal mt : Metal.values()) {


                if (storage.contains(mt)) {
                    count++;
                    if (full_display) {
                        MutableComponent metal =
                                ItemDisplay.addColorToText("metals." + mt.getName(), ChatFormatting.GRAY);
                        tooltip.add(metal);
                    }
                }
            }
            if (!full_display) {
                MutableComponent lcount =
                        ItemDisplay.addColorToText("item.allomancy.vial.lore_count", ChatFormatting.GRAY, count);
                tooltip.add(lcount);
                MutableComponent linst =
                        ItemDisplay.addColorToText("item.allomancy.vial.lore_inst", ChatFormatting.GRAY);
                tooltip.add(linst);

            }
        }
    }

    public static void fillVial(ItemStack stack, FlakeStorage storage) {
        stack.set(FLAKE_STORAGE, storage);
        if (storage == null) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, null);
            stack.set(DataComponents.RARITY, Rarity.COMMON);
        } else {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, FILLED_MODEL_DATA);
            stack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        }

    }

    /**
     * TEMPORARY: Used to port pre-1.20.5 worlds to post.
     * Loads custom NBT data and convets it to the data component.
     */
    @Override
    public void verifyComponentsAfterLoad(ItemStack pStack) {
        super.verifyComponentsAfterLoad(pStack);
        if (pStack.has(FLAKE_STORAGE)) {
            return;
        }

        CustomData customData = pStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains("steel")) {

            CompoundTag tag = customData.copyTag();
            Allomancy.LOGGER.info("Found old custom item data for vial: {}", tag.toString());
            FlakeStorage.Mutable storage = new FlakeStorage.Mutable();
            for (Metal mt : Metal.values()) {
                if (tag.getBoolean(mt.getName())) {
                    storage.add(mt);
                }
                tag.remove(mt.getName());
            }
            fillVial(pStack, storage.toImmutable());
            pStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
}
