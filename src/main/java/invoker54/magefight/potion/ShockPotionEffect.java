package invoker54.magefight.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.awt.*;

public class ShockPotionEffect extends Effect {
    public static final int effectColor = new Color(218, 174, 72,255).getRGB();

    public ShockPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
}
