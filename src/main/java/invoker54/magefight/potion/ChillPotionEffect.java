package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class ChillPotionEffect extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DamageSource CHILLED_DEATH = new DamageSource("ars_mage_fight.spell.chilled").bypassArmor().setMagic();
    public static final ArrayList<UUID> checkedEntities = new ArrayList<>();
    public static final int effectColor = new Color(70, 163, 217,255).getRGB();

    public ChillPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ChillEvent{

        @SubscribeEvent
        public static void onHeal(LivingHealEvent event){
            LivingEntity healEntity = event.getEntityLiving();
            if (event.isCanceled()) return;
            if (healEntity.isDeadOrDying()) return;
//            LOGGER.debug("Does checkedEntities contain this entity? " + checkedEntities.contains(healEntity.getUUID()));
            if (checkedEntities.contains(healEntity.getUUID())) return;
//            LOGGER.debug("Does " + healEntity.getDisplayName().getString() + " have chill effect? " + (healEntity.hasEffect(EffectInit.CHILL_EFFECT)));
            if (!healEntity.hasEffect(EffectInit.CHILL_EFFECT)) return;

            //First grab the chill effect instance
            EffectInstance effect = healEntity.getEffect(EffectInit.CHILL_EFFECT);

            //Second grab the healing amount and health to lose
            float heal = event.getAmount();
            float healLoss = heal * ((effect.getAmplifier() + 1) * 0.25F);

            //Now reduce that healing.
            heal -= healLoss;
            event.setAmount(heal);

            if (heal < 0){
                event.setCanceled(true);
                healEntity.hurt(ChillPotionEffect.CHILLED_DEATH, -heal);

                //Make sure to place this entity in the already checked list, or else it may create an endless loop.
                checkedEntities.add(healEntity.getUUID());
            }
        }

        @SubscribeEvent
        public static void wipeArray(TickEvent.ServerTickEvent event){
            if (event.phase == TickEvent.Phase.START) return;

            if (!checkedEntities.isEmpty()) checkedEntities.clear();
        }
    }
}
