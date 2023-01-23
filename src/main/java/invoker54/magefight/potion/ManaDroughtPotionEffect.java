package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import net.minecraft.client.renderer.VideoMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManaDroughtPotionEffect extends Effect {
    public static final int effectColor = new Color(122, 46, 116,255).getRGB();

    public ManaDroughtPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static void applyPotionEffect(LivingEntity affected, int seconds, int amp, boolean additiveTime, boolean additiveAmp){
        if (!affected.hasEffect(EffectInit.MANA_DROUGHT_EFFECT)){
            affected.addEffect(new EffectInstance(EffectInit.MANA_DROUGHT_EFFECT, seconds * 20, Math.max(0,amp - 1)));
        }
        else{
            int oldAmp = affected.getEffect(EffectInit.MANA_DROUGHT_EFFECT).getAmplifier();
            int newAmp = (additiveAmp ? oldAmp + amp : Math.max(amp, oldAmp));

            int oldTime = affected.getEffect(EffectInit.MANA_DROUGHT_EFFECT).getDuration();
            int newTime = (additiveTime ? oldTime + (seconds * 20) : Math.max(oldTime, seconds * 20));

            affected.addEffect(new EffectInstance(EffectInit.MANA_DROUGHT_EFFECT, newTime, newAmp));
        }
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class ManaDroughtEvents{

        @SubscribeEvent
        public static void onRegen(ManaRegenCalcEvent event){
            if (!event.getEntityLiving().hasEffect(EffectInit.MANA_DROUGHT_EFFECT)) return;

            float percentLoss = (event.getEntityLiving().getEffect(EffectInit.MANA_DROUGHT_EFFECT).getAmplifier() + 1) * 0.25F;

            event.setRegen(event.getRegen() - (percentLoss * event.getRegen()));
        }

    }
}
