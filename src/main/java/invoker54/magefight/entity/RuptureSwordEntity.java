package invoker54.magefight.entity;

import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class RuptureSwordEntity extends FollowerEntity {
    public RuptureSwordEntity(World world, Vector3d position, LivingEntity trackedEntity) {
        super(EntityInit.RUPTURE_SWORD_ENTITY, world, position, trackedEntity);
    }

    public RuptureSwordEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public RuptureSwordEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.RUPTURE_SWORD_ENTITY, world);
    }

    @Override
    protected boolean hasTrackEffect(LivingEntity trackedEntity) {
        return trackedEntity.hasEffect(EffectInit.RUPTURE_EFFECT) && super.hasTrackEffect(trackedEntity);
    }
}
