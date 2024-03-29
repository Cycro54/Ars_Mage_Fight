package invoker54.magefight.entity;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static invoker54.magefight.potion.ComboPotionEffect.*;

public class ComboEntity extends FollowerEntity{

    int deathTime = 5 * 20;

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
//        if (this.level.isClientSide) {
//            NetworkHandler.INSTANCE.sendToServer(new SyncRequestMsg(this.level.dimension().getRegistryName(), this.getId()));
//            return;
//        }
    }
    public ComboEntity(World world, Vector3d position, LivingEntity trackedEntity, LivingEntity casterEntity) {
        super(EntityInit.COMBO_ENTITY, world, position, trackedEntity);

        //This is for if the caster loses the COMBO effect
        MagicDataCap.getCap(this).getTag(comboString).putInt(casterString, casterEntity.getId());

//        POGGER.debug("IS CASTER NULL " + (this.level.getEntity(casterEntity.getId())));
//        POGGER.debug("MY ID IS: " + (this.getId()));
    }

    public ComboEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public ComboEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.COMBO_ENTITY, world);
    }

    @Override
    public double getMyRidingOffset() {
        double offset = super.getMyRidingOffset();
        if (this.getVehicle() != null){
            offset += this.getVehicle().getBbHeight()/2F;
        }

        return offset;
    }

    public LivingEntity getCaster(){
        if (!MagicDataCap.getCap(this).hasTag(comboString)) return null;

        CompoundNBT myTag = MagicDataCap.getCap(this).getTag(comboString);
        int casterID = myTag.getInt(casterString);
        return (LivingEntity) this.level.getEntity(casterID);
    }

    @Override
    public void turn(LivingEntity trackedEntity) {
        this.lookAt(EntityAnchorArgument.Type.EYES, ClientUtil.mC.player.getEyePosition(0));
        this.lookAt(EntityAnchorArgument.Type.FEET, ClientUtil.mC.player.getEyePosition(0));
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
//        data.addAnimationController(new AnimationController(this, "startController", 0, this::startController));
        data.addAnimationController(new AnimationController(this, "rotateController", 0, this::rotateController));
        data.addAnimationController(new AnimationController(this, "lifeController", 0, this::lifeController));
    }
    private PlayState lifeController(AnimationEvent<RuptureSwordEntity> event){
        AnimationController control = event.getController();
        LivingEntity caster = getCaster();

        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Spawn"));
        }
        else if (caster != null && !MagicDataCap.getCap(caster).hasTag(comboString)){
            control.setAnimation(new AnimationBuilder().addAnimation("Death"));
        }
        return PlayState.CONTINUE;
    }
//    private PlayState startController(AnimationEvent<RuptureSwordEntity> event){
//        AnimationController control = event.getController();
//        if (control.getCurrentAnimation() == null) {
//            control.setAnimation(new AnimationBuilder().addAnimation("Spawn"));
//        }
//        return PlayState.CONTINUE;
//    }
    private PlayState rotateController(AnimationEvent<RuptureSwordEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Rotate"));
        }
        return PlayState.CONTINUE;
    }
    @Override
    protected boolean hasTrackEffect(LivingEntity trackedEntity) {
        //This should always have the combo string, if it doesn't then we have to wait a little.
        if (!MagicDataCap.getCap(this).hasTag(comboString)) return true;
        LivingEntity caster = getCaster();

        if (caster == null) return false;

        if (!MagicDataCap.getCap(caster).hasTag(comboString)){
            return --deathTime > 0;
        }

        if (level.isClientSide) return true;
        CompoundNBT casterTag = MagicDataCap.getCap(caster).getTag(comboString);
        List<Integer> hitList = Arrays.stream(casterTag.getIntArray(hitListString)).boxed().collect(Collectors.toList());

        return super.hasTrackEffect(trackedEntity) &&
                hitList.contains(trackedEntity.getId()) &&
                caster.hasEffect(EffectInit.COMBO_EFFECT);
    }
}
