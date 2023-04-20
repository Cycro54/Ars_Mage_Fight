package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.CalcUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BattleHungerPotionEffect extends Effect {
    public static final int effectColor = new Color(100, 16, 16,255).getRGB();
    protected static UUID battleHungerUUID = UUID.fromString("dc2e9944-44e2-43ea-a957-f87832f984f8");
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String BATTLE_HUNGER_DATA = "BATTLE_HUNGER_DATA";
    public static final String KILL_COUNT_INT = "KILL_COUNT_INT";
    public static final String CASTER_DAMAGE_FLOAT = "CASTER_DAMAGE_FLOAT";
    public static final String CASTER_UUID = "CASTER_UUID";

    public BattleHungerPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity affected, int amp) {
        EffectInstance battleHungerInst = affected.getEffect(EffectInit.BATTLE_HUNGER_EFFECT);
        if (battleHungerInst == null) return;

        float seconds = (battleHungerInst.getDuration()/20F);
        if ((battleHungerInst.getDuration()/20F) % 5F == 0 && seconds != 25){
            CompoundNBT hungerNBT = MagicDataCap.getCap(affected).getTag(BATTLE_HUNGER_DATA);
            PlayerEntity caster = affected.level.getPlayerByUUID(hungerNBT.getUUID(CASTER_UUID));
            DamageSource BATTLE_HUNGER_DAMAGE = new EntityDamageSource("ars_mage_fight.spell.battle_hunger", caster);
            //Deal damage
            float damage = new CalcUtil(hungerNBT.getFloat(CASTER_DAMAGE_FLOAT)).healthMultiply(affected.getHealth(), 0.5F).compile();
            float damagePercent = 1 - (seconds/25F);
            damagePercent *= damagePercent;
            affected.hurt(BATTLE_HUNGER_DAMAGE, damage * damagePercent);
            //Then apply slow
            affected.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int) (1.5F * 20), 2));
        }

        super.applyEffectTick(affected, amp);
    }

    public static void castBattleHunger(LivingEntity hitEntity, LivingEntity caster, float damage, int killCount){
        //kill count must be higher than or equal to 1
        killCount = Math.max(1, killCount);
        //And be lower than or equal to 3
        killCount = Math.min(3, killCount);

        CompoundNBT battleHungerNBT = MagicDataCap.getCap(hitEntity).getTag(BATTLE_HUNGER_DATA);

        //Check if they have the effect already
        if (!hitEntity.hasEffect(EffectInit.BATTLE_HUNGER_EFFECT) || battleHungerNBT.getInt(KILL_COUNT_INT) < killCount) {
            //Set up the kill count
            battleHungerNBT.putInt(KILL_COUNT_INT, killCount);
            //Set up damage
            battleHungerNBT.putFloat(CASTER_DAMAGE_FLOAT, damage);
            //Save caster uuid
            battleHungerNBT.putUUID(CASTER_UUID, caster.getUUID());
            //Give them the potion effect.
            hitEntity.addEffect(new EffectInstance(EffectInit.BATTLE_HUNGER_EFFECT, 25 * 20, 0));
        }
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{

        //When the affected is hurt by the spell
        @SubscribeEvent
        public static void onHurt(LivingDamageEvent event){
            if (event.getEntityLiving().level.isClientSide) return;
            if (event.isCanceled()) return;
            LivingEntity hitEntity = event.getEntityLiving();
            if (!hitEntity.hasEffect(EffectInit.BATTLE_HUNGER_EFFECT)) return;

            if (!event.getSource().getMsgId().equals("ars_mage_fight.spell.battle_hunger")) return;
            CompoundNBT hungerNBT = MagicDataCap.getCap(hitEntity).getTag(BATTLE_HUNGER_DATA);
            //Make sure the damage does not exceed 20% of the entities health
            event.setAmount(Math.min(hungerNBT.getFloat(CASTER_DAMAGE_FLOAT), hitEntity.getMaxHealth() * 0.2F));
        }

        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.getPotionEffect() == null) return;
            if (!(event.getPotionEffect().getEffect() instanceof BattleHungerPotionEffect)) return;

            LivingEntity affected = event.getEntityLiving();
            CompoundNBT hungerNBT = MagicDataCap.getCap(affected).getTag(BATTLE_HUNGER_DATA);
            PlayerEntity caster = affected.level.getPlayerByUUID(hungerNBT.getUUID(CASTER_UUID));
            DamageSource BATTLE_HUNGER_DAMAGE = new EntityDamageSource("ars_mage_fight.spell.battle_hunger", caster);
            //Deal damage
            float damage = new CalcUtil(hungerNBT.getFloat(CASTER_DAMAGE_FLOAT)).healthMultiply(affected.getHealth(), 0.5F).compile();
            affected.hurt(BATTLE_HUNGER_DAMAGE, damage);
            //Then apply slow
            affected.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (4 * 20), 2));
        }
    }
}
