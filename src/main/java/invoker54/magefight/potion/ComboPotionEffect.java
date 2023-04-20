package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.common.capability.Mana;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.ComboEntity;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.ComboEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ComboPotionEffect extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    //private static final HashMap<World, HashMap<UUID, SpellResolver>> comboSpells = new HashMap<>();
    public static final int effectColor = new Color(250, 146, 82,255).getRGB();
    protected static UUID comboUUID = UUID.fromString("7e9588dd-dfea-4748-a146-51829cb07e1f");
    public static final String comboString = "COMBO_EFFECT_STRING";
    public static final String casterString = "CASTER";
    public static final String hitListString = "HIT_LIST_STRING";
    public static final String storedDamageString = "STORED_DAMAGE";

    public ComboPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }
    @Override
    public void removeAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
        ModifiableAttributeInstance moveSpeedInstance = manager.getInstance(Attributes.MOVEMENT_SPEED);
        ModifiableAttributeInstance attackSpeedInstance = manager.getInstance(Attributes.ATTACK_SPEED);
//        LOGGER.debug("ADDING THE COMBO MODIFIER");

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
//        LOGGER.debug("ADDING THE COMBO MODIFIER");
        MagicDataCap cap = MagicDataCap.getCap(entityIn);
        float currAmount = cap.getTag(comboString).getIntArray(hitListString).length;

        AttributeModifier modifier = new AttributeModifier(comboUUID, "Combo", Math.min(currAmount * 0.1F, 0.7F), AttributeModifier.Operation.MULTIPLY_TOTAL);

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

    public static void startCombo(LivingEntity hitEntity){
        MagicDataCap cap = MagicDataCap.getCap(hitEntity);
        cap.removeTag(comboString);
        cap.getTag(comboString);
        MagicDataCap.syncToClient(hitEntity);
//        LOGGER.debug("WHOS THE ENTITY " + hitEntity.getName().getString());

        //Now give them the combo effect
        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, 6 * 20, 0));
    }

    public static void addComboTime(LivingEntity hitEntity){
        //Increase the amount of amps there are if the max hasn't been reached already
        EffectInstance instance = hitEntity.getEffect(EffectInit.COMBO_EFFECT);
        int ticks = instance.getDuration() + (3 * 20);

        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, ticks, 0));
    }

    //This will cast the combo spell and remove the effect
    public static void castCombo(LivingEntity attacker, boolean shiftClicked) {
//        LOGGER.debug("GRABBING CAP");
        MagicDataCap cap = MagicDataCap.getCap(attacker);

        if (!cap.hasTag(comboString)) return;
        CompoundNBT tag = cap.getTag(comboString);

//        LOGGER.debug("GETTING STORED DAMAGE");
        float storedDmg = tag.getFloat(storedDamageString);
//        LOGGER.debug("ENTITY HAS COMBO RIGHT? " + attacker.hasEffect(EffectInit.COMBO_EFFECT));
        if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;

        //Only if the sneak click the last enemy may it end correctly
        if (shiftClicked) {
            //Entities attacked
            List<Integer> hitList = Arrays.stream(tag.getIntArray(hitListString)).boxed().collect(Collectors.toList());

//            LOGGER.debug("GRABBING POTENTIAL VICTIMS");
            ArrayList<LivingEntity> victims = new ArrayList<>();

            for (int mobID : hitList) {
                LivingEntity comboEntity = (LivingEntity) attacker.level.getEntity(mobID);
                if (comboEntity == null || !comboEntity.isAlive()) continue;
                victims.add(comboEntity);
            }


//            LOGGER.debug("CREATING COMBO EVENT");
//            LOGGER.debug("STORED DAMAGE: " + storedDmg);
//            LOGGER.debug("AMOUNT OF VICTIMS: " + victims.size());

            attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, attacker.getSoundSource(), 1.0F, 1.0F);
            attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, attacker.getSoundSource(), 1.0F, 1.0F);
            EventQueue.getServerInstance().addEvent(new ComboEvent(victims, attacker, storedDmg));
        }

//        LOGGER.debug("REMOVING COMBO TAG AND COMBO EFFECT");
        //Remove the tag
        cap.removeTag(comboString);
