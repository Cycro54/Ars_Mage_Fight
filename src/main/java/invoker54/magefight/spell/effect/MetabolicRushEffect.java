package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.event.ManaCapEvents;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MetabolicRushEffect extends AbstractEffect {

    public static MetabolicRushEffect INSTANCE = new MetabolicRushEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public ForgeConfigSpec.DoubleValue MANA_REGEN_PER_EXHAUST;

    private MetabolicRushEffect() {
        super("metabolic_rush", "Metabolic Rush");
    }

    //This would make it so the effect only works on entities (might not need it though.)
//    @Override
//    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        return rayTraceResult instanceof EntityRayTraceResult;
//    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (shooter == null) return;
        // LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        if (rayTraceResult.getEntity().getId() != shooter.getId()) return;

        //Then do the spell thing here
        shooter.addEffect(new EffectInstance(EffectInit.METABOLIC_RUSH_EFFECT, (6 + (3 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE))) * 20, spellStats.getBuffCount(AugmentAmplify.INSTANCE)));
        //The shooters mana
        IMana mana = ManaCapability.getMana(shooter).resolve().get();
        if (mana.getCurrentMana() == mana.getMaxMana()) mana.setMana(mana.getMaxMana() - 0.01F);
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

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        INSTANCE.MANA_REGEN_PER_EXHAUST = (builder.comment("Mana regen per exhaust (1 exhaust is 1/4 of 1 food)").defineInRange("Mana_Per_Exhaust", 1F, 0, Integer.MAX_VALUE));
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE);
    }
}
