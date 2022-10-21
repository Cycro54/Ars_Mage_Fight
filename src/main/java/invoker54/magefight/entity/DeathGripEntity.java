package invoker54.magefight.entity;

import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
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

import java.util.*;

public class DeathGripEntity extends BasicEntity{
    public static final ArrayList<Integer> death_grips = new ArrayList<>();
    private static final Logger POGGER = LogManager.getLogger();
    private int maxAge = 0;
    public double ownerScale = 0;

    public double ownerX = 0;
    public double ownerZ = 0;

    //Who the hand is attached to
    private static final DataParameter<Integer> OWNER_ID;
    //How much time the hand has left before it gets desummoned
    private static final DataParameter<Integer> TIME_LEFT;
    public DeathGripEntity(World world, LivingEntity owner, int maxAge) {
        super(EntityInit.DEATH_GRIP_ENTITY, world);
        this.getEntityData().set(OWNER_ID, owner.getId());
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        //The +1 is sort of like a grace period, the 0.95833 is the starting animation length
        this.maxAge = (int) (maxAge + ((0.95833 + 1) * 20F));
        death_grips.add(owner.getId());
    }
    public DeathGripEntity(EntityType<DeathGripEntity> deathGripEntityEntityType, World world) {
        super(EntityInit.DEATH_GRIP_ENTITY, world);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.noPhysics = true;
        this.setNoGravity(true);
        this.noCulling = true;
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isDeadOrDying() || !this.isAlive()){
            return;
        }

        LivingEntity owner = (LivingEntity) level.getEntity(this.getEntityData().get(OWNER_ID));
        if (owner == null){
            return;
        }

        if (ownerZ == 0 && ownerX == 0){
            ownerZ = owner.getZ();
            ownerX = owner.getX();
        }

        //Stop it from moving
        //If they can fly then make sure they can't move at all.
        if (owner.position().x != ownerX || owner.position().z != ownerZ) {
            double y = (owner.isNoGravity() ? 0 : owner.getDeltaMovement().y());
            owner.setPos(ownerX, owner.getY(), ownerZ);
            owner.setDeltaMovement(0, y, 0);
            owner.hasImpulse = true;
        }
        this.setPos(owner.getX(), owner.getY(), owner.getZ());

        //Rotate it.
        this.setYBodyRot(owner.yBodyRot);

        if (this.ownerScale == 0){
            //A players bounding box height is almost around 2 blocks.
            //I wish to set the scale for the hand to be as large as the largest number on the owners bounding box.
            AxisAlignedBB boundBox = owner.getBoundingBox();
//            POGGER.debug("WHATS MY bounding box stuff");
//            POGGER.debug("X: " + boundBox.getXsize());
//            POGGER.debug("Y: " + boundBox.getYsize());
//            POGGER.debug("Z: " + boundBox.getZsize());
            this.ownerScale = Math.max(this.ownerScale, boundBox.getXsize());
            this.ownerScale = Math.max(this.ownerScale, boundBox.getYsize());
            this.ownerScale = Math.max(this.ownerScale, boundBox.getZsize());
//            POGGER.debug("WHATS MY OWNER SCALE? " + this.ownerScale);
        }

        if (!level.isClientSide && (this.tickCount >= maxAge || owner.isDeadOrDying()) && !this.isDeadOrDying()){
            this.remove();
            return;
        }
    }

    @Override
    public void remove(boolean keepData) {
        int id = this.entityData.get(OWNER_ID);
        POGGER.debug("PREVIOUS SIZE: " + death_grips.size());
        death_grips.removeIf(number -> number == id);
        POGGER.debug("NEW SIZE: " + death_grips.size());
        super.remove(keepData);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        //Who it is attached to
        this.entityData.define(OWNER_ID, 0);
        //How much time the hand has before its death
        this.entityData.define(TIME_LEFT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("OWNER_ID", this.entityData.get(OWNER_ID));
        tag.putInt("TIME_LEFT", this.entityData.get(TIME_LEFT));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(OWNER_ID, tag.getInt("OWNER_ID"));
        this.entityData.set(TIME_LEFT, tag.getInt("TIME_LEFT"));
    }

    static {
        OWNER_ID = EntityDataManager.defineId(DeathGripEntity.class, DataSerializers.INT);
        TIME_LEFT = EntityDataManager.defineId(DeathGripEntity.class, DataSerializers.INT);
    }

    public DeathGripEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        super(EntityInit.DEATH_GRIP_ENTITY, world);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "baseControl", 0, this::mainController));
        data.addAnimationController(new AnimationController(this, "wristControl", 0, this::wristController));
    }

    private PlayState mainController(AnimationEvent<DeathGripEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("spawn"));
        }
        else if (Objects.equals(control.getCurrentAnimation().animationName, "spawn") && control.getAnimationState() == AnimationState.Stopped){
            control.setAnimation(new AnimationBuilder().addAnimation("idle_fingers"));
        }
        return PlayState.CONTINUE;
    }

    private PlayState wristController(AnimationEvent<DeathGripEntity> event){
        AnimationController control = event.getController();
        //1.15 is the length of the spawn animation, 1.148 is to get rid of the twitch
        if (control.getCurrentAnimation() == null && (tickCount + ClientUtil.mC.getFrameTime()) > (1.15F * 20F)) {
            control.setAnimation(new AnimationBuilder().addAnimation("idle_wrist"));
        }
        return PlayState.CONTINUE;
    }
}
