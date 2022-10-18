package invoker54.magefight.potion;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.UUID;

public class ComboPotionEffect extends Effect {
    private static final Logger LOGGER = LogManager.getLogger();
    //private static final HashMap<World, HashMap<UUID, SpellResolver>> comboSpells = new HashMap<>();
    public static final int effectColor = new Color(250, 146, 82,255).getRGB();
    public static final String comboString = "COMBO_EFFECT_STRING";
    public static final String casterString = "CASTER";
    public static final String spellString = "SPELL_STRING";
    public static final String spellColorString = "SPELL_COLOR_STRING";

    public ComboPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static void saveCombo(LivingEntity entity, SpellResolver resolver){
        MagicDataCap cap = MagicDataCap.getCap(entity);

        CompoundNBT tag = cap.getTag(comboString);
        //Caster
        tag.putUUID(casterString, resolver.spellContext.caster.getUUID());
        //Spell
        tag.putString(spellString, resolver.spell.serialize());
        //Color
        tag.putString(spellColorString, resolver.spellContext.colors.serialize());
    }

    //This will cast the combo spell and refresh the effect
    public static void castCombo(LivingEntity entity){
        if (entity.level.isClientSide) return;
        ServerWorld entityWorld = (ServerWorld) entity.getCommandSenderWorld();

        MagicDataCap cap = MagicDataCap.getCap(entity);
        CompoundNBT tag = cap.getTag(comboString);
        UUID casterID = tag.getUUID(casterString);

        LivingEntity casterEntity = (LivingEntity) entityWorld.getEntity(casterID);
        Spell spell = Spell.deserialize(tag.getString(spellString));
        ParticleColor.IntWrapper color = ParticleColor.IntWrapper.deserialize(tag.getString(spellColorString));

        //Cast the spell
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, casterEntity).withColors(color));
        resolver.onResolveEffect(entityWorld, resolver.spellContext.caster, new EntityRayTraceResult(entity));

//        EffectInstance effectInstance = entity.getEffect(EffectInit.COMBO_EFFECT);
//        int timeInTicks = (10 + (5 * effectInstance.getAmplifier())) * 20;
        int timeInTicks = 12 * 20;

        //Add back the effect.
        entity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, timeInTicks, 0));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ComboEvent{

//        //This changes the world where there combo data is saved
//        @SubscribeEvent
//        public static void onDimensionChange(EntityTravelToDimensionEvent event){
//            if (event.isCanceled()) return;
//            if (!(event.getEntity() instanceof LivingEntity)) return;
//            LivingEntity entity = (LivingEntity) event.getEntity();
//
//            //If they don't have the effect, return.
//            if (!entity.hasEffect(EffectInit.COMBO_EFFECT)) return;
//            if (!comboSpells.containsKey(entity.level)) return;
//
//            SpellResolver resolver = comboSpells.get(entity.level).get(entity.getUUID());
//            if (resolver == null) return;
//        }

        //This refreshes the combo effect.
        @SubscribeEvent
        public static void onKill(LivingDeathEvent event){
            if (event.getSource() == null) return;
            if (event.getEntityLiving().level.isClientSide) return;
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof LivingEntity)) return;
            LivingEntity attacker = (LivingEntity) source.getEntity();

            //Make sure they have a combo spell
            if (!attacker.hasEffect(EffectInit.COMBO_EFFECT)) return;

            //Then find and cast the spell once more.
            castCombo(attacker);
        }


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
}
