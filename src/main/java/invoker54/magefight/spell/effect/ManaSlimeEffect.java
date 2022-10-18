package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.entity.BloodSlimeEntity;
import invoker54.magefight.entity.ManaSlimeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ManaSlimeEffect extends AbstractEffect {

    public static ManaSlimeEffect INSTANCE = new ManaSlimeEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private ManaSlimeEffect() {
        super("mana_slime", "Mana Slime");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
//        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        //Make sure the entity hit is a player
        if(!(hitEntity instanceof PlayerEntity)) return;

        //Make sure they are living
        if (!hitEntity.isAlive()) return;

        //How much mana the player will get AND how much the player will slow down
        float manaIncrease = Math.min(0.75F, 0.15F + (0.15F * spellStats.getBuffCount(AugmentAmplify.INSTANCE)));

        //Their size
        int size = Math.round(MathHelper.lerp((spellStats.getBuffCount(AugmentAmplify.INSTANCE))/4F, 1, 3));

        //Starting position
        Vector3d startPos = rayTraceResult.getLocation().add(0, hitEntity.getBoundingBox().getYsize(), 0);


        ManaSlimeEntity manaSlime = new ManaSlimeEntity(shooter.getCommandSenderWorld(), hitEntity.getId(), size, manaIncrease, startPos);
        world.addFreshEntity(manaSlime);

        //Do a quick check for another slime
            for (ManaSlimeEntity slimeEntity : world.getEntitiesOfClass(ManaSlimeEntity.class, manaSlime.getBoundingBox())) {
                if (slimeEntity == manaSlime) continue;
                if (slimeEntity.slimeNBT.getInt(ManaSlimeEntity.ownerString) == hitEntity.getId()) {
                    slimeEntity.kill();
                }
            }

    }

    //Make sure to change the mana cost
    @Override
    public int getManaCost() {
        return 120;
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
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }
}
