package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.potion.BlackHolePotionEffect;
import invoker54.magefight.entity.BlackHoleEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class BlackHoleEffect extends AbstractEffect {

    public static BlackHoleEffect INSTANCE = new BlackHoleEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private BlackHoleEffect() {
        super("black_hole", "Black Hole");
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
        BlackHoleEntity blackHole = new BlackHoleEntity(world, hitEntity.blockPosition());

        world.addFreshEntity(blackHole);
        BlackHolePotionEffect.startAttract(blackHole, spellStats, shooter, hitEntity);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if (world.isClientSide) return;
        if (spellStats.hasBuff(AugmentSensitive.INSTANCE)) return;

//        LOGGER.debug("IT WILL NOW LIVE (BLACK HOLE I MEAN)");
//        if (spellContext.caster == null) LOGGER.debug( "CASTER IS NULL");
        BlockPos pos = rayTraceResult.getBlockPos().above();
        BlackHoleEntity blackHole = new BlackHoleEntity(world, pos);

        world.addFreshEntity(blackHole);
        BlackHolePotionEffect.startAttract(blackHole, spellStats, shooter, null);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 80;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    //Get a book description (I don't think this is actually used.)
    @Override
    public String getBookDescription() {
        return "Cause mobs to be attracted towards point. Can increase pull strength and search radius. Sensitive makes it affect mobs of the same type.";
    }

    //What is the main item for crafting this glyph
    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    @Override
    public void applyConfigPotion(LivingEntity entity, Effect potionEffect, SpellStats spellStats, boolean showParticles) {
        super.applyConfigPotion(entity, potionEffect, spellStats, showParticles);
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE,
                AugmentSensitive.INSTANCE, AugmentExtendTime.INSTANCE);
    }
}
