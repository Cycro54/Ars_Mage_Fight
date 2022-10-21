package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.magefight.potion.ComboPotionEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ComboEffect extends AbstractEffect {

    public static ComboEffect INSTANCE = new ComboEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private ComboEffect() {
        super("combo", "Combo");
    }

    //This would make it so the effect only works on entities (might not need it though.)
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    //Old way of doing Combo
//    @Override
//    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
//        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
//        spellContext.setCanceled(true);
//        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size()) return;
//        while (rayTraceResult.getEntity() instanceof PartEntity) {
//            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
//        }
//        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
//
//        //This was the LivingEntity hit.
//        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();
//
//        List<AbstractSpellPart> spellParts = spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size());
//
//        //Each extra combo glyph adds 3 seconds, and 2 extra hits
//        //Base combo is 3
//        int extraCombo = (int) spellParts.stream().filter((spellPart -> spellPart instanceof ComboEffect)).count();
//        //Remove all of those combo effects
//        spellParts.removeIf(spellPart -> spellPart instanceof ComboEffect);
//
//        //Make the new spell
//        Spell newSpell =  new Spell(new ArrayList<>(spellParts));
//        SpellContext newContext = new SpellContext(newSpell, shooter).withColors(spellContext.colors);
//        SpellResolver resolver = new SpellResolver(newContext);
////        hitEntity.addEffect(new EffectInstance(EffectInit.COMBO_EFFECT, 30 * 20, 0));
//        //Save the combo for later use
//        ComboPotionEffect.startCombo(hitEntity, resolver, extraCombo);
////        //Then finally cast the combo
////        ComboPotionEffect.castCombo(hitEntity, extraCombo);
//    }
    //New way
    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity) {
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
        if (shooter == null) return;

        //This was the LivingEntity hit.
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        if (hitEntity.getId() == shooter.getId()) {
            ComboPotionEffect.startCombo(hitEntity, spellStats);
        }
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
        return "Example text. (Use Patchouli formatting)";
    }

    //What is the main item for crafting this glyph
    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
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
        return augmentSetOf(AugmentAmplify.INSTANCE);
    }
}
