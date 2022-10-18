package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.RuptureSwordEntity;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.RewindEffect;
import invoker54.magefight.spell.effect.RuptureEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

public class RupturePotionEffect extends Effect {
    public static final int effectColor = new Color(122, 0, 0,255).getRGB();

    public static final DamageSource RUPTURE_DEATH = new DamageSource("spell.rupture").bypassArmor().setMagic();

    public RupturePotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
    public static final String ruptureData = "RUPTURE_DATA";
    public static final String lastHurtTick = "HURT_TICK_INT";
    public static final String healthString = "HEALTH_FLOAT";

    public static final String ruptureEntityString = "RUPTURE_ENTITY_ID";

    public static void castRupture(LivingEntity entity, int time){
        CompoundNBT ruptureNBT = MagicDataCap.getCap(entity).getTag(ruptureData);

        //Last position
        RewindEffect.packPosition(ruptureNBT, entity.position());

        //Last hurt tick
        ruptureNBT.putInt(lastHurtTick, (int) entity.level.getGameTime());

        //Current health
        ruptureNBT.putFloat(healthString, entity.getHealth());

        //Then do the spell thing here
        entity.addEffect(new EffectInstance(EffectInit.RUPTURE_EFFECT, time));
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entityIn, int amp) {
        if (entityIn.isAlive() && !entityIn.level.isClientSide){
            CompoundNBT ruptureNBT = MagicDataCap.getCap(entityIn).getTag(ruptureData);
            if (Math.abs(ruptureNBT.getInt(lastHurtTick) - entityIn.level.getGameTime()) >= 5){
                Vector3d currPos = new Vector3d(entityIn.position().x(), entityIn.position().y(), entityIn.position().z());
                Vector3d lastPos = RewindEffect.unPackPosition(ruptureNBT);

                double distance = currPos.distanceTo(lastPos);
                //HEAL
                if (distance <= 0.1F && entityIn.getHealth() < ruptureNBT.getFloat(healthString)){
                    entityIn.heal((float) (entityIn.getMaxHealth() * 0.01F * RuptureEffect.INSTANCE.HEAL_MULTIPLIER.get()));
                    if (entityIn.getHealth() > ruptureNBT.getFloat(healthString)) entityIn.setHealth(ruptureNBT.getFloat(healthString));
                }
                //HURT
                else {
                    float damage = (float) ((distance * 0.01F) * entityIn.getMaxHealth() * RuptureEffect.INSTANCE.DAMAGE_MULTIPLIER.get());

//                    if ((((!RuptureEffect.INSTANCE.CAN_KILL_PLAYER.get() && (entityIn instanceof PlayerEntity))
//                            || (!RuptureEffect.INSTANCE.CAN_KILL_MOBS.get() && (entityIn instanceof MobEntity)))
//                            && damage < entityIn.getHealth()) || entityIn.getHealth() > damage)
                    if (damage > 0.05F)
                            entityIn.hurt(RUPTURE_DEATH, damage);
                }

                //Sets the lastHurtTick to the current time
                ruptureNBT.putInt(lastHurtTick, (int) entityIn.level.getGameTime());

                //Pack the new position for later use
                RewindEffect.packPosition(ruptureNBT, entityIn.position());
            }

            //This is the cosmetic effect
            Entity swordEntity = entityIn.level.getEntity(ruptureNBT.getInt(ruptureEntityString));
            if (!(swordEntity instanceof RuptureSwordEntity)){
                RuptureSwordEntity newSword = new RuptureSwordEntity(entityIn.level, entityIn.position(), entityIn);
                entityIn.level.addFreshEntity(newSword);

                //Now save their id
                ruptureNBT.putInt(ruptureEntityString, newSword.getId());

                //Make sure to sync so the client entity has this data
                MagicDataCap.syncToClient(entityIn);
            }
        }
        super.applyEffectTick(entityIn, amp);
    }


    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDamage(LivingDamageEvent event){
            if (event.getSource() != RUPTURE_DEATH) return;
            if (RuptureEffect.INSTANCE.CAN_KILL_PLAYER.get() && (event.getEntityLiving() instanceof PlayerEntity)) return;
            if (RuptureEffect.INSTANCE.CAN_KILL_MOBS.get() && (event.getEntityLiving() instanceof MobEntity)) return;
            if (event.isCanceled()) return;

            LivingEntity hurtEntity = event.getEntityLiving();
            if (hurtEntity.getHealth() <= event.getAmount()){
                event.setCanceled(true);
                hurtEntity.setHealth(1);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onDying(LivingDeathEvent event){
            if (event.getSource() != RUPTURE_DEATH) return;
            if (RuptureEffect.INSTANCE.CAN_KILL_PLAYER.get() && (event.getEntityLiving() instanceof PlayerEntity)) return;
            if (RuptureEffect.INSTANCE.CAN_KILL_MOBS.get() && (event.getEntityLiving() instanceof MobEntity)) return;
            if (event.isCanceled()) return;

            event.getEntityLiving().setHealth(1);
            event.setCanceled(true);
        }
    }
}
