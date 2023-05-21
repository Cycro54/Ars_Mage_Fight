package invoker54.magefight.entity;

import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Objects;

public class TemplateEntity extends LivingEntity implements IAnimatable {
    private static final Logger POGGER = LogManager.getLogger();

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public void push(Entity pEntity) {

    }

    @Override
    public void push(double pX, double pY, double pZ) {

    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public void knockback(float p_233627_1_, double p_233627_2_, double p_233627_4_) {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level.isClientSide) return;

        //This is for physics.
//        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public TemplateEntity(EntityType<? extends LivingEntity> livingEntityType, World world) {
        super(livingEntityType, world);
    }

    //How much time the black hole has left before it gets desummoned
    public TemplateEntity(World world, LivingEntity caster) {
        this(EntityInit.BLACK_HOLE_ENTITY, world);
    }

    public TemplateEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.BLACK_HOLE_ENTITY, world);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
    if (source != DamageSource.OUT_OF_WORLD) return false;
    return super.hurt(source, damage);
    }

    @Override
    public EntityType<?> getType() {
        return EntityInit.BLACK_HOLE_ENTITY;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.withSize(0, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {

    }

    @Override
    public HandSide getMainArm() {
        return HandSide.LEFT;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "rotateControl", 0, this::rotateController));
        data.addAnimationController(new AnimationController(this, "sizeControl", 0, this::sizeController));
    }

    private PlayState sizeController(AnimationEvent<TemplateEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("grow"));
            POGGER.debug("GROWING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "grow") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("bounce"));
            POGGER.debug("BOUNCING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "bounce") && this.hasEffect(EffectInit.BLACK_HOLE_EFFECT) && this.getEffect(EffectInit.BLACK_HOLE_EFFECT).getDuration() == 0){
            control.setAnimation(new AnimationBuilder().addAnimation("shrink"));
            POGGER.debug("SHRINKING");
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "shrink") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("idle"));
            POGGER.debug("IDLING");
        }
        return PlayState.CONTINUE;
    }
    private PlayState rotateController(AnimationEvent<TemplateEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("rotate"));
        }
        return PlayState.CONTINUE;
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
