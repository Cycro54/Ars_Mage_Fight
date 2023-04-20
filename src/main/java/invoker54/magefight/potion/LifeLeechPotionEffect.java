package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.CalcUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

public class LifeLeechPotionEffect extends Effect {
    public static final int effectColor = new Color(199, 243, 67,255).getRGB();

    public static final String LIFE_LEECH_DATA = "LIFE_LEECH_DATA";
    public static final String CASTER_UUID = "CASTER_UUID";
    private static final Logger LOGGER = LogManager.getLogger();

    public LifeLeechPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    public static void applyLeech(LivingEntity affected, int amp){

        float healthLoss = 0;

        //get the leechNBT
        CompoundNBT leechNBT = MagicDataCap.getCap(affected).getTag(LIFE_LEECH_DATA);
        PlayerEntity caster = null;
        if (leechNBT.hasUUID(CASTER_UUID)) {
            caster = affected.level.getPlayerByUUID(leechNBT.getUUID(CASTER_UUID));
        }
        //Base range is 3, each amp will increase the range by 2
        AxisAlignedBB bounds = affected.getBoundingBox().inflate((amp * 2) + 3);
        ArrayList<LivingEntity> targets = new ArrayList<>();
        for (LivingEntity target : affected.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (caster != null && target.isAlliedTo(caster)) continue;
            if (target == affected) continue;
            if (caster != null && target.getUUID().equals(caster.getUUID())) continue;
            if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;
            if (target.getHealth() == target.getMaxHealth()) continue;

            //Add the target
            targets.add(target);

            //Grab the amount of health they lost
            healthLoss += target.getMaxHealth() - target.getHealth();
        }
        if (caster != null && affected != caster && caster.getHealth() != caster.getMaxHealth()) {
            targets.add(caster);
            healthLoss += caster.getMaxHealth() - caster.getHealth();
        }
        //If noone is in the list, return.
        if (targets.isEmpty()) return;
        //Heal/Damage will equal the average amount of health missing among all nearby entities
        float health = healthLoss / targets.size();

        //Hurt the affected entity
        DamageSource leechSource = new EntityDamageSource("ars_mage_fight.spell.life_leech", caster);
        affected.hurt(leechSource, health);

//        LOGGER.debug("Health before multiplier: " + health);
        health = new CalcUtil(health).entityMultiplier(targets.size(), 0.5F).compile();

//        LOGGER.debug("Health before averaging: " + health);
        //Get the average heal
        health /= targets.size();
//        LOGGER.debug("Health after averaging: " + health);

        //Now heal each entity
        for (LivingEntity target : targets) {
            if (target.isInvertedHealAndHarm()) target.hurt(DamageSource.MAGIC, health);

            else target.heal(health);
        }
    }

    @Override
    public void applyEffectTick(LivingEntity affected, int amp) {
        if (!affected.level.isClientSide) {
            EffectInstance leechInst = affected.getEffect(EffectInit.LIFE_LEECH_EFFECT);
            if (leechInst == null) return;

            float seconds = leechInst.getDuration() / 20F;
            if (seconds % 3 == 0) {
                applyLeech(affected, amp);
            }
        }

        super.applyEffectTick(affected, amp);
    }

    public static void castLeech(LivingEntity affected, @Nullable LivingEntity caster, int amp){
        CompoundNBT leechNBT = MagicDataCap.getCap(affected).getTag(LIFE_LEECH_DATA);
        if (caster != null) leechNBT.putUUID(CASTER_UUID, caster.getUUID());
        affected.addEffect(new EffectInstance(EffectInit.LIFE_LEECH_EFFECT, 15 * 20, amp));
    }
    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{
        @SubscribeEvent (priority = EventPriority.HIGHEST)
        public static void onDamage(LivingDamageEvent event){
            if (event.getEntityLiving().level.isClientSide) return;
            if (event.isCanceled()) return;
            LivingEntity hitEntity = event.getEntityLiving();
            if (!hitEntity.hasEffect(EffectInit.LIFE_LEECH_EFFECT)) return;

            if (!event.getSource().getMsgId().equals("ars_mage_fight.spell.life_leech")) return;

//            LOGGER.debug("How much damage? " + event.getAmount());
//            LOGGER.debug("20% of the entities max health? " + hitEntity.getMaxHealth() * 0.2F);
            event.setAmount(Math.min(event.getAmount(), hitEntity.getMaxHealth() * 0.2F));
        }

        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.getPotionEffect() == null) return;
            if (!(event.getPotionEffect().getEffect() instanceof LifeLeechPotionEffect)) return;
            if (!event.getEntityLiving().isAlive()) return;

            //Apply one final leech.
            applyLeech(event.getEntityLiving(), event.getPotionEffect().getAmplifier());
        }
    }
}
