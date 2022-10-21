package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.ComboEntity;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ComboPotionEffect extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    //private static final HashMap<World, HashMap<UUID, SpellResolver>> comboSpells = new HashMap<>();
    public static final int effectColor = new Color(250, 146, 82,255).getRGB();
    protected static UUID comboUUID = UUID.fromString("7e9588dd-dfea-4748-a146-51829cb07e1f");
    public static final String comboString = "COMBO_EFFECT_STRING";
    public static final String casterString = "CASTER";
    public static final String hitListString = "HIT_LIST_STRING";
    public static final String maxComboString = "MAX_COMBO";
    public static final String storedDamageString = "STORED_DAMAGE";

    public ComboPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
    @Override
    public void removeAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
        ModifiableAttributeInstance moveSpeedInstance = manager.getInstance(Attributes.MOVEMENT_SPEED);
        ModifiableAttributeInstance attackSpeedInstance = manager.getInstance(Attributes.ATTACK_SPEED);
        LOGGER.debug("ADDING THE COMBO MODIFIER");

        AttributeModifier modifier = new AttributeModifier(comboUUID, "Combo", 0, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (moveSpeedInstance != null){
            moveSpeedInstance.removeModifier(modifier.getId());
        }

        if (attackSpeedInstance != null){
            attackSpeedInstance.removeModifier(modifier.getId());
        }

        super.removeAttributeModifiers(entityIn, manager, amp);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
        ModifiableAttributeInstance moveSpeedInstance = manager.getInstance(Attributes.MOVEMENT_SPEED);
        ModifiableAttributeInstance attackSpeedInstance = manager.getInstance(Attributes.ATTACK_SPEED);
        LOGGER.debug("ADDING THE COMBO MODIFIER");
        MagicDataCap cap = MagicDataCap.getCap(entityIn);
        float currAmount = cap.getTag(comboString).getIntArray(hitListString).length;

        AttributeModifier modifier = new AttributeModifier(comboUUID, "Combo", currAmount * 0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (moveSpeedInstance != null){
            moveSpeedInstance.removeModifier(modifier.getId());
            moveSpeedInstance.addPermanentModifier(modifier);
        }
        if (attackSpeedInstance != null){
            attackSpeedInstance.removeModifier(modifier.getId());
            attackSpeedInstance.addPermanentModifier(modifier);
        }
        super.addAttributeModifiers(entityIn, manager, amp);
    }

    public static void startCombo(LivingEntity hitEntity, SpellStats stats){
        MagicDataCap cap = MagicDataCap.getCap(hitEntity);
        cap.removeTag(comboString);
        LOGGER.debug("WHOS THE ENTITY " + hitEntity.getName().getString());

        CompoundNBT tag = cap.getTag(comboString);
//        //Caster
//        tag.putUUID(casterString, stats.);
//        //Color
//        tag.putString(spellColorString, resolver.spellContext.colors.serialize());
        //Amount of hits allowed
        tag.putInt(maxComboString, 3 + stats.getBuffCount(AugmentAmplify.INSTANCE));

        //Now give them the combo effect
        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, (6 + (stats.getBuffCount(AugmentAmplify.INSTANCE) * 2)) * 20, 0));
    }

    public static void tallyCombo(LivingEntity hitEntity){
        //Increase the amount of amps there are if the max hasn't been reached already
        EffectInstance instance = hitEntity.getEffect(EffectInit.COMBO_EFFECT);
        int ticks = instance.getDuration();
        int amp = instance.getAmplifier() + 1;
        LOGGER.debug("TALLY THE CURRENT AMP: " + amp);

        //If they hit the max, cast the spell!
        if (amp >= 3){
//            LOGGER.debug("MAX WAS ACHIEVED! CASTING COMBO");
//            LOGGER.debug("THEY STILL HAVE THE DATA RIGHT? " + MagicDataCap.getCap(hitEntity).hasTag(comboString));
            castCombo(hitEntity);
            return;
        }

        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, ticks, amp));
    }

    //This will cast the combo spell and remove the effect
    public static void castCombo(LivingEntity attacker){
        LOGGER.debug("GRABBING CAP");
        MagicDataCap cap = MagicDataCap.getCap(attacker);

        if (!cap.hasTag(comboString)) return;
        CompoundNBT tag = cap.getTag(comboString);

        LOGGER.debug("GETTING STORED DAMAGE");
        float storedDmg = tag.getFloat(storedDamageString);
        LOGGER.debug("ENTITY HAS COMBO RIGHT? " + attacker.hasEffect(EffectInit.COMBO_EFFECT));
        if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;
        int amp = attacker.getEffect(EffectInit.COMBO_EFFECT).getAmplifier() + 1;
        if (amp > 0) {

            LOGGER.debug("GRABBING HIT LIST");
            List<Integer> hitList = Arrays.stream(tag.getIntArray(hitListString)).boxed().collect(Collectors.toList());
            ArrayList<LivingEntity> victims = new ArrayList<>();

            for (int mobID : hitList){
                LivingEntity comboEntity = (LivingEntity) attacker.level.getEntity(mobID);
                if (comboEntity == null || !comboEntity.isAlive()) continue;
                victims.add(comboEntity);
            }
            LOGGER.debug("CREATING COMBO EVENT");
            LOGGER.debug("STORED DAMAGE: " + storedDmg);
            LOGGER.debug("AMOUNT OF VICTIMS: " + victims.size());
            LOGGER.debug("AMPLIFIER: " + amp);

            EventQueue.getServerInstance().addEvent(new ComboEvent(victims, attacker, storedDmg * amp));
        }

        LOGGER.debug("REMOVING COMBO TAG AND COMBO EFFECT");
        //Remove the tag
        cap.removeTag(comboString);
        MagicDataCap.syncToClient(attacker);
        //Then make sure to remove the Combo effect
        attacker.removeEffect(EffectInit.COMBO_EFFECT);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ComboEvents{

        //This will be for entities that aren't the caster
        @SubscribeEvent
        public static void onDamage(LivingDamageEvent event) {
            if (event.getEntityLiving().level.isClientSide) return;
            if (event.isCanceled()) return;
            if (event.getSource() == null) return;
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            LivingEntity hitEntity = event.getEntityLiving();
            if (attacker == null) return;
            //Make sure they aren't damaging themselves. That would be cheating!
            if (attacker.equals(hitEntity)) return;
            if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;

            MagicDataCap cap = MagicDataCap.getCap(attacker);
            CompoundNBT tag = cap.getTag(comboString);
            List<Integer> hitList = Arrays.stream(tag.getIntArray(hitListString)).boxed().collect(Collectors.toList());
            EffectInstance comboEffect = attacker.getEffect(EffectInit.COMBO_EFFECT);

            if (hitList.contains(hitEntity.getId())) return;

            //Now add their id to the list
            hitList.add(hitEntity.getId());

            //Put the list back into the magic tag
            tag.putIntArray(hitListString, hitList);

            //Add the Combo entity for effect
            ComboEntity comboEntity = new ComboEntity(hitEntity.level, hitEntity.position(), hitEntity, attacker);
            hitEntity.level.addFreshEntity(comboEntity);

            //This is how much is needed to increase the amp
            float threshold = tag.getInt(maxComboString) / 3F;
            threshold = (hitList.size() / (threshold * (comboEffect.getAmplifier() + 1)));

            //Increase the attack and move speed
            EffectInit.COMBO_EFFECT.addAttributeModifiers(attacker, attacker.getAttributes(), 0);

            //Reduce the damage & store it for later
            LOGGER.debug("DAMAGE BEFORE REDUCTION: " + event.getAmount());
            event.setAmount(event.getAmount() * 0.20F);
            LOGGER.debug("DAMAGE AFTER REDUCTION: " + event.getAmount());
            tag.putFloat(storedDamageString, tag.getFloat(storedDamageString) + event.getAmount());

            //Increase the effects amp if high enough
            if (threshold >= 1) {
                LOGGER.debug("TALLYING");
                //Increase the tally
                tallyCombo(attacker);
            }
        }

        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
            LivingEntity expireEntity = event.getEntityLiving();

            castCombo(expireEntity);
        }

        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
            LivingEntity removeEntity = event.getEntityLiving();

            castCombo(removeEntity);
        }

