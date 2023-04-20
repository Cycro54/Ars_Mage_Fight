package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import invoker54.magefight.potion.LifeLeechPotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public class LifeLeechEffect extends AbstractEffect {

    public static LifeLeechEffect INSTANCE = new LifeLeechEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private LifeLeechEffect() {
        super("life_leech", "Life Leech");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Range is controlled by the book tier and amount of aoe (2 aoe = +1 range)
        int amp = 0;
        if (shooter instanceof PlayerEntity){
            Optional<IMana> optional = ManaCapability.getMana(shooter).resolve();
            if (optional.isPresent()) amp = Math.round(Math.min(optional.get().getBookTier() * 2, spellStats.getBuffCount(AugmentAOE.INSTANCE)));
        }
        LifeLeechPotionEffect.castLeech(hitEntity, shooter, amp);
    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 60;
    }

    //Change the tier
    @Override
    public Tier getTier() {
        return Tier.ONE;
    }

    //The augments that will work with this glyph
    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE);
    }
}
