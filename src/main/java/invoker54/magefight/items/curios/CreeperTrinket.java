package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectEnderChest;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Random;

public class CreeperTrinket extends Item implements ICurioItem, IManaEquipment {
    private static final Logger LOGGER = LogManager.getLogger();

    public CreeperTrinket(Properties properties) {
        super(properties);
    }

    public static final String MAX_MANA_BOOST = ArsMageFight.MOD_ID + "MAX_MANA_BOOST";

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return i.getOrCreateTag().getInt(MAX_MANA_BOOST);
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ICurioItem.super.curioTick(identifier, index, livingEntity, stack);

        if (!(stack.getOrCreateTag().contains(MAX_MANA_BOOST))) return;
        if (livingEntity.hasEffect(EffectInit.OVERCHARGE_EFFECT)) return;

        //Remove the mana bonus if they no longer have the overcharge effect.
        stack.getOrCreateTag().remove(MAX_MANA_BOOST);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class CreeperTrinketEvents{
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDamage(LivingHurtEvent event){
            if (event.isCanceled()) return;
            if (event.getSource() == null) return;
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            DamageSource source = event.getSource();
            LOGGER.debug("WHATS THE DAMAGE SOURCE? " + source.toString());
            if (!source.isExplosion()) return;
            PlayerEntity DamagedPlayer = (PlayerEntity) event.getEntityLiving();

            //Now look for the creeper trinket
            CuriosUtil.getAllWornItems(DamagedPlayer).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the creeper trinket
                    if (item instanceof CreeperTrinket){
                        int amp = 0;
                        if (DamagedPlayer.hasEffect(EffectInit.OVERCHARGE_EFFECT)) {
                            amp += DamagedPlayer.getEffect(EffectInit.OVERCHARGE_EFFECT).getAmplifier() + 1;

                            //If the player ends up having more than 3 stacks of overcharge (amp + 1) then increase the damage and remove the effect.
                            if (amp > 2){
                                event.setAmount(event.getAmount() * 3);
                                DamagedPlayer.removeEffect(EffectInit.OVERCHARGE_EFFECT);
                                return;
                            }
                        }
                        //Quarter the damage
                        event.setAmount(event.getAmount() * 0.25F);
                        //Set the effect
                        DamagedPlayer.addEffect(new EffectInstance(EffectInit.OVERCHARGE_EFFECT, 30 * 20, amp));

                        //Then make sure to set the max mana boost
                        IMana mana = ManaCapability.getMana(DamagedPlayer).resolve().get();
                        float manaPercent = mana.getMaxMana() * 0.2F;
                        e.getStackInSlot(i).getOrCreateTag().putInt(MAX_MANA_BOOST, (int) (manaPercent * (amp + 1)));
                    }
                }
            });
        }
    }
}
