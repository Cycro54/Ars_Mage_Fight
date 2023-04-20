package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.UUID;

public class EnragedPotionEffect extends Effect {
    public static final int effectColor = new Color(255, 40, 91,255).getRGB();

    public EnragedPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String casterString = "CASTER";
    public static final String targetString = "TARGET";
    public static final String enragedString = "ENRAGED_DATA";

    //Caster is who cast the spell, entityIn is the entity hit by the spell
    public static void startRage(LivingEntity caster, LivingEntity entityIn){
        //The bounds
        AxisAlignedBB bounds = entityIn.getBoundingBox().inflate(9);
        if (entityIn.hasEffect(EffectInit.ENRAGED_EFFECT)) return;

        // LOGGER.debug("DOES ENTITYIN HAVE A UUID? " + entityIn.getUUID());

        //Then do the spell thing here
        if (!entityIn.level.isClientSide) {
            for (MobEntity target : entityIn.level.getEntitiesOfClass(MobEntity.class, bounds)) {
                if (target == entityIn) continue;
                //If the target is enraged already, continue.
                if (target.hasEffect(EffectInit.ENRAGED_EFFECT)) continue;
                //Grab there capability
                MagicDataCap targCap = MagicDataCap.getCap(target);
                //Get the enraged tag
                CompoundNBT tag = targCap.getTag(enragedString);
                //Place the caster into the tag
                tag.putUUID(casterString, caster.getUUID());
                //Also place the target there as well.
                tag.putUUID(targetString, entityIn.getUUID());
                // LOGGER.debug("DOES TAG HAVE CASTER ID? " + caster.getUUID());
                // LOGGER.debug("DOES TAG HAVE ENTITYIN ID? " + entityIn.getUUID());
                //Set their target
                target.setTarget(entityIn);

                //Finally, give them the enraged effect
                target.addEffect(new EffectInstance(EffectInit.ENRAGED_EFFECT, 20*20, 0));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ComboEvents{
        //This will be for entities that aren't the caster
        @SubscribeEvent
        public static void onDamage(LivingDamageEvent event) {
            LivingEntity hurtEntity = event.getEntityLiving();
            //If they HAVE the enraged effect, don't bother with em
            if (hurtEntity.hasEffect(EffectInit.ENRAGED_EFFECT)) return;
            if (event.getSource() == null) return;
            if (!(event.getSource().getEntity() instanceof LivingEntity)) return;
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            //If the attacker DOES NOT have the Enraged effect, don't bother.
            if (!attacker.hasEffect(EffectInit.ENRAGED_EFFECT)) return;

            MagicDataCap attackCap = MagicDataCap.getCap(attacker);
            CompoundNBT tag = attackCap.getTag(enragedString);
            
            UUID casterID = tag.getUUID(casterString);
            UUID targetID = tag.getUUID(targetString);

            //Make sure it's the target entity being attacked
            if (!targetID.equals(hurtEntity.getUUID())) return;
            
            //Half the damage
            event.setAmount(event.getAmount()/2F); 
            
            LivingEntity caster = (LivingEntity) ((ServerWorld)hurtEntity.level).getEntity(casterID);
            
            //Then send that damage to the attacker
            attacker.hurt(new EntityDamageSource("ars_mage_fight.spell.enraged", caster), event.getAmount());
        }

        @SubscribeEvent
        public static void onTarget(LivingSetAttackTargetEvent event){
            LivingEntity trackerEntity = event.getEntityLiving();
            LivingEntity targetEntity = event.getTarget();
            if (trackerEntity.level.isClientSide) return;
            if (targetEntity == null) return;
            
            if (trackerEntity.hasEffect(EffectInit.ENRAGED_EFFECT)) {
                MagicDataCap cap = MagicDataCap.getCap(trackerEntity);
                CompoundNBT tag = cap.getTag(enragedString);

                //This is who they should be attacking first
                if (tag.hasUUID(targetString)) {
                    if (tag.getUUID(targetString).equals(targetEntity.getUUID())) return;
                    Entity rageEntity = ((ServerWorld) trackerEntity.level).getEntity(tag.getUUID(targetString));
                    if (rageEntity != null) {
                        ((MobEntity) trackerEntity).setTarget((LivingEntity) rageEntity);
                        return;
                    }
                }

                //This is who they should attack next
                if (tag.hasUUID(casterString)) {
                    if (tag.getUUID(casterString).equals(targetEntity.getUUID())) return;
                    Entity casterEntity = ((ServerWorld) trackerEntity.level).getEntity(tag.getUUID(casterString));
                    if (casterEntity != null) {
                        ((MobEntity) trackerEntity).setTarget((LivingEntity) casterEntity);
                    }
                }
            }

        }
    }
}
