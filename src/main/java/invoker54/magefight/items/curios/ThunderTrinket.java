package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ThunderTrinket extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public ThunderTrinket(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class ThunderTrinketEvents{

        //This will be used to add the shock effect
        @SubscribeEvent
        public static void onTriggerShock(LivingHurtEvent event){
            if (event.isCanceled()) return;
            if (event.getSource() == null) return;
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity)) return;
            LivingEntity attacker = (LivingEntity) source.getEntity();
            LivingEntity hitEntity = event.getEntityLiving();

            //Now look for the Thunder Trinket
            CuriosUtil.getAllWornItems(attacker).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the Thunder Trinket
                    if (item instanceof ThunderTrinket) {
                        //This is for the entity hit
                        int amp = 0;
                        if (hitEntity.hasEffect(EffectInit.SHOCK_EFFECT)) amp += hitEntity.getEffect(EffectInit.SHOCK_EFFECT).getAmplifier() + 1;
                        amp = Math.min(2, amp);
                        hitEntity.addEffect(new EffectInstance(EffectInit.SHOCK_EFFECT, (20 * 6), amp));

                        //This is for the attacker
                        amp = 0;
//                        if (attacker.hasEffect(EffectInit.SHOCK_EFFECT)) amp += attacker.getEffect(EffectInit.SHOCK_EFFECT).getAmplifier() + 1;
//                        amp = Math.min(2, amp);
                        attacker.addEffect(new EffectInstance(EffectInit.SHOCK_EFFECT, (20 * 6), amp));
                    }
                }
            });
        }

        //This will be used to increase damage taken.
        @SubscribeEvent
        public static void onDamage(LivingDamageEvent event){
            if (event.isCanceled()) return;

            LivingEntity hitEntity = event.getEntityLiving();
            
            if (!hitEntity.hasEffect(EffectInit.SHOCK_EFFECT)) return;

            float extraDamage = event.getAmount() * ((hitEntity.getEffect(EffectInit.SHOCK_EFFECT).getAmplifier() + 1) * 0.25F);
            event.setAmount(event.getAmount() + extraDamage);
        }
    }
}
