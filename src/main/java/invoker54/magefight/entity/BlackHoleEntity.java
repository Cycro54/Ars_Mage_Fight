package invoker54.magefight.entity;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import invoker54.magefight.potion.BlackHolePotionEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.Objects;

public class BlackHoleEntity extends BasicEntity{
    private static final Logger POGGER = LogManager.getLogger();

    //How much time the black hole has left before it gets desummoned
    public BlackHoleEntity(World world, BlockPos pos) {
        super(EntityInit.BLACK_HOLE_ENTITY, world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlackHoleEntity(EntityType<BlackHoleEntity> blackHoleEntityType, World world) {
        super(EntityInit.BLACK_HOLE_ENTITY, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;
        if (!this.isAlive()) return;

        if (!this.hasEffect(EffectInit.BLACK_HOLE_EFFECT)){
            deathTime++;
            if (deathTime > 3 * 20) {
                POGGER.debug("I AM REMOVING THIS BLACK HOLE EFFECT.");
                this.remove();
            }
        }
    }

    public BlackHoleEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        super(EntityInit.BLACK_HOLE_ENTITY, world);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "rotateControl", 0, this::rotateController));
        data.addAnimationController(new AnimationController(this, "sizeControl", 0, this::sizeController));
    }

    private PlayState sizeController(AnimationEvent<BlackHoleEntity> event){
        AnimationController control = event.getController();

        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("grow"));
            POGGER.debug("GROWING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "grow") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("bounce"));
            POGGER.debug("BOUNCING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "bounce") && !(MagicDataCap.getCap(this).hasTag(BlackHolePotionEffect.ATTRACT_STRING))){
            control.setAnimation(new AnimationBuilder().addAnimation("shrink"));
            POGGER.debug("SHRINKING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "shrink") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("idle"));
            POGGER.debug("IDLING");
        }
        return PlayState.CONTINUE;
    }
    private PlayState rotateController(AnimationEvent<BlackHoleEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("rotate"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setNoGravity(true);
        this.noCulling = true;
        this.setInvulnerable(true);
        this.noPhysics = true;

        if (!this.level.isClientSide && this.isAlive()) {
            for (LivingEntity target : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                if (target == this) continue;
                if (target instanceof BlackHoleEntity) {
                    if (!target.hasEffect(EffectInit.BLACK_HOLE_EFFECT)) continue;

                    target.remove();
                }
            }
        }
    }
}
