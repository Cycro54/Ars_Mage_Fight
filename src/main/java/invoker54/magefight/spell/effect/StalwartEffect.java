package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.potion.StalwartPotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.acl.Owner;
import java.util.Set;

public class StalwartEffect extends AbstractEffect {

    public static StalwartEffect INSTANCE = new StalwartEffect();
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    public ForgeConfigSpec.DoubleValue MAX_MANA_MULTIPLIER;
    @Nullable
    public ForgeConfigSpec.DoubleValue REGEN_MULTIPLIER;

    private StalwartEffect() {
        super("stalwart", "Stalwart");
    }

    //This would make it so the effect only works on entities (might not need it though.)
//    @Override
//    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
//        return rayTraceResult instanceof EntityRayTraceResult;
//    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        if (hitEntity instanceof ISummon){
            shooter = (LivingEntity) ((ISummon)hitEntity).getOwner((ServerWorld) shooter.getCommandSenderWorld());
            if (shooter == null) return;
        }

        //Then do the spell thing here
        StalwartPotionEffect.castStalwart(shooter, spellStats, hitEntity);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 165;
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
        INSTANCE.MAX_MANA_MULTIPLIER = (builder.comment("Cost multiplier per 100 max mana (per 1 damage)").defineInRange("Max_Mana_Cost_Multiplier", 3F, 0, Integer.MAX_VALUE));
        INSTANCE.REGEN_MULTIPLIER = (builder.comment("Cost multiplier for mana regen (per total damage)").defineInRange("Regen_Cost_Multiplier", 4F, 0, Integer.MAX_VALUE));
//        LOGGER.debug("THIS IS WHAT THE MULTIPLIER VALUE IS: " + INSTANCE.MULTIPLIER.get());
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }
}
