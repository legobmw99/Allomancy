package com.legobmw99.allomancy.modules.consumables.item;

import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.setup.AllomancySetup;
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
    private static final Food lerasium = new Food.Builder().fastToEat().setAlwaysEdible().saturation(0).hunger(0).build();

    public LerasiumItem() {
        super(AllomancySetup.createStandardItemProperties().rarity(Rarity.EPIC).maxStackSize(1).food(lerasium));
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        ItemStack itemStackIn = player.getHeldItem(hand);
        if (!cap.isMistborn()) {
            player.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);

        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity livingEntity) {
        AllomancyCapability cap = AllomancyCapability.forPlayer((PlayerEntity) livingEntity);
        double x = livingEntity.getPositionVec().getX();
        double y = livingEntity.getPositionVec().getY() + 3;
        double z = livingEntity.getPositionVec().getZ();
        if (!cap.isMistborn()) {
            cap.setMistborn();
        }
        //Fancy shmancy effects
        LightningBoltEntity lightning = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.func_233623_a_(true); //effectOnly
        lightning.func_233576_c_(new Vector3d(x, y, z)); // set position - see TridentEntity
        world.addEntity(lightning);
        livingEntity.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
                20, 0, true, false));

        return super.onItemUseFinish(stack, world, livingEntity);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ITextComponent lore = AllomancySetup.addColorToText("item.allomancy.lerasium_nugget.lore", TextFormatting.LIGHT_PURPLE);
        tooltip.add(lore);

    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 4;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}