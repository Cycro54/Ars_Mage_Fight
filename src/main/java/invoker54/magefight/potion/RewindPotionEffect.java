package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.DeathGripEntity;
import invoker54.magefight.entity.TimeAnchorEntity;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.effect.RewindEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class RewindPotionEffect extends Effect {
    public static final int effectColor = new Color(122, 131, 232,255).getRGB();
    private static final Logger LOGGER = LogManager.getLogger();

    public RewindPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        return true;
    }

    @Override
    public void addAttributeModifiers(LivingEntity p_111185_1_, AttributeModifierManager p_111185_2_, int p_111185_3_) {
        super.addAttributeModifiers(p_111185_1_, p_111185_2_, p_111185_3_);
    }

    @Override
    public void applyEffectTick(LivingEntity entityIn, int amp) {
        if (!entityIn.level.isClientSide && entityIn.isAlive()){
            CompoundNBT anchorTag = MagicDataCap.getCap(entityIn).getTag(RewindEffect.rewindString).getCompound(RewindEffect.anchorPackString);
            LivingEntity anchorEntity = (LivingEntity) entityIn.level.getEntity(anchorTag.getInt(RewindEffect.anchorIDString));

            if (!(anchorEntity instanceof TimeAnchorEntity)){
//                LOGGER.info("HERE IS THE ID: " + (anchor.getId()));
                LOGGER.info("MAKING A NEW ANCHOR, HERE WILL BE THE COORDS: " + (RewindEffect.unPackPosition(anchorTag)));
                TimeAnchorEntity anchor = new TimeAnchorEntity(entityIn.level, entityIn, RewindEffect.unPackPosition(anchorTag));
                entityIn.level.addFreshEntity(anchor);
                LOGGER.info("HERE IS THERE ID: " + (anchor.getId()));
                anchorTag.putInt(RewindEffect.anchorIDString, anchor.getId());
                MagicDataCap.syncToClient(entityIn);
            }
        }
        super.applyEffectTick(entityIn, amp);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{

        //If expires, use method in Rewind Glyph Effect class
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.isCanceled()) return;
            if (event.getPotionEffect().getEffect() != EffectInit.REWIND_EFFECT) return;

            RewindEffect.moveToAnchor(event.getEntityLiving());
        }

        /** If Dispelled, Don't do anything */
        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.isCanceled()) return;
            if (event.getPotion() != EffectInit.FATAL_BOND_EFFECT) return;
        }

        @SubscribeEvent
        public static void onDimensionChange(EntityTravelToDimensionEvent event){
            if (!(event.getEntity() instanceof LivingEntity)) return;

            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.hasEffect(EffectInit.REWIND_EFFECT)){
                entity.removeEffect(EffectInit.REWIND_EFFECT);
            }
        }
    }
}
