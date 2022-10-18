package invoker54.magefight.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class OverChargePotionEffect extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int effectColor = new Color(82, 250, 174,255).getRGB();

    public OverChargePotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
}
