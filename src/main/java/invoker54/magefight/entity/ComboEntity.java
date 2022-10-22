package invoker54.magefight.entity;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.SyncRequestMsg;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static invoker54.magefight.potion.ComboPotionEffect.*;

public class ComboEntity extends FollowerEntity{

    int deathTime = 5 * 20;

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level.isClientSide) {
            NetworkHandler.INSTANCE.sendToServer(new SyncRequestMsg(this.level.dimension().getRegistryName(), this.getId()));
            return;
        }
    }

    public ComboEntity(World world, Vector3d position, LivingEntity trackedEntity, LivingEntity casterEntity) {
        super(EntityInit.COMBO_ENTITY, world, position, trackedEntity);

        //This is for if the caster loses the COMBO effect
        MagicDataCap.getCap(this).getTag(comboString).putInt(casterString, casterEntity.getId());

        POGGER.debug("IS CASTER NULL " + (this.level.getEntity(casterEntity.getId())));
        POGGER.debug("MY ID IS: " + (this.getId()));
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

    @Override
    public void turn(LivingEntity trackedEntity) {
        this.lookAt(EntityAnchorArgument.Type.EYES, ClientUtil.mC.player.getEyePosition(0));
        this.lookAt(EntityAnchorArgument.Type.FEET, ClientUtil.mC.player.getEyePosition(0));
    }

    @Override
    public void registerControllers(AnimationData data) {
        super.registerControllers(data);
        data.addAnimationController(new AnimationController(this, "startController", 0, this::startController));
        data.addAnimationController(new AnimationController(this, "rotateController", 0, this::rotateController));
        data.addAnimationController(new AnimationController(this, "lifeController", 0, this::lifeController));
    }
    private PlayState lifeController(AnimationEvent<RuptureSwordEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Spawn"));
        }
        else if (deathTime != 5 * 20){
            control.setAnimation(new AnimationBuilder().addAnimation("Death"));
        }
        return PlayState.CONTINUE;
    }
    private PlayState startController(AnimationEvent<RuptureSwordEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Spawn"));
        }
        return PlayState.CONTINUE;
    }
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

        CompoundNBT myTag = MagicDataCap.getCap(this).getTag(comboString);
        int casterID = myTag.getInt(casterString);
        LivingEntity caster = (LivingEntity) this.level.getEntity(casterID);

        if (caster == null) return false;

        if (!MagicDataCap.getCap(caster).hasTag(comboString)){
            return --deathTime > 0;
        }

        if (level.isClientSide) return true;
        CompoundNBT casterTag = MagicDataCap.getCap(caster).getTag(comboString);
        List<Integer> hitList = Arrays.stream(casterTag.getIntArray(hitListString)).boxed().collect(Collectors.toList());

//        POGGER.debug("ENTITY IN LIST " + (hitList.contains(trackedEntity.getId())));
//        POGGER.debug("DOES CASTER HAVE COMBO " + (caster.hasEffect(EffectInit.COMBO_EFFECT)));
//        POGGER.debug(hitList);
//        POGGER.debug(trackedEntity.getId());

        return hitList.contains(trackedEntity.getId()) && caster.hasEffect(EffectInit.COMBO_EFFECT);
    }
}
