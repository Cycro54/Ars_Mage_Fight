package invoker54.magefight.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LifeSigilEntity extends BasicEntity {
    public static final DamageSource DAMAGE_SOURCE = new DamageSource("spell.life_sigil").bypassArmor().setMagic();
    public static final String lifeSigilString = "LIFE_SIGIL_DATA";
    public static final String rangeString = "RANGE";
    public static final String casterString = "CASTER";
    public static final String healString = "HEAL_AMOUNT";

    public static final int secondsToLive = 20 * 20;
    public static final int growTime = 2 * 20;
    private int newDeathTime = 0;

    public LifeSigilEntity(World world, BlockPos pos, SpellStats stats, LivingEntity caster) {
        super(EntityInit.LIFE_SIGIL_ENTITY, world);
        this.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
        this.yBodyRot = 0;

        MagicDataCap cap = MagicDataCap.getCap(this);
        CompoundNBT capNBT = cap.getTag(lifeSigilString);
        //Range
        capNBT.putInt(rangeString, stats.getBuffCount(AugmentAOE.INSTANCE));
        //Caster
        capNBT.putUUID(casterString, caster.getUUID());
        //Heal amount
        capNBT.putInt(healString, 2 + (stats.getBuffCount(AugmentAmplify.INSTANCE)));
    }

    public LifeSigilEntity(EntityType<LifeSigilEntity> blackHoleEntityType, World world) {
        super(EntityInit.LIFE_SIGIL_ENTITY, world);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level.isClientSide) MagicDataCap.syncToClient(this);
        this.setNoGravity(true);
        this.noCulling = true;
        this.noPhysics = true;
        this.setInvulnerable(true);

        //Check for other nearby life sigils (that are from this one's caster)
        if (!this.level.isClientSide && this.isAlive()) {
            UUID ownerID = MagicDataCap.getCap(this).getTag(lifeSigilString).getUUID(casterString);
            for (LivingEntity target : this.level.getEntitiesOfClass(LifeSigilEntity.class, this.getBoundingBox())) {
                if (target == this) continue;
                if (MagicDataCap.getCap(this).getTag(lifeSigilString).getUUID(casterString).equals(ownerID)) target.remove();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;
        if (!this.isAlive()) return;

        if (this.tickCount >= (growTime + secondsToLive)){
            this.kill();
            return;
        }

        if (tickCount >= growTime){
            float burstProgress = (tickCount - growTime)/20F;

            //Every 5 seconds, heal everything in range.
            if (burstProgress % 4 == 0 & burstProgress >= 0){
                MagicDataCap cap = MagicDataCap.getCap(this);
                if (!cap.hasTag(lifeSigilString)) return;

                CompoundNBT capNBT = cap.getTag(lifeSigilString);
                int range = 1 + capNBT.getInt(rangeString);
                UUID caster = capNBT.getUUID(casterString);

                AxisAlignedBB bounds = this.getBoundingBox().inflate(range);
                List<LivingEntity> entityList = this.level.getEntitiesOfClass(LivingEntity.class, bounds);
                if (entityList.stream().noneMatch((entity -> entity.getUUID().equals(caster)))){
                    this.kill();
                    return;
                }

                for (LivingEntity target : entityList) {
                    if (target == this) continue;
                    if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;
                    if (!target.isAlive()) continue;

                    //This will heal/damage the entity, and move on.
                    if (target.isInvertedHealAndHarm()){
                        target.hurt(DAMAGE_SOURCE, capNBT.getInt(healString));
                    }
                    else{
                        target.heal(capNBT.getInt(healString));
                    }
                }
            }
        }
    }

    public LifeSigilEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        super(EntityInit.LIFE_SIGIL_ENTITY, world);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "sizeControl", 0, this::sizeController));
    }
    private PlayState sizeController(AnimationEvent<LifeSigilEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("grow"));
            POGGER.debug("GROWING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "grow") && control.getAnimationState() == AnimationState.Stopped){
            control.transitionLengthTicks = 0.5F;
            control.setAnimation(new AnimationBuilder().addAnimation("bounce"));
            POGGER.debug("BOUNCING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "bounce") && this.isDeadOrDying()){
            control.setAnimation(new AnimationBuilder().addAnimation("shrink"));
            POGGER.debug("SHRINKING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "shrink") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("idle"));
            POGGER.debug("IDLING");
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void tickDeath() {
        ++newDeathTime;
        if (this.newDeathTime > 3 * 20) {
            this.remove(); //Forge keep data until we revive player
        }
    }

}
