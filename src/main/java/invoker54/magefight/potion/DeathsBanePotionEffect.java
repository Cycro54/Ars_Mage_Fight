package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DeathsBanePotionEffect extends Effect {
    public static final int effectColor = new Color(62, 22, 126,255).getRGB();

    public DeathsBanePotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{

        //When the player kills something while Deaths bane is active
        @SubscribeEvent
        public static void onKill(LivingDeathEvent event){
            if (event.getSource() == null) return;
            if (!(event.getSource().getEntity() instanceof LivingEntity)) return;
            LivingEntity killer = (LivingEntity) event.getSource().getEntity();
            EffectInstance effect = killer.getEffect(EffectInit.DEATHS_BANE_EFFECT);
            if (effect == null) return;
            if (effect.getAmplifier() != 0) return;

            //Add the deaths bane effect tier 2 for 2 minutes as a cooldown
            killer.addEffect(new EffectInstance(EffectInit.DEATHS_BANE_EFFECT, 120 * 20, 1));
            //Then heals the player with 3 seconds of invulnerability
            killer.setHealth(killer.getMaxHealth()/2);
            killer.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 3 * 20, 5));
            killer.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 3 * 20, 5));
        }

        //When the potion expires whilst it is tier 1
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            EffectInstance effect = event.getPotionEffect();
            if (effect == null) return;
            if (!(effect.getEffect() instanceof  DeathsBanePotionEffect)) return;
            if (effect.getAmplifier() != 0) {
            }

            //This should kill the player if they run out of time.
            else{
                LivingEntity potionEntity = event.getEntityLiving();
                potionEntity.addEffect(new EffectInstance(EffectInit.DEATHS_BANE_EFFECT, 3 * 20, 1));
                DamageSource source = potionEntity.getLastDamageSource();
                if (source == null) source = DamageSource.OUT_OF_WORLD;
                potionEntity.hurt(source, Float.MAX_VALUE);
            }
        }

    }
}
