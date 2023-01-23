package invoker54.magefight.entity;

import invoker54.magefight.client.ClientUtil;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FollowerEntity extends BasicEntity {

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.setNoGravity(true);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    public void stopRiding() {
        if (!this.isAlive()) super.stopRiding();
    }

    @Override
    public double getMyRidingOffset() {
        return -this.getVehicle().getPassengersRidingOffset();
    }

    @Override
    public boolean saveAsPassenger(CompoundNBT p_184198_1_) {
        return false;
    }

    public FollowerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    //How much time the black hole has left before it gets desummoned
    public FollowerEntity(EntityType<? extends LivingEntity> entityType, World world, Vector3d position, LivingEntity trackedEntity) {
        super(entityType, world);
        this.moveTo(position);
        this.setYBodyRot(trackedEntity.yBodyRot);
        this.setYHeadRot(trackedEntity.yHeadRot);
        this.startRiding(trackedEntity,true);
    }

    @Override
    public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        if (!this.isPassenger()) return true;

        if (this.getVehicle() != null && this.getVehicle().getId() == ClientUtil.mC.player.getId()){
            PointOfView pointofview = ClientUtil.mC.options.getCameraType();
            return !pointofview.isFirstPerson();
        }
        return super.shouldRender(p_145770_1_, p_145770_3_, p_145770_5_);
    }
    protected boolean hasTrackEffect(LivingEntity trackedEntity){
        return trackedEntity != null && trackedEntity.isAlive();
    }

    public void turn(LivingEntity trackedEntity){
        this.setYBodyRot(trackedEntity.yBodyRot);
        this.setYHeadRot(trackedEntity.getYHeadRot());
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity trackedEntity = (LivingEntity) this.getVehicle();
//        POGGER.debug("IS NULL: " + (trackedEntity == null));
//        POGGER.debug("IS ALIVE: " + (trackedEntity.isAlive()));
//        POGGER.debug("IS CLIENTSIDE: " + (this.level.isClientSide));
//        POGGER.debug("HAS TRACK EFFECT: " + (hasTrackEffect(trackedEntity)));
        if (!this.level.isClientSide && !hasTrackEffect(trackedEntity)){
//            LOGGER.debug("THE RUPTURE ENTITY IS NULL OR DEAD!");
            this.remove();
            return;
        }
        if (!trackedEntity.hasPassenger(this)){
            this.startRiding(trackedEntity,true);
        }

        this.turn(trackedEntity);
    }
}
