package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.MetabolicRushEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.FoodStats;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

public class MetabolicRushPotionEffect extends Effect {
    public static final int effectColor = new Color(255, 143, 12,255).getRGB();

    public MetabolicRushPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }


    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class potionEvents{
        @SubscribeEvent
        public static void manaRegen(ManaRegenCalcEvent event){
            LivingEntity entity = event.getEntityLiving();
            if (!entity.hasEffect(EffectInit.METABOLIC_RUSH_EFFECT) || !(entity instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) entity;

            //If they have no food, then don't bother
            if (player.getFoodData().getFoodLevel() <= 0) return;

            FoodStats stats = player.getFoodData();

            int amp = player.getEffect(EffectInit.METABOLIC_RUSH_EFFECT).getAmplifier() + 1;
            //You need 4 exhaust to lose 1 food
            //1 amp will be 2 food exhaust, and 2 mana regen
            float foodExhaust = (amp * 2);
            //Take whatever is smaller
            foodExhaust = Math.min(foodExhaust, ((stats.getFoodLevel() + stats.getSaturationLevel()) * 4F));
            player.causeFoodExhaustion(foodExhaust);

            //Then give the mana regeneration
            event.setRegen(event.getRegen() + (foodExhaust * MetabolicRushEffect.INSTANCE.MANA_REGEN_PER_EXHAUST.get()));
        }
    }
}
