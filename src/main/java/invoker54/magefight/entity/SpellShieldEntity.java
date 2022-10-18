package invoker54.magefight.entity;

import invoker54.magefight.init.EffectInit;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpellShieldEntity extends FollowerEntity {
    private static final Logger POGGER = LogManager.getLogger();

    public SpellShieldEntity(EntityType<? extends LivingEntity> spellShieldEntityEntityType, World world) {
        super(spellShieldEntityEntityType, world);
    }

    public SpellShieldEntity(World world, Vector3d position, LivingEntity trackedEntity) {
        super(EntityInit.SPELL_SHIELD_ENTITY, world, position, trackedEntity);
    }

    public SpellShieldEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.SPELL_SHIELD_ENTITY, world);
    }

    @Override
    protected boolean hasTrackEffect(LivingEntity trackedEntity) {
        return trackedEntity.hasEffect(EffectInit.STALWART_EFFECT);
    }
}
