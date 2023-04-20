package invoker54.magefight.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.awt.*;

public class ExplosiveBloodPotionEffect extends Effect {
    public static final int effectColor = new Color(239, 56, 26,255).getRGB();

    public ExplosiveBloodPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
}
