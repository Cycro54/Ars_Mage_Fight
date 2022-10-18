package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.event.ManaCapEvents;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateMana;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.network.NetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LifeTapEffect extends AbstractEffect {

    public static LifeTapEffect INSTANCE = new LifeTapEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private LifeTapEffect() {
        super("life_tap", "Life Tap");
    }

    //This would make it so the effect only works on entities (might not need it though.)
//    @Override
//    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        return rayTraceResult instanceof EntityRayTraceResult;
//    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (shooter == null) return;
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        if (rayTraceResult.getEntity().getId() != shooter.getId()) return;
        if (!(shooter instanceof PlayerEntity)) return;
        shooter = (LivingEntity) rayTraceResult.getEntity();

        //Max time
        int time = (12 + (4 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE))) * 20;
        int amp = Math.min(4, spellStats.getBuffCount(AugmentAmplify.INSTANCE));

        //convert extra health into mana (if they don't have the effect already
        float extraMana = 0;
        //The shooters mana
        IMana mana = ManaCapability.getMana(shooter).resolve().get();

        if (!shooter.hasEffect(EffectInit.LIFE_TAP_EFFECT)) {
            //How much health to take and mana max to change
            float convertPercentage = 0.1F + (0.1F * (amp));

            //get the new max health
            float newMaxHealth = shooter.getMaxHealth() - (shooter.getMaxHealth() * convertPercentage);

            //Convert leftover health into mana
            float leftoverHealth = shooter.getHealth() - newMaxHealth;
            extraMana = (leftoverHealth/(shooter.getMaxHealth() * convertPercentage)) * (mana.getMaxMana() * convertPercentage);
        }

        shooter.addEffect(new EffectInstance(EffectInit.LIFE_TAP_EFFECT, time, amp));
        mana.setMaxMana(ManaUtil.getMaxMana((PlayerEntity) shooter));
        mana.addMana(extraMana);
        if (mana.getCurrentMana() == mana.getMaxMana()) mana.setMana(mana.getMaxMana() - 0.01F);
//        LOGGER.debug("IS SHOOTER A SERVER PLAYER: " + (shooter instanceof ServerPlayerEntity));
        ManaCapEvents.syncPlayerEvent((PlayerEntity) shooter);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public int getAdjustedManaCost(List<AbstractAugment> augmentTypes) {
        int cost = this.getConfigCost();

        for (AbstractAugment a : augmentTypes) {
            cost += a.getConfigCost();
        }
        return -cost;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    //Get a book description (I don't think this is actually used.)
    @Override
    public String getBookDescription() {
        return "Example text. (Uses Patchouli formatting)";
    }

    //What is the main item for crafting this glyph
    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }
}
