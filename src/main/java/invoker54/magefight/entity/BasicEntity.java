package invoker54.magefight.entity;

import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.SyncRequestMsg;
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
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BasicEntity extends LivingEntity implements IAnimatable {
    protected static final Logger POGGER = LogManager.getLogger();
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
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

        POGGER.debug("WHATS MY NAME? " + this.getName().getString());
        POGGER.debug("WHATS MY ID? " + this.getId());
        if (this.level.isClientSide){
            NetworkHandler.INSTANCE.sendToServer(new SyncRequestMsg(this.level.dimension().getRegistryName(), this.getId()));
        }

        //This is for physics.
//        this.setNoAi(true);
    }

    public BasicEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (source != DamageSource.OUT_OF_WORLD) return false;
        return super.hurt(source, damage);
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

    private final AnimationFactory factory = new AnimationFactory(this);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "mainControl", 0, this::mainController));
    }

    private PlayState mainController(AnimationEvent<RuptureSwordEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Idle"));
            POGGER.debug("IDLING");
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
