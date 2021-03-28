package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LerasiumItem extends Item {
    private static final Food lerasium = new Food.Builder().fast().alwaysEat().saturationMod(0).nutrition(0).build();

    public LerasiumItem() {
        super(Allomancy.createStandardItemProperties().rarity(Rarity.EPIC).stacksTo(1).food(lerasium));
    }


    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        ItemStack itemStackIn = player.getItemInHand(hand);
        if (!cap.isMistborn()) {
            player.startUsingItem(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);

        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity livingEntity) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(livingEntity);
        double x = livingEntity.position().x();
        double y = livingEntity.position().y() + 3;
        double z = livingEntity.position().z();
        if (!cap.isMistborn()) {
            cap.setMistborn();
        }
        //Fancy shmancy effects
        LightningBoltEntity lightning = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setVisualOnly(true);
        lightning.moveTo(new Vector3d(x, y, z)); // see TridentEntity
        world.addFreshEntity(lightning);
        livingEntity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 20, 0, true, false));

        return super.finishUsingItem(stack, world, livingEntity);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ITextComponent lore = Allomancy.addColorToText("item.allomancy.lerasium_nugget.lore", TextFormatting.LIGHT_PURPLE);
        tooltip.add(lore);

    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 4;
    }

    @Override
    public boolean isFoil(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}