package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.potion.ComboPotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ComboEffect extends AbstractEffect {

    public static ComboEffect INSTANCE = new ComboEffect();
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    public ForgeConfigSpec.DoubleValue MANA_LOSS_PER_ENTITY;
    @Nullable
    public ForgeConfigSpec.DoubleValue MANA_GAIN_PER_EXTRA_DAMAGE;
    private ComboEffect() {
        super("combo", "Combo");
    }

    //This would make it so the effect only works on entities (might not need it though.)
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
//        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
        if (shooter == null) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();
        Optional<IMana> mana = ManaCapability.getMana(hitEntity).resolve();
        if (!mana.isPresent() || mana.get().getCurrentMana() < INSTANCE.MANA_LOSS_PER_ENTITY.get()) return;

        if (hitEntity.getId() == shooter.getId()) {
            ComboPotionEffect.startCombo(hitEntity);
        }
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 0;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    //Get a book description (I don't think this is actually used.)
    @Override
    public String getBookDescription() {
        return "Example text. (Use Patchouli formatting)";
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
        INSTANCE.MANA_LOSS_PER_ENTITY = (builder.comment("Mana loss per hit entity").defineInRange("Mana_Loss", 45F, 0, Integer.MAX_VALUE));
        INSTANCE.MANA_GAIN_PER_EXTRA_DAMAGE = (builder.comment("Mana gained per extra damage point unused (won't exceed mana loss per entity)").defineInRange("Mana_Gain_Per_DMG", 4F, 0, Integer.MAX_VALUE));
//        LOGGER.debug("THIS IS WHAT THE MULTIPLIER VALUE IS: " + INSTANCE.MULTIPLIER.get());
    }

    //The augments that will work with this glyph
//    @Nonnull
//    @Override
//    public Set<AbstractAugment> getCompatibleAugments() {
//        List<AbstractAugment> augments = new ArrayList<>();
//        ArsNouveauAPI.getInstance().getSpell_map().values().forEach((part) -> {
//            if (part instanceof AbstractAugment){
////                LOGGER.debug("IT MATCHED: " + part.getName());
//                augments.add((AbstractAugment) part);
//            }
//        });
////        LOGGER.debug(augments);
//        return Collections.unmodifiableSet(new HashSet(augments));
//
//    }
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
