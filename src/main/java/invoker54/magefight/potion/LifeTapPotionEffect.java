package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.UUID;

public class LifeTapPotionEffect extends Effect {
    public static final int effectColor = new Color(215, 51, 107,255).getRGB();

    public LifeTapPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    UUID lifeTapUUID = UUID.fromString("989e8f88-fb4f-4bdc-8fc2-cef266d2c72d");
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void removeAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
        ModifiableAttributeInstance instance = manager.getInstance(Attributes.MAX_HEALTH);
        // LOGGER.debug("REMOVING THE LIFE TAP MODIFIER");

        float convertPercent = 0.1F + (0.1F * (amp));
        convertPercent = 1 - convertPercent;
        AttributeModifier modifier = new AttributeModifier(lifeTapUUID, "LifeTap", -convertPercent, AttributeModifier.Operation.MULTIPLY_TOTAL);
        float health = entityIn.getHealth();
        if (instance.hasModifier(modifier)) instance.removeModifier(modifier.getId());
        super.removeAttributeModifiers(entityIn, manager, amp);

        entityIn.setHealth(health);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
        ModifiableAttributeInstance instance = manager.getInstance(Attributes.MAX_HEALTH);
        // LOGGER.debug("ADDING THE LIFE TAP MODIFIER");

        float convertPercent = 0.1F + (0.1F * (amp));
        convertPercent = 1 - convertPercent;
//        LOGGER.debug("WHAT IS CONVERT PERCENT: " + convertPercent);
        AttributeModifier modifier = new AttributeModifier(lifeTapUUID, "LifeTap", -convertPercent, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (instance.hasModifier(modifier)) instance.removeModifier(modifier.getId());
        instance.addPermanentModifier(modifier);
        super.addAttributeModifiers(entityIn, manager, amp);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{

        @SubscribeEvent
        public static void calcMaxMana(MaxManaCalcEvent event){
            LivingEntity shooter = event.getEntityLiving();
            if (!shooter.hasEffect(EffectInit.LIFE_TAP_EFFECT)) return;
            EffectInstance instance = shooter.getEffect(EffectInit.LIFE_TAP_EFFECT);

            //How much health to take and mana max to change
            float convertPercentage = 0.1F + (0.1F * (instance.getAmplifier()));

            //This will be the added max Mana
            int extraMana = (int) (event.getMax() * convertPercentage);

            //Set the max mana
            event.setMax(event.getMax() + extraMana);

//            LOGGER.debug("POTION EFFECT MAX MANA: " + event.getMax());
        }
//        @SubscribeEvent
//        public static void onHeal(LivingHealEvent event){
//            LivingEntity healEntity = event.getEntityLiving();
//            if (!healEntity.hasEffect(EffectInit.LIFE_TAP_EFFECT)) return;
//
//            EffectInstance instance = healEntity.getEffect(EffectInit.LIFE_TAP_EFFECT);
//
//            float convertPercent = 0.1F + (0.1F * (instance.getAmplifier()));
//
//            if (event.getAmount() + healEntity.getHealth() > (healEntity.getMaxHealth()))
//        }

    }
}
