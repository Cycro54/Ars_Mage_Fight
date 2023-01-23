package invoker54.magefight.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.ibm.icu.text.UFormat;
import com.mojang.brigadier.arguments.FloatArgumentType;
import invoker54.magefight.entity.DeathGripEntity;
import invoker54.magefight.init.EntityInit;
import jdk.jfr.Threshold;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.entity.PartEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeathGripEffect extends AbstractEffect{

    public static DeathGripEffect INSTANCE = new DeathGripEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public ForgeConfigSpec.DoubleValue MAX_HOLD_MOB;
    @Nullable
    public ForgeConfigSpec.DoubleValue MAX_HOLD_PLAYER;
    @Nullable
    public ForgeConfigSpec.DoubleValue HEALTH_THRESHOLD;

    private DeathGripEffect() {
        super("death_grip", "Death's Grip");
    }
    public void summonHand(World world, LivingEntity shooter, LivingEntity entityHit, List<AbstractAugment> augments){
        if (entityHit == null) return;

        if (world.isClientSide) return;
        if (DeathGripEntity.death_grips.contains(entityHit.getId())) return;

        //Grab time will be halved if it's a player (20 converts it to ticks)
        float maxTime = (float) ((entityHit instanceof PlayerEntity ? INSTANCE.MAX_HOLD_PLAYER.get() : INSTANCE.MAX_HOLD_MOB.get()) * 20);
        //Grab time will be affected by the amount of health missing (40% of the health loss will be max)
        float healthLoss = entityHit.getMaxHealth() - entityHit.getHealth();
        healthLoss = (float) (healthLoss/(entityHit.getMaxHealth() * INSTANCE.HEALTH_THRESHOLD.get()));
        healthLoss = Math.min(healthLoss, 1);
        maxTime = healthLoss * maxTime;

        DeathGripEntity deathGripEntity = new DeathGripEntity(world, entityHit, (int) (maxTime));
        world.addFreshEntity(deathGripEntity);
    }

    //This effect ONLY works on entities. nothing else.
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult;
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        LOGGER.debug("WHAT I HIT? " + rayTraceResult.getEntity().getClass());
        while (rayTraceResult.getEntity() instanceof PartEntity){
            rayTraceResult = new EntityRayTraceResult(((PartEntity<?>) rayTraceResult.getEntity()).getParent());
        }
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        summonHand(world, shooter, hitEntity, spellStats.getAugments());
    }

    @Override
    public int getManaCost() {
        return 240;
    }

    @Override
    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.THREE;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        INSTANCE.MAX_HOLD_MOB = (builder.comment("Max time in seconds for a mob to be held").defineInRange("Max_Hold_Time_Mob", 10F, 0, Integer.MAX_VALUE));
        INSTANCE.MAX_HOLD_PLAYER = (builder.comment("Max time in seconds for a player to be held").defineInRange("Max_Hold_Time_Player", 5F, 0, Integer.MAX_VALUE));
        INSTANCE.HEALTH_THRESHOLD = (builder.comment("Percentage of health to be missing for full effect").defineInRange("Health_Threshold", 0.4F, 0F, 1F));
    }

    @Override
    public String getBookDescription() {
//        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

}
