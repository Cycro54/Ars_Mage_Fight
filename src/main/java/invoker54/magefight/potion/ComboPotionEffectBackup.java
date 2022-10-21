package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ComboPotionEffectBackup extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    //private static final HashMap<World, HashMap<UUID, SpellResolver>> comboSpells = new HashMap<>();
    public static final int effectColor = new Color(250, 146, 82,255).getRGB();
//
//    UUID comboUUID = UUID.fromString("7e9588dd-dfea-4748-a146-51829cb07e1f");
//    public static final String comboString = "COMBO_EFFECT_STRING";
//    public static final String casterString = "CASTER";
//    public static final String spellString = "SPELL_STRING";
//    public static final String hitListString = "HIT_LIST_STRING";
//    public static final String maxComboString = "MAX_COMBO";
//    public static final String spellColorString = "SPELL_COLOR_STRING";
//
    public ComboPotionEffectBackup(EffectType effectType) {
        super(effectType, effectColor);
    }
//
//    @Override
//    public void removeAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
//        ModifiableAttributeInstance moveSpeedInstance = manager.getInstance(Attributes.MOVEMENT_SPEED);
//        ModifiableAttributeInstance attackSpeedInstance = manager.getInstance(Attributes.ATTACK_SPEED);
//        LOGGER.debug("ADDING THE COMBO MODIFIER");
//
//        AttributeModifier modifier = new AttributeModifier(comboUUID, "Combo", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
//
//        if (moveSpeedInstance != null){
//            moveSpeedInstance.removeModifier(modifier.getId());
//        }
//
//        if (attackSpeedInstance != null){
//            attackSpeedInstance.removeModifier(modifier.getId());
//        }
//
//        super.removeAttributeModifiers(entityIn, manager, amp);
//    }
//
//    @Override
//    public void addAttributeModifiers(LivingEntity entityIn, AttributeModifierManager manager, int amp) {
//        MagicDataCap cap = MagicDataCap.getCap(entityIn);
//        if (!cap.getTag(comboString).getUUID(casterString).equals(entityIn.getUUID())) return;
//
//        ModifiableAttributeInstance moveSpeedInstance = manager.getInstance(Attributes.MOVEMENT_SPEED);
//        ModifiableAttributeInstance attackSpeedInstance = manager.getInstance(Attributes.ATTACK_SPEED);
//        LOGGER.debug("ADDING THE COMBO MODIFIER");
//
//        AttributeModifier modifier = new AttributeModifier(comboUUID, "Combo", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);
//
//        if (moveSpeedInstance != null){
//            moveSpeedInstance.removeModifier(modifier.getId());
//            moveSpeedInstance.addPermanentModifier(modifier);
//        }
//
//        if (attackSpeedInstance != null){
//            attackSpeedInstance.removeModifier(modifier.getId());
//            attackSpeedInstance.addPermanentModifier(modifier);
//        }
//        super.addAttributeModifiers(entityIn, manager, amp);
//    }
//
//    @Override
//    public double getAttributeModifierValue(int p_111183_1_, AttributeModifier p_111183_2_) {
//        return super.getAttributeModifierValue(p_111183_1_, p_111183_2_);
//    }
//
//    public static void startCombo(LivingEntity entity, SpellResolver resolver, int extraCombo){
//        MagicDataCap cap = MagicDataCap.getCap(entity);
//        cap.removeTag(comboString);
//        LOGGER.debug("WHOS THE ENTITY " + entity.getName().getString());
//
//        CompoundNBT tag = cap.getTag(comboString);
//        //Caster
//        tag.putUUID(casterString, resolver.spellContext.caster.getUUID());
//        //Spell
//        tag.putString(spellString, resolver.spell.serialize());
//        //Color
//        tag.putString(spellColorString, resolver.spellContext.colors.serialize());
//        //Amount of hits allowed
//        tag.putInt(maxComboString, 3 + (extraCombo * 2));
//
//        //Now give them the combo effect
//        entity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, (6 + (extraCombo * 3)) * 20, 0));
//
//        //Check to see if they still have the data
//        LOGGER.debug("DO THEY STILL HAVE THE DATA? " + MagicDataCap.getCap(entity).hasTag(comboString));
//    }
//
//    public static void tallyCombo(LivingEntity hitEntity){
//        MagicDataCap cap = MagicDataCap.getCap(hitEntity);
//        CompoundNBT tag = cap.getTag(comboString);
//
//        //Increase the amount of amps there are if the max hasn't been reached already
//        EffectInstance instance = hitEntity.getEffect(EffectInit.COMBO_EFFECT);
//        int ticks = instance.getDuration();
//        int amp = instance.getAmplifier() + 1;
//        LOGGER.debug("TALLY THE CURRENT AMP: " + amp);
//
//        //If they hit the max, cast the spell!
//        if (amp >= tag.getInt(maxComboString)){
////            LOGGER.debug("MAX WAS ACHIEVED! CASTING COMBO");
////            LOGGER.debug("THEY STILL HAVE THE DATA RIGHT? " + MagicDataCap.getCap(hitEntity).hasTag(comboString));
//            castCombo(hitEntity, amp);
//            return;
//        }
//
//        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, ticks, amp));
//        LOGGER.debug("WHATS THE NEW AMP: " + (hitEntity.getEffect(EffectInit.COMBO_EFFECT).getAmplifier()));
//    }
//
//    //This will cast the combo spell and remove the effect
//    public static void castCombo(LivingEntity hitEntity, int count){
////        LOGGER.debug("I AM NOW AT CAST COMBO. THE DATA IS THERE CORRECT? " + MagicDataCap.getCap(hitEntity).hasTag(comboString));
//        if (hitEntity.level.isClientSide) return;
//        ServerWorld entityWorld = (ServerWorld) hitEntity.getCommandSenderWorld();
//
//        MagicDataCap cap = MagicDataCap.getCap(hitEntity);
//        if (!cap.hasTag(comboString) || !hitEntity.hasEffect(EffectInit.COMBO_EFFECT)){
////            LOGGER.debug("THEY DONT HAVE COMBO DATA");
//            return;
//        }
//        CompoundNBT tag = cap.getTag(comboString);
//        UUID casterID = tag.getUUID(casterString);
//
//        LivingEntity casterEntity = (LivingEntity) entityWorld.getEntity(casterID);
//        Spell oldSpell = Spell.deserialize(tag.getString(spellString));
//        Spell newSpell = oldSpell;
//        ParticleColor.IntWrapper color = ParticleColor.IntWrapper.deserialize(tag.getString(spellColorString));
//
//        if (oldSpell.recipe.get(0) instanceof AbstractAugment){
//            newSpell = new Spell();
//            LOGGER.debug("HERES THE AMP: " + count);
//
//            //What this is doing is grabbing all the augments that were directly after the Combo glyph
//            ArrayList<AbstractAugment> affectedAugments = new ArrayList<>();
////            ArrayList<AbstractAugment> addedAugments = new ArrayList<>();
//            for (AbstractSpellPart part : oldSpell.recipe){
//                if (!(part instanceof AbstractAugment)) break;
//
//                affectedAugments.add((AbstractAugment) part);
//            }
//
//            //Next we begin to build the actual spell
//            for (AbstractSpellPart part : oldSpell.recipe){
//                //This is so the Combo glyph augments won't be included into the spell
//                if (newSpell.isEmpty() && part instanceof AbstractAugment) continue;
//
//                //Add the part in the very end
//                newSpell.add(part);
//
//                if (part instanceof AbstractEffect){
//                    //Get the augments that can go with this glyph
//                    LOGGER.debug("COMPATIBLE PIECES: " + part.getCompatibleAugments());
//                    List<AbstractAugment> partAugments = part.getCompatibleAugments().stream().filter(affectedAugments::contains).collect(Collectors.toList());
//                    LOGGER.debug("WHICH ONES WE CAN ADD TO: " + partAugments);
//
//                    //Then go through them, adding it to the spell
//                    for (AbstractAugment augment : partAugments){
//                        newSpell.add(augment, count);
//                    }
//                }
//
////                if (part instanceof AbstractAugment && affectedAugments.contains(part) && !addedAugments.contains(part)){
////                    //Add the augments based on the amplifier count
////                    newSpell.add(part,count);
////
////                    addedAugments.add((AbstractAugment) part);
////                }
////                else if (!(part instanceof AbstractAugment)){
////                    addedAugments.clear();
////                }
////
////                //Add the part in the very end
////                newSpell.add(part);
//            }
//
//        }
//
//        //Remove the tag
//        cap.removeTag(comboString);
//        //Then make sure to remove the Combo effect
//        hitEntity.removeEffect(EffectInit.COMBO_EFFECT);
//
//        //Cast the spell
//        SpellResolver resolver = new SpellResolver(new SpellContext(newSpell, casterEntity).withColors(color));
//        resolver.onResolveEffect(entityWorld, resolver.spellContext.caster, new EntityRayTraceResult(hitEntity));
//
//
//////        EffectInstance effectInstance = hitEntity.getEffect(EffectInit.COMBO_EFFECT);
//////        int timeInTicks = (10 + (5 * effectInstance.getAmplifier())) * 20;
////        int timeInTicks = 12 * 20;
////
////        //Add back the effect.
////        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, timeInTicks, 0));
//    }
//
////    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//    public static class ComboEvent{
//
////        //This changes the world where there combo data is saved
////        @SubscribeEvent
////        public static void onDimensionChange(EntityTravelToDimensionEvent event){
////            if (event.isCanceled()) return;
////            if (!(event.getEntity() instanceof LivingEntity)) return;
////            LivingEntity entity = (LivingEntity) event.getEntity();
////
////            //If they don't have the effect, return.
////            if (!entity.hasEffect(EffectInit.COMBO_EFFECT)) return;
////            if (!comboSpells.containsKey(entity.level)) return;
////
////            SpellResolver resolver = comboSpells.get(entity.level).get(entity.getUUID());
////            if (resolver == null) return;
////        }
//
//        //This will be for the caster
//        @SubscribeEvent
//        public static void onAttack(LivingAttackEvent event){
//            if (event.getEntityLiving().level.isClientSide) return;
//            if (event.isCanceled()) return;
//            if (event.getSource() == null) return;
//            if (!(event.getSource().getEntity() instanceof LivingEntity)) return;
//
//            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
//            LivingEntity hitEntity = event.getEntityLiving();
//            if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;
//
//            MagicDataCap cap = MagicDataCap.getCap(attacker);
//            CompoundNBT tag = cap.getTag(comboString);
//            List<Integer> hitList = Arrays.stream(tag.getIntArray(hitListString)).boxed().collect(Collectors.toList());
//
//
//            if (hitList.contains(hitEntity.getId())) return;
//
//            //Now add their id to the list
//            hitList.add(hitEntity.getId());
//
//            //Put the list back into the magic tag
//            tag.putIntArray(hitListString, hitList);
//
//            //Add the Combo entity for effect
//            ComboEntity comboEntity = new ComboEntity(hitEntity.level, hitEntity.position(), hitEntity, attacker);
//            hitEntity.level.addFreshEntity(comboEntity);
//
//            //And increase the effects amp
//            tallyCombo(attacker);
//        }
//
//        //This will be for entities that aren't the caster
//        @SubscribeEvent
//        public static void onDamage(LivingDamageEvent event){
//            if (event.getEntityLiving().level.isClientSide) return;
//            if (event.isCanceled()) return;
//            if (!event.getEntityLiving().hasEffect(EffectInit.COMBO_EFFECT)) return;
//            if (event.getSource() == null) return;
//            //Make sure they aren't damaging themselves. That would be cheating!
//            if (event.getSource().getEntity().equals(event.getEntityLiving())) return;
//
//            LivingEntity hitEntity = (LivingEntity) event.getEntity();
//            LOGGER.debug("IN DAMAGE METHOD, DO THEY STILL HAVE DATA? " + MagicDataCap.getCap(hitEntity).hasTag(comboString));
//
//            if (hitEntity.getHealth() > event.getAmount() && MagicDataCap.getCap(hitEntity).hasTag(comboString)) tallyCombo(hitEntity);
//
//            else castCombo(hitEntity, hitEntity.getEffect(EffectInit.COMBO_EFFECT).getAmplifier());
//        }
//
//        @SubscribeEvent
//        public static void onExpire(PotionEvent.PotionExpiryEvent event){
//            if (event.getPotionEffect() == null) return;
//            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
//            LivingEntity expireEntity = event.getEntityLiving();
//
//            castCombo(expireEntity, expireEntity.getEffect(EffectInit.COMBO_EFFECT).getAmplifier());
//        }
//
//        @SubscribeEvent
//        public static void onRemove(PotionEvent.PotionRemoveEvent event){
//            if (event.getPotionEffect() == null) return;
//            if (!event.getPotionEffect().getEffect().equals(EffectInit.COMBO_EFFECT)) return;
//            LivingEntity removeEntity = event.getEntityLiving();
//
//            castCombo(removeEntity, removeEntity.getEffect(EffectInit.COMBO_EFFECT).getAmplifier());
//        }
//
////        //This refreshes the combo effect.
////        @SubscribeEvent
////        public static void onKill(LivingDeathEvent event){
////            if (event.getSource() == null) return;
////            if (event.getEntityLiving().level.isClientSide) return;
////            DamageSource source = event.getSource();
////
////            if (!(source.getEntity() instanceof LivingEntity)) return;
////            LivingEntity attacker = (LivingEntity) source.getEntity();
////
////            //Make sure they have a combo spell
////            if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;
////
////            //Then find and cast the spell once more.
////            castCombo(attacker);
////        }
//
//
////        //This clears the hash map of any dead, null, or just living entities that don't have the combo effect
////        @SubscribeEvent
////        public static void wipeArray(TickEvent.WorldTickEvent event){
////            if (event.side.isClient()) return;
////            if (event.phase == TickEvent.Phase.START) return;
////
////            if (!comboSpells.containsKey(event.world)) return;
////            Set<UUID> entityIDs = comboSpells.get(event.world).keySet();
////
////            for (UUID uuid : entityIDs){
////                ServerWorld world = (ServerWorld) event.world;
////                LivingEntity entity = (LivingEntity) world.getEntity(uuid);
////                if (entity == null || entity.isDeadOrDying() || !entity.hasEffect(EffectInit.COMBO_EFFECT)){
////                    comboSpells.get(event.world).remove(uuid);
////                }
////            }
////        }
//    }
}
