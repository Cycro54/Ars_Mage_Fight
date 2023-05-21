package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.VengefulStrikeEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class VengeancePotionEffect extends Effect {
    public static final int effectColor = new Color(45, 27, 134,255).getRGB();
    public static final DamageSource VENGEFUL_DEATH = new DamageSource("spell.vengeance").bypassArmor().setMagic();

    public VengeancePotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static final String vengeanceString = "VENGEANCE_EFFECT_STRING";
    public static final String damageStored = "STORED_DAMAGE";
    public static final String caster = "CASTER_ENTITY";

    public static void beginVengeance(LivingEntity hitEntity, LivingEntity shooter, int amp){
        if (shooter.level.isClientSide) return;

        CompoundNBT vengNBT = MagicDataCap.getCap(hitEntity).getTag(vengeanceString);
        vengNBT.putUUID(caster, shooter.getUUID());

        hitEntity.addEffect(new EffectInstance(EffectInit.VENGEANCE_EFFECT, 6 * 20, amp));
    }

    public static void storeDamage(LivingEntity entity, float damage){
        if (entity.level.isClientSide) return;

        CompoundNBT vengNBT = MagicDataCap.getCap(entity).getTag(vengeanceString);
        vengNBT.putFloat(damageStored, damage + vengNBT.getFloat(damageStored));
    }
    public static float getDamage(LivingEntity entity){
        if (entity.level.isClientSide) return 0;

        CompoundNBT vengNBT = MagicDataCap.getCap(entity).getTag(vengeanceString);

        return vengNBT.getFloat(damageStored);
    }
    public static UUID getCaster(LivingEntity entity){
        if (entity.level.isClientSide) return null;

        CompoundNBT vengNBT = MagicDataCap.getCap(entity).getTag(vengeanceString);

        return vengNBT.contains(caster) ? vengNBT.getUUID(caster) : null;
    }
    public static void removeCompound(LivingEntity entity){
        MagicDataCap.getCap(entity).removeTag(vengeanceString);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{
        @SubscribeEvent
        public static void onDamage(LivingAttackEvent event){
            if (event.isCanceled()) return;

            LivingEntity hitEntity = event.getEntityLiving();
            if (!hitEntity.hasEffect(EffectInit.VENGEANCE_EFFECT)) return;
            
            //Store a portion of the damage
            storeDamage(hitEntity, (float) (event.getAmount() * VengefulStrikeEffect.INSTANCE.DAMAGE_STORED_PERCENT.get()));
        }
        
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.VENGEANCE_EFFECT)) return;
            causeDamage(event.getEntityLiving(), event.getPotionEffect().getAmplifier());
        }

        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.isCanceled()) return;
            if (event.getPotion() != EffectInit.VENGEANCE_EFFECT) return;
            causeDamage(event.getEntityLiving(), event.getPotionEffect().getAmplifier());
        }

        @SubscribeEvent
        public static void onDeath(LivingDeathEvent event){
            if (event.isCanceled()) return;
            LivingEntity deathEntity = event.getEntityLiving();
            if (!deathEntity.hasEffect(EffectInit.VENGEANCE_EFFECT))return;
            causeDamage(deathEntity, deathEntity.getEffect(EffectInit.VENGEANCE_EFFECT).getAmplifier());
        }

        public static void causeDamage(LivingEntity potionEntity, int amp){
            float damage = getDamage(potionEntity);
            UUID casterID = getCaster(potionEntity);
            if (casterID == null) casterID = Util.NIL_UUID;
            LivingEntity caster = (LivingEntity) ((ServerWorld)potionEntity.level).getEntity(casterID);

            int range = 3 + (2 * amp);
            AxisAlignedBB bounds = potionEntity.getBoundingBox().inflate(range);
            ArrayList<LivingEntity> targets = new ArrayList<>();
            for (LivingEntity target : potionEntity.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
                if (caster != null && target.isAlliedTo(caster)) continue;
                if (target == potionEntity) continue;
                if (target.getUUID().equals(casterID)) continue;
                if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;

                //Add the target
                targets.add(target);
            }

            //Divide the damage evenly among all entities in the list
            damage /= targets.size();

            for (LivingEntity entity : targets){
                //Make them take damage
                entity.hurt(VENGEFUL_DEATH, damage);
            }

            removeCompound(potionEntity);
        }
    }
}
