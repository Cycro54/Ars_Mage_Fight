package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class ChillingTouchEffect extends AbstractEffect {

    public static ChillingTouchEffect INSTANCE = new ChillingTouchEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private ChillingTouchEffect() {
        super("chilling_touch", "Chilling Touch");
    }

    //This would make it so the effect only works on entities (might not need it though.)
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        if (world.isClientSide) return;
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Then do the spell thing here
        int timeInTicks = (10 + (5 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE))) * 20;
        int stacks = Math.min(7, spellStats.getBuffCount(AugmentAmplify.INSTANCE));
        LOGGER.debug("How Long? " + (timeInTicks/20F));
        LOGGER.debug("What's the value? " + (stacks * 0.25F));
        hitEntity.addEffect(new EffectInstance(EffectInit.CHILL_EFFECT, timeInTicks, stacks));

        //Offset the entities health by a smidge if stacks is high enough to get effect working
        if (stacks > 3){
            hitEntity.setHealth(hitEntity.getHealth() - 0.001f);
        }
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 80;
    }

    //Change the tier
    @Override
    public ISpellTier.Tier getTier() {
        return Tier.TWO;
    }

    //Get a book description (I don't think this is actually used.)
    @Override
    public String getBookDescription() {
        return "Inflicts the Chilled debuff (10 seconds) on any entity hit which causes their healing to be reduced by 25%. can be amplified to increase the amount of healing lost (By 25%, up to 200%). Values higher than 100% will cause the entity to lose health instead.";
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
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentAmplify.INSTANCE);
    }
}
