package invoker54.magefight.items.curios;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class OwlSightTrinket extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public OwlSightTrinket(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.level.isClientSide) return;

        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;

        //If they have night vision don't bother giving them night vision again.
        if (player.hasEffect(Effects.NIGHT_VISION)) return;

        if (player.getFoodData().getFoodLevel() < 6) return;

        //This will take away 2 food (1 drumstick) from the player
        player.causeFoodExhaustion(8);

        //Now give the player night vision for 1 minute
        player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 1200));

        ICurioItem.super.curioTick(identifier, index, livingEntity, stack);
    }
}
