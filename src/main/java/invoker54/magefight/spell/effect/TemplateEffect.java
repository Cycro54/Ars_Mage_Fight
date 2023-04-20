package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TemplateEffect extends AbstractEffect {

    public static TemplateEffect INSTANCE = new TemplateEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private TemplateEffect() {
        super("template", "Template");
    }

    //This would make it so the effect only works on entities (might not need it though.)
//    @Override
//    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        return rayTraceResult instanceof EntityRayTraceResult;
//    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        // LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Then do the spell thing here
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 120;
    }

    //Change the tier
    @Override
    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.THREE;
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
        return augmentSetOf();
    }
}
