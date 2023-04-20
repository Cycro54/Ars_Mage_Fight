package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.entity.LifeSigilEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class LifeSigilEffect extends AbstractEffect {

    public static LifeSigilEffect INSTANCE = new LifeSigilEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private LifeSigilEffect() {
        super("life_sigil", "Life Sigil");
    }

    //This would make it so the effect only works on entities (might not need it though.)
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof BlockRayTraceResult;
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockRayTraceResult origRayTrace = rayTraceResult;
        Material hitMat = world.getBlockState(rayTraceResult.getBlockPos()).getMaterial();
        //Count is the amount of blocks this will go down in search for the perfect spot.
        int count = 4;
        while (!hitMat.blocksMotion() && !hitMat.isLiquid() && count > 0){
            rayTraceResult = rayTraceResult.withPosition(rayTraceResult.getBlockPos().below());
            count--;
            if (count == 0) rayTraceResult = origRayTrace;
        }

        LifeSigilEntity lifeSigil = new LifeSigilEntity(world, rayTraceResult.getBlockPos().above(), spellStats, shooter);

        world.addFreshEntity(lifeSigil);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 45;
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
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }
}