//        LOGGER.debug("HAVE COMBO TAG? " + cap.hasTag(comboString));
        MagicDataCap.syncToClient(attacker);
        //Then make sure to remove the Combo effect
        attacker.removeEffect(EffectInit.COMBO_EFFECT);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ComboEvents{

        @SubscribeEvent
        public static void onRegen(ManaRegenCalcEvent event){
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            if (event.isCanceled()) return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            //If they dont have combo effect, pass.
            if (!player.hasEffect(EffectInit.COMBO_EFFECT)) return;

            //Make sure they have the combo tag!
            MagicDataCap cap = MagicDataCap.getCap(player);
            if (!cap.hasTag(comboString)) return;

            //If the Combo INSTANCE or MANA_LOSS_PER_ENTITY don't exist, pass.
            if (ComboEffect.INSTANCE == null || ComboEffect.INSTANCE.MANA_LOSS_PER_ENTITY == null) return;

            event.setRegen(0);
        }

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
            Mana manaCap = (Mana) ManaCapability.getMana(attacker).resolve().get();

            if (!hitList.contains(hitEntity.getId())) {
                //Now add their id to the list
                hitList.add(hitEntity.getId());

                //Put the list back into the magic tag
                tag.putIntArray(hitListString, hitList);

                //Add the Combo entity for effect
                ComboEntity comboEntity = new ComboEntity(hitEntity.level, hitEntity.position(), hitEntity, attacker);
                hitEntity.level.addFreshEntity(comboEntity);

                //Increase the attack and move speed
                EffectInit.COMBO_EFFECT.addAttributeModifiers(attacker, attacker.getAttributes(), 0);

                //Reduce the damage & store it for later
//                LOGGER.debug("DAMAGE BEFORE REDUCTION: " + event.getAmount());
                event.setAmount(event.getAmount() * 0.5F);
//                LOGGER.debug("DAMAGE AFTER REDUCTION: " + event.getAmount());
                tag.putFloat(storedDamageString, tag.getFloat(storedDamageString) + event.getAmount());

                if (manaCap != null) manaCap.removeMana(ComboEffect.INSTANCE.MANA_LOSS_PER_ENTITY.get());

                //Increase effect duration
                if (hitList.size() % 2 == 0) {
//                    LOGGER.debug("TALLYING");
                    //Increase the tally
                    addComboTime(attacker);
                }
            }
            else {
                event.setCanceled(true);
                return;
            }

            if (attacker.isCrouching() || (manaCap != null && manaCap.getCurrentMana() == 0)){
                castCombo(attacker, true);
            }
        }

        @SubscribeEvent
        public static void onFinalDamage(LivingDamageEvent event){
            if (event.getSource() == null) return;
            if (!Objects.equals(event.getSource().msgId, "ars_mage_fight.spell.combo")) return;
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;

            //If the damage isn't greater than health, pass.
            if (event.getAmount() <= event.getEntityLiving().getHealth()) return;

            float leftover = (event.getAmount() - event.getEntityLiving().getHealth());
            ManaCapability.getMana((LivingEntity) event.getSource().getEntity()).ifPresent((mana) ->{
                mana.addMana(Math.min(ComboEffect.INSTANCE.MANA_GAIN_PER_EXTRA_DAMAGE.get() * leftover, ComboEffect.INSTANCE.MANA_LOSS_PER_ENTITY.get()));
            });
        }

        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
            LivingEntity expireEntity = event.getEntityLiving();

            castCombo(expireEntity, false);
        }

        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
            LivingEntity removeEntity = event.getEntityLiving();

            castCombo(removeEntity, false);
        }
    }

    public static class ComboEvent implements ITimedEvent{
        ArrayList<LivingEntity> victims;
        DamageSource damageSource;
        float damage;
        int time = 20;

        public ComboEvent(ArrayList<LivingEntity> victims, LivingEntity attacker, float damage){
            this.victims = victims;
            this.damageSource = new EntityDamageSource("ars_mage_fight.spell.combo",attacker);
            this.damage = damage;
//            LOGGER.debug("HOW MUCH DAMAGE WILL BE DONE: " + damage);
        }

        @Override
        public void tick(boolean isServer) {
            --time;
            if (!isServer) return;

            if (time <= 0){
                Entity attacker = this.damageSource.getEntity();
                attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
                for (LivingEntity entity : victims){
                    if (entity.isAlive()){
                        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, entity.getSoundSource(), 1.0F, 1.0F);
                        entity.hurt(this.damageSource, damage);
                    }
                }
            }
        }

        @Override
        public boolean isExpired() {
            return time <= 0;
        }
    }
}