//        //This refreshes the combo effect.
//        @SubscribeEvent
//        public static void onKill(LivingDeathEvent event){
//            if (event.getSource() == null) return;
//            if (event.getEntityLiving().level.isClientSide) return;
//            DamageSource source = event.getSource();
//
//            if (!(source.getEntity() instanceof LivingEntity)) return;
//            LivingEntity attacker = (LivingEntity) source.getEntity();
//
//            //Make sure they have a combo spell
//            if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;
//
//            //Then find and cast the spell once more.
//            castCombo(attacker);
//        }


//        //This clears the hash map of any dead, null, or just living entities that don't have the combo effect
//        @SubscribeEvent
//        public static void wipeArray(TickEvent.WorldTickEvent event){
//            if (event.side.isClient()) return;
//            if (event.phase == TickEvent.Phase.START) return;
//
//            if (!comboSpells.containsKey(event.world)) return;
//            Set<UUID> entityIDs = comboSpells.get(event.world).keySet();
//
//            for (UUID uuid : entityIDs){
//                ServerWorld world = (ServerWorld) event.world;
//                LivingEntity entity = (LivingEntity) world.getEntity(uuid);
//                if (entity == null || entity.isDeadOrDying() || !entity.hasEffect(EffectInit.COMBO_EFFECT)){
//                    comboSpells.get(event.world).remove(uuid);
//                }
//            }
//        }
    }

    public static class ComboEvent implements ITimedEvent{

        ArrayList<LivingEntity> victims;
        DamageSource damageSource;
        float damage;
        int time = 20;

        public ComboEvent(ArrayList<LivingEntity> victims, LivingEntity attacker, float damage){
            this.victims = victims;
            this.damageSource = new EntityDamageSource("spell.combo",attacker);
            this.damage = damage;
            LOGGER.debug("HOW MUCH DAMAGE WILL BE DONE: " + damage);
        }

        @Override
        public void tick(boolean isServer) {
            --time;
            if (!isServer) return;

            if (time <= 0){
                for (LivingEntity entity : victims){
                    if (entity.isAlive()) entity.hurt(this.damageSource, damage);
                }
            }
        }

        @Override
        public boolean isExpired() {
            return time <= 0;
        }
    }
}
