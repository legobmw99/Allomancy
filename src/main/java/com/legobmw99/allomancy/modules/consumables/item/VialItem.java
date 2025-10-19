package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static com.legobmw99.allomancy.modules.consumables.ConsumeSetup.FLAKE_STORAGE;

public class VialItem extends Item {
    private static final FoodProperties vial_food =
            new FoodProperties.Builder().alwaysEdible().saturationModifier(0).nutrition(0).build();

    private static final Consumable vial_consumable = Consumable
            .builder()
            .consumeSeconds(0.3f)
            .animation(ItemUseAnimation.DRINK)
            .sound(SoundEvents.GENERIC_DRINK)
            .hasConsumeParticles(false)
            .build();

    public VialItem(Item.Properties props) {
        super(props.stacksTo(32).food(vial_food, vial_consumable).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);

        var data = AllomancerAttachment.get(playerIn);
        //If all the ones being filled are full, don't allow
        int filling = 0, full = 0;
        FlakeStorage storage = itemStackIn.get(FLAKE_STORAGE);
        if (storage != null) {
            for (Metal mt : Metal.values()) {
                if (storage.contains(mt)) {
                    filling++;
                    if (data.getStored(mt) >= IAllomancerData.MAX_STORAGE) {
                        full++;
                    }
                }
            }

            if (filling != full) {
                playerIn.startUsingItem(hand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    public static void fillVial(ItemStack stack, @Nullable FlakeStorage storage) {
        stack.set(FLAKE_STORAGE, storage);
        if (storage == null) {
            stack.set(DataComponents.RARITY, Rarity.COMMON);
        } else {
            stack.set(DataComponents.USE_REMAINDER, new UseRemainder(ConsumeSetup.VIAL.toStack()));
            stack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        }
    }
}
