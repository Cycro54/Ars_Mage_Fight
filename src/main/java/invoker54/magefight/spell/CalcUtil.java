package invoker54.magefight.spell;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CalcUtil {

    private float baseValue;
    private float multiplier = 1;

    public CalcUtil(float baseValue){
        this.baseValue = baseValue;
    }

    public CalcUtil healthMultiply(float maxHealth, float percentage){
        maxHealth -= 20;
        multiplier += ((maxHealth/20F) * percentage);
        return this;
    }

    public CalcUtil entityMultiplier(int count, float percentage){
        count -= 1;
        multiplier += (count * percentage);
        return this;
    }

    public CalcUtil manaMultiplier(LivingEntity player, float percentage){
        if (!(player instanceof PlayerEntity)) return this;
        ManaCapability.getMana(player).resolve().ifPresent((mana) ->{
            float baseMaxMana = 100;
            float maxMana = mana.getMaxMana() - baseMaxMana;
           this.multiplier += ((maxMana / baseMaxMana) * percentage);
        });
        return this;
    }

    public CalcUtil regenMultiplier(PlayerEntity player, float percentage){
        ManaCapability.getMana(player).resolve().ifPresent((mana) ->{
            float baseRegen = 5;
            float regen = (float) (ManaUtil.getManaRegen(player) - baseRegen);
            this.multiplier += ((regen / baseRegen) * percentage);
        });
        return this;
    }

    public float compile(){
        return baseValue *= multiplier;
    }
}
