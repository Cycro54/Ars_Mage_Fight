package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import invoker54.magefight.potion.BattleHungerPotionEffect;
import invoker54.magefight.spell.CalcUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class BattleHungerEffect extends AbstractEffect {

    public static BattleHungerEffect INSTANCE = new BattleHungerEffect();
    private static final Logger LOGGER = LogManager.getLogger();
    private BattleHungerEffect() {
        super("battle_hunger", "Battle Hunger");
    }

    //This would make it so the effect only works on entities (might not need it though.)
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        // LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
        if (!(shooter instanceof PlayerEntity)) return;

        float damage = 0F;
        damage = new CalcUtil(8).manaMultiplier(shooter, 0.5F).compile();

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Cast onto the hit entity
        BattleHungerPotionEffect.castBattleHunger(hitEntity, shooter, damage, 1 + (spellStats.getBuffCount(AugmentAmplify.INSTANCE)));
        //Then cast onto the caster if (
        BattleHungerPotionEffect.castBattleHunger(shooter, shooter,
                damage,1 + (spellStats.getBuffCount(AugmentAmplify.INSTANCE) - spellStats.getBuffCount(AugmentDampen.INSTANCE)));
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 90;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }
}
