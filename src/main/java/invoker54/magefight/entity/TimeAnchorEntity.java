package invoker54.magefight.entity;

import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import invoker54.magefight.spell.effect.RewindEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3d;
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

import static invoker54.magefight.potion.RupturePotionEffect.ruptureData;

public class TimeAnchorEntity extends BasicEntity {
    public static final String timeEntityString = "TIME_ENTITY_ID";

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level.isClientSide) MagicDataCap.syncToClient(this);

        //This is for physics.
//        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    public TimeAnchorEntity(EntityType<TimeAnchorEntity> timeAnchorEntity, World world) {
        super(EntityInit.TIME_ANCHOR_ENTITY, world);
    }

    public TimeAnchorEntity(World world, LivingEntity hitEntity, Vector3d pos) {
        this(EntityInit.TIME_ANCHOR_ENTITY, world);
        MagicDataCap.getCap(this).getTag(RewindEffect.rewindString).putInt(timeEntityString, hitEntity.getId());
        this.moveTo(pos);
    }

    public TimeAnchorEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.TIME_ANCHOR_ENTITY, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide) return;
        if (!this.isAlive()) return;

        LivingEntity timeEntity = getOwner();
        if (timeEntity == null || !timeEntity.isAlive() || (!timeEntity.hasEffect(EffectInit.REWIND_EFFECT) && !this.level.isClientSide)){
            if (!this.level.isClientSide) this.remove();
            return;
        }

        if (!timeEntity.isAlive() || !timeEntity.hasEffect(EffectInit.REWIND_EFFECT)){
            this.remove();
        }
    }

    public LivingEntity getOwner(){
        return (LivingEntity) this.level.getEntity(MagicDataCap.getCap(this).getTag(RewindEffect.rewindString).getInt(timeEntityString));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "mainControl", 0, this::mainController));
    }
    private PlayState mainController(AnimationEvent<TimeAnchorEntity> event){
        AnimationController control = event.getController();
        if (control.getCurrentAnimation() == null) {
            control.setAnimation(new AnimationBuilder().addAnimation("Idle"));
        }
        return PlayState.CONTINUE;
    }
}
