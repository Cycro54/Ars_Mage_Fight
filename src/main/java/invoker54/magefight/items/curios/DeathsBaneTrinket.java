package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class DeathsBaneTrinket extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public DeathsBaneTrinket(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        return super.getShareTag(stack);
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class CurioEvents{

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onLethalDamage(LivingDamageEvent event){
            if (event.isCanceled()) return;
            LivingEntity hurtEntity = event.getEntityLiving();
            if (!(hurtEntity instanceof PlayerEntity)) return;
            if (hurtEntity.getHealth() > event.getAmount()) return;
            //Now look for the Explosive Blood Charm
            CuriosUtil.getAllWornItems(hurtEntity).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    ItemStack curioStack = e.getStackInSlot(i);
                    Item item = curioStack.getItem();
                    //Find the Explosive Blood Charm
                    if (item instanceof DeathsBaneTrinket){
                        EffectInstance effect = hurtEntity.getEffect(EffectInit.DEATHS_BANE_EFFECT);
                        //If it's on cooldown (tier is higher than 1), allow it to pass.
                        if (effect != null && effect.getAmplifier() >= 1) return;
                        else if (effect == null){
                            event.setCanceled(true);
                            hurtEntity.setHealth(1);
                            hurtEntity.addEffect(new EffectInstance(EffectInit.DEATHS_BANE_EFFECT, 6 * 20, 0));
                        }

                        //This should only run if the effect amp is 0
                        if (effect != null && effect.getAmplifier() == 0) event.setAmount(0);
                    }
                }
            });
        }
    }
}
