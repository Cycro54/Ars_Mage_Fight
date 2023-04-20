package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.SpellShieldEntity;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.StalwartEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.UUID;

public class StalwartPotionEffect extends Effect {
    public static final int effectColor = new Color(0, 31, 42,255).getRGB();
    private static final Logger LOGGER = LogManager.getLogger();

    public StalwartPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static final String stalwartString = "STALWART_DATA";
    public static final String casterString = "CASTER";
    public static final String spellShieldString = "Spell_Shield_ID";
    public static final String storedManaString = "STORED_MANA_FLOAT";

    public static void castStalwart(LivingEntity shooter, SpellStats stats, LivingEntity hitEntity){
        MagicDataCap cap = MagicDataCap.getCap(hitEntity);
        CompoundNBT stalwartNBT = cap.getTag(stalwartString);
        //Place the caster in the nbt
        stalwartNBT.putUUID(casterString, shooter.getUUID());

        //Do the basic calculations
        int timeInTicks = (15 + (stats.getBuffCount(AugmentExtendTime.INSTANCE) * 5)) * 20;

        //Apply the mana drought effect to the caster (apply twice if the stalwart effect is applied to the caster)
        if (hitEntity.getId() != shooter.getId())ManaDroughtPotionEffect.applyPotionEffect(shooter, 8 + (stats.getBuffCount(AugmentExtendTime.INSTANCE) * 4), 1, false, true);
        else ManaDroughtPotionEffect.applyPotionEffect(shooter, 8 + (stats.getBuffCount(AugmentExtendTime.INSTANCE) * 4), 2,false, true);

        //Then give the hitEntity the Stalwart effect
        hitEntity.addEffect(new EffectInstance(EffectInit.STALWART_EFFECT, timeInTicks, 0,false, false, true));

    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entityIn, int amplifier) {
        if (!entityIn.level.isClientSide && entityIn.isAlive()) {
            CompoundNBT stalwartNBT = MagicDataCap.getCap(entityIn).getTag(stalwartString);
            if (!stalwartNBT.contains(casterString)){
                // LOGGER.debug("CASTER DOESN'T EXIST, HERES THE ENTITY WITHOUT THE DATA: " + entityIn.getName().getString());
                entityIn.removeEffect(EffectInit.STALWART_EFFECT);
                return;
            }

            Entity shieldEntity = entityIn.level.getEntity(stalwartNBT.getInt(spellShieldString));
            if (!(shieldEntity instanceof SpellShieldEntity)){
                SpellShieldEntity newShield = new SpellShieldEntity(entityIn.level, entityIn.position(), entityIn);
                entityIn.level.addFreshEntity(newShield);

                //Now save their id
                stalwartNBT.putInt(spellShieldString, newShield.getId());

                //Make sure to sync so the client entity has this data
                MagicDataCap.syncToClient(entityIn);
            }
        }

        super.applyEffectTick(entityIn, amplifier);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents {

        public static void removeEffect(LivingEntity potionEntity){
            MagicDataCap cap = MagicDataCap.getCap(potionEntity);
            CompoundNBT stalwartNBT = cap.getTag(stalwartString);

            //Before removing the tag, first remove the shield entity
            Entity shieldEntity = potionEntity.level.getEntity(stalwartNBT.getInt(spellShieldString));
            if (shieldEntity instanceof SpellShieldEntity) shieldEntity.remove();

            //Then let's begin setting up the slowness and knockback
            LivingEntity casterEntity = (LivingEntity) ((ServerWorld)potionEntity.level).getEntity(stalwartNBT.getUUID(casterString));
            UUID casterID = (casterEntity == null ? null : casterEntity.getUUID());
            // LOGGER.debug("WHATS MANA AMP CONFIG : " + StalwartEffect.INSTANCE.STORED_MANA_AMP.get());
            float amp = (float) (stalwartNBT.getFloat(storedManaString)/StalwartEffect.INSTANCE.STORED_MANA_AMP.get());
            // LOGGER.debug("HOW MUCH MANA WAS STORED? " + stalwartNBT.getFloat(storedManaString));
            // LOGGER.debug("WHATS AMP AT: " + amp);

            AxisAlignedBB bounds = potionEntity.getBoundingBox().inflate(amp);

            //Grab all entities nearby
            for (LivingEntity target : potionEntity.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
                if ((target.noPhysics || target.isInvulnerable() || !target.isPushable()))
                    continue;
                if (target == potionEntity) continue;
                if (target.getUUID().equals(casterID)) continue;
                if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;

                //First apply knockback
                EffectKnockback.INSTANCE.knockback(target, potionEntity, amp * (amp - target.distanceTo(potionEntity)));
                target.hurtMarked = true;
                //Then apply slowness
                target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, (int) (amp * 20), 0, false, false, true));
            }
            //Let's get some particle effects to show up and the explosion sound
            potionEntity.level.playSound(null, potionEntity.blockPosition(), SoundEvents.GENERIC_EXPLODE,
                    potionEntity.getSoundSource(), 4.0F, (1.0F + (potionEntity.level.random.nextFloat() - potionEntity.level.random.nextFloat()) * 0.2F) * 0.7F);

            Vector3d pos = new Vector3d(potionEntity.getX(), potionEntity.getY(), potionEntity.getZ());
            ((ServerWorld) potionEntity.level).sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1,1.0D, 0.0D, 0.0D, 1);
            //Finally remove the tag
            cap.removeTag(stalwartString);
        }

        @SubscribeEvent
        public static void onDimensionChange(EntityTravelToDimensionEvent event){
            if (!(event.getEntity() instanceof LivingEntity)) return;
            LivingEntity entity = (LivingEntity) event.getEntity();

            //Make sure they have this potion effect
            if (!entity.hasEffect(EffectInit.STALWART_EFFECT)) return;
            MagicDataCap cap = MagicDataCap.getCap(entity);
            //Make sure the entity doesn't equal the caster
            if (cap.getTag(stalwartString).getUUID(casterString).equals(entity.getUUID())) return;

            //Finally, remove the effect
            entity.removeEffect(EffectInit.STALWART_EFFECT);
        }
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.isCanceled()) return;
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.STALWART_EFFECT)) return;
            removeEffect(event.getEntityLiving());
        }
        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.isCanceled()) return;
            if (event.getPotion() != EffectInit.STALWART_EFFECT) return;
            removeEffect(event.getEntityLiving());
        }

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDamage(LivingHurtEvent event) {
            LivingEntity hurtEntity = event.getEntityLiving();
            if (hurtEntity.level.isClientSide) return;
            if (!hurtEntity.hasEffect(EffectInit.STALWART_EFFECT)) return;

            MagicDataCap cap = MagicDataCap.getCap(hurtEntity);
            if (!cap.hasTag(stalwartString)) return;

            CompoundNBT stalwartNBT = cap.getTag(stalwartString);
            LivingEntity casterEntity = (LivingEntity) ((ServerWorld)hurtEntity.level).getEntity(stalwartNBT.getUUID(casterString));
            EffectInstance effectInstance = hurtEntity.getEffect(EffectInit.STALWART_EFFECT);

            //if the caster is no longer in this dimension, remove the effect
            if (casterEntity == null){
                hurtEntity.removeEffect(EffectInit.STALWART_EFFECT);
                return;
            }

            IMana mana = ManaCapability.getMana(casterEntity).resolve().get();
            //This is how much damage we wish to block
            float dmgToBlock = (event.getAmount() * ((0.50F * hurtEntity.getArmorCoverPercentage()) + 0.25F));
            // LOGGER.debug("HOW MUCH DAMAGE DO I WISH TO BLOCK: " + dmgToBlock);
            //Make sure to take it out of the amount for now.
            event.setAmount(event.getAmount() - dmgToBlock);
            //This is the casters regen
            double manaRegen = ManaUtil.getManaRegen((PlayerEntity) casterEntity);
            StalwartEffect inst = StalwartEffect.INSTANCE;
            // LOGGER.debug("HOW MUCH MANA DO I HAVE IN TOTAL: " + mana.getMaxMana());
            // LOGGER.debug("MANA REGEN: " + manaRegen);
            // LOGGER.debug("MANA REGEN divided by dmgToBlock: " + (manaRegen/dmgToBlock));
            // LOGGER.debug("Max Mana/100: " + (mana.getMaxMana()/100F));
            // LOGGER.debug("That times the multiplier: " + ((mana.getMaxMana()/100F) * inst.MAX_MANA_MULTIPLIER.get()));
            //This is the mana per 1 damage
            double manaPerDmg = ((manaRegen/dmgToBlock) * inst.REGEN_MULTIPLIER.get()) + ((mana.getMaxMana()/100F) * inst.MAX_MANA_MULTIPLIER.get());
            // LOGGER.debug("Mana Per Damage: " + (manaPerDmg));
            //Mana cost will be halved if not the caster
            if (casterEntity.getId() != hurtEntity.getId()) manaPerDmg *= 0.5F;
            //This is how much mana will be used
            double manaToUse = dmgToBlock * manaPerDmg;
            if (manaToUse > mana.getCurrentMana()){
                manaToUse = mana.getCurrentMana();
            }
            // LOGGER.debug("HOW MUCH MANA TO USE " + manaToUse);
            //Reduce the amount of damage
            dmgToBlock = (float) (dmgToBlock - (manaToUse/manaPerDmg));
            // LOGGER.debug("HOW MUCH DAMAGE IS LEFT? " + dmgToBlock);
            //Add that damage back to the main amount
            event.setAmount(event.getAmount() + dmgToBlock);
            // LOGGER.debug("WHATS THE TOTAL DAMAGE LEFT? " + event.getAmount());
            //Remove the spent mana
            mana.removeMana(manaToUse);
            //And finally place it into the storeMana float
            stalwartNBT.putFloat(storedManaString, (float) (manaToUse + stalwartNBT.getFloat(storedManaString)));


            //If the caster has no more mana left, remove the stalwart effect
            if (mana.getCurrentMana() == 0){
                hurtEntity.removeEffect(EffectInit.STALWART_EFFECT);
            }
            //reduce the duration of the effect
            else {
                EffectInstance oldInstance = hurtEntity.removeEffectNoUpdate(EffectInit.STALWART_EFFECT);
                hurtEntity.addEffect(new EffectInstance(EffectInit.STALWART_EFFECT, oldInstance.getDuration() - (2 * 20), 0,false, false, true));
            }
        }
    }
}
