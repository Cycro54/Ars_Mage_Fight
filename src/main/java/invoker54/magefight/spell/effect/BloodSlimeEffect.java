package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.entity.BloodSlimeEntity;
import invoker54.magefight.spell.CalcUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class BloodSlimeEffect extends AbstractEffect {

    public static BloodSlimeEffect INSTANCE = new BloodSlimeEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private BloodSlimeEffect() {
        super("blood_slime", "Blood Slime");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        // LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof MobEntity)) return;

        //This was the LivingEntity hit.
        MobEntity hitEntity = (MobEntity) rayTraceResult.getEntity();


        //Make sure the entity hit has ai
        if (hitEntity.isNoAi()) return;
        //Make sure they are living
        if (!hitEntity.isAlive()) return;
        //And make sure the entity is NOT a blood slime
        if (hitEntity instanceof BloodSlimeEntity) return;

        //Make sure there isn't already a blood slime
        //This will be used for checking if the hitEntity has a blood slime
        MagicDataCap hitEntityCap = MagicDataCap.getCap(hitEntity);
        if (hitEntityCap.hasTag(BloodSlimeEntity.bloodSlimeString)) {
            Entity slimeEntity = world.getEntity(hitEntityCap.getTag(BloodSlimeEntity.bloodSlimeString).getInt(BloodSlimeEntity.bloodSlimeFollower));
            if (slimeEntity instanceof BloodSlimeEntity && slimeEntity.isAlive()){
                return;
            }
        }

        //Calculate the carry health
        float carryHealth = new CalcUtil(6).healthMultiply(hitEntity.getMaxHealth(), 0.5F).compile();

        //Then do the spell thing here
        BloodSlimeEntity slimeEntity = new BloodSlimeEntity(shooter.getCommandSenderWorld(), hitEntity, shooter.getUUID(), true, carryHealth, rayTraceResult.getLocation());
        world.addFreshEntity(slimeEntity);
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
