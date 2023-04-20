package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.potion.RupturePotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class RuptureEffect extends AbstractEffect {

    public static RuptureEffect INSTANCE = new RuptureEffect();
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    public ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
    @Nullable
    public ForgeConfigSpec.DoubleValue HEAL_MULTIPLIER;
    @Nullable
    public ForgeConfigSpec.DoubleValue DAMAGE_MAX;

    private RuptureEffect() {
        super("rupture", "Rupture");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        // LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Get the time
        int time = (9 + (3 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE))) * 20;

        RupturePotionEffect.castRupture(hitEntity, time);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 105;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.TWO;
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
        INSTANCE.DAMAGE_MULTIPLIER = (builder.comment("Multiplies damage done per 1 unit every 5 ticks (default is 10% max health)").defineInRange("Damage_Multiplier", 6F, 0, Integer.MAX_VALUE));
        INSTANCE.HEAL_MULTIPLIER = (builder.comment("Multiplies heals done while standing (default is 5% health)").defineInRange("Health_Multiplier", 2F, 0, Integer.MAX_VALUE));
        INSTANCE.DAMAGE_MAX = (builder.comment("The max percentage of health that may be taken").defineInRange("Damage_Max", 0.75F, 0, 1));
//        LOGGER.debug("THIS IS WHAT THE MULTIPLIER VALUE IS: " + INSTANCE.MULTIPLIER.get());
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE);
    }
}
