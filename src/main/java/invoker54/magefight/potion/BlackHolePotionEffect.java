package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.BlackHoleEntity;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.UUID;

public class BlackHolePotionEffect extends Effect {
    public static final int effectColor = new Color(236, 99, 255,255).getRGB();

    private static final Logger LOGGER = LogManager.getLogger();

//    private static final HashMap<UUID, MagicDataCap> storedData = new HashMap<>();

    public BlackHolePotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static final String ATTRACT_STRING = "ATTRACT_EFFECT_STRING";
    public static final String SENSITIVE = "SENSITIVE";
    public static final String CASTER = "CASTER";
    public static final String HIT_ENTITY = "HIT_ENTITY";

    @Override
    public void applyEffectTick(LivingEntity entityIn, int amplifier) {
        if (!entityIn.level.isClientSide) {
//            LOGGER.debug("WHATS THE ENTITY CLASS? " + entityIn.getClass());
            CompoundNBT attractTag = MagicDataCap.getCap(entityIn).getTag(ATTRACT_STRING);
            int pullCount = 0;
            int range = 4 + (3 * amplifier);
            boolean isSensitive = attractTag.getBoolean(SENSITIVE);
            String entityHitID = attractTag.getString(HIT_ENTITY);

            UUID caster = attractTag.contains(CASTER) ? attractTag.getUUID(CASTER) : null;

            AxisAlignedBB bounds = entityIn.getBoundingBox().inflate(range);
            //Actual range from the center to the furthest wall of the bounding box
            double actualRange = Math.abs(bounds.maxX - entityIn.position().x);
            for (LivingEntity target : entityIn.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
                if ((target.noPhysics || target.isInvulnerable() || !target.isPushable()) && !(target instanceof BlackHoleEntity)) continue;
                if (target == entityIn) continue;
                if (target.getUUID().equals(caster)) continue;
                if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;
                if (isSensitive && !entityHitID.equals(target.getEncodeId())) continue;

                double distance = entityIn.distanceTo(target);
//            LOGGER.debug("WHATS THE MOB NAME " + target.getDisplayName().getString());
//            LOGGER.debug("WHATS THEIR DISTANCE " + distance);
                if (distance > actualRange) continue;

                //Pull strength per second (since a second is divided into 20 ticks)
                float pullStrength = 6 / 20F;
                pullStrength = (float) (pullStrength * (1 - Math.abs(distance / actualRange)));

                //This will repel mobs with the attract effect.
                if (target.hasEffect(EffectInit.BLACK_HOLE_EFFECT)) {
                    pullStrength = -pullStrength;
                    pullCount--;
                }

                //Now start to pull them towards the middle point
                double x = target.getDeltaMovement().x();
                x = x + (pullStrength * Math.signum(entityIn.position().x() - target.position().x()));

                double y = target.getDeltaMovement().y();
                if (!target.hasEffect(EffectInit.BLACK_HOLE_EFFECT)) {
                    y = y + (pullStrength * Math.signum(entityIn.position().y() - target.position().y()));
                }

                double z = target.getDeltaMovement().z();
                z = z + (pullStrength * Math.signum(entityIn.position().z() - target.position().z()));

                target.setDeltaMovement(x, y, z);
                target.hasImpulse = true;

                if (target instanceof ServerPlayerEntity){
                    ServerPlayerEntity player = (ServerPlayerEntity) target;
                    player.connection.send(new SEntityVelocityPacket(player));
                }

                //Increase pull count
                pullCount++;
            }
            //if the pull count is greater than 1, modify attract duration
            //Duration will be affected by the amount of entities being pulled on.
            EffectInstance attractEffect = entityIn.getEffect(EffectInit.BLACK_HOLE_EFFECT);
            if (attractEffect != null) {
                //Total potion time
                int totalTime = entityIn.tickCount + attractEffect.getDuration();
                int timeRemaining = (int) ((totalTime / (0.9F + (pullCount * 0.2F))));
                if (timeRemaining < entityIn.tickCount) {
                    entityIn.removeEffect(EffectInit.BLACK_HOLE_EFFECT);
                }
            }
        }

        super.applyEffectTick(entityIn, amplifier);
    }

    //This will give the entityIn the attract effect, and save the sensitive bool in their magic capability for later use
    public static void startAttract(LivingEntity entityIn, SpellStats stats, LivingEntity caster, LivingEntity hitEntity){
        int time = (8 + (4 * stats.getBuffCount(AugmentExtendTime.INSTANCE))) * 20;
        int amp = stats.getBuffCount(AugmentAOE.INSTANCE);
        // LOGGER.debug("WHATS THE AMP ON THE ATTRACT EFFECT? " + amp);

        CompoundNBT attractTag = MagicDataCap.getCap(entityIn).getTag(ATTRACT_STRING);
        attractTag.putBoolean(SENSITIVE, stats.hasBuff(AugmentSensitive.INSTANCE));
        //Who cast the spell, so they won't be affected by it.
        if (caster != null) {
            attractTag.putUUID(CASTER, caster.getUUID());
        }
        attractTag.putString(HIT_ENTITY, hitEntity != null ? hitEntity.getEncodeId() : "");

        MagicDataCap.syncToClient(entityIn);

        entityIn.addEffect(new EffectInstance(EffectInit.BLACK_HOLE_EFFECT, time, amp, false, false));
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            resetEffects(event);
        }
        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            resetEffects(event);
        }
        protected static void resetEffects(PotionEvent event){
            if (event.getEntityLiving().level.isClientSide) return;
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.BLACK_HOLE_EFFECT)) return;
            MagicDataCap cap = MagicDataCap.getCap(event.getEntityLiving());
            cap.removeTag(ATTRACT_STRING);
            MagicDataCap.syncToClient(event.getEntityLiving());
        }
    }
}
