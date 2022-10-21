package invoker54.magefight.init;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ArrayList<EntityType<?>> entityTypes = new ArrayList<>();
    public static <T extends Entity> EntityType<T> addEntity (EntityType.Builder<T> builder, String resourceLocation){
        ResourceLocation location = new ResourceLocation(ArsMageFight.MOD_ID, resourceLocation);
        EntityType<T> entityType = builder.build(location.toString());
        entityType.setRegistryName(location);
        entityTypes.add(entityType);
        return entityType;
    }

    //This is for the glyph spell entities
    public static final EntityType<DeathGripEntity> DEATH_GRIP_ENTITY = addEntity(EntityType.Builder.<DeathGripEntity>
            of(DeathGripEntity::new, EntityClassification.MISC).fireImmune().setShouldReceiveVelocityUpdates(true).setCustomClientFactory(DeathGripEntity::new).sized(1,1)
            .setTrackingRange(10), "deaths_grip");
    public static final EntityType<BlackHoleEntity> BLACK_HOLE_ENTITY = addEntity(EntityType.Builder.<BlackHoleEntity>
                    of(BlackHoleEntity::new, EntityClassification.MISC).fireImmune().setCustomClientFactory(BlackHoleEntity::new).sized(1,1)
            .setTrackingRange(10), "black_hole");

    public static final EntityType<TimeAnchorEntity> TIME_ANCHOR_ENTITY = addEntity(EntityType.Builder.<TimeAnchorEntity>
                    of(TimeAnchorEntity::new, EntityClassification.MISC).noSave().fireImmune().setCustomClientFactory(TimeAnchorEntity::new).sized(1,1)
            .setTrackingRange(10), "time_anchor");

    public static final EntityType<SpellShieldEntity> SPELL_SHIELD_ENTITY = addEntity(EntityType.Builder.<SpellShieldEntity>
                    of(SpellShieldEntity::new, EntityClassification.MISC).noSave().fireImmune().setCustomClientFactory(SpellShieldEntity::new).sized(1,1)
            .setTrackingRange(10), "spell_shield");

    public static final EntityType<RuptureSwordEntity> RUPTURE_SWORD_ENTITY = addEntity(EntityType.Builder.<RuptureSwordEntity>
                    of(RuptureSwordEntity::new, EntityClassification.MISC).noSave().fireImmune().setCustomClientFactory(RuptureSwordEntity::new).sized(1,1)
            .setTrackingRange(10), "rupture_sword");

    public static final EntityType<ComboEntity> COMBO_ENTITY = addEntity(EntityType.Builder.<ComboEntity>
                    of(ComboEntity::new, EntityClassification.MISC).noSave().fireImmune().setCustomClientFactory(ComboEntity::new).sized(1,1)
            .setTrackingRange(10), "combo_entity");

    public static final EntityType<LifeSigilEntity> LIFE_SIGIL_ENTITY = addEntity(EntityType.Builder.<LifeSigilEntity>
                    of(LifeSigilEntity::new, EntityClassification.MISC).noSave().fireImmune().setCustomClientFactory(LifeSigilEntity::new).sized(1,1)
            .setTrackingRange(10), "life_sigil");
    public static final EntityType<BloodSlimeEntity> BLOOD_SLIME_ENTITY = addEntity(EntityType.Builder.<BloodSlimeEntity>
                of(BloodSlimeEntity::new, EntityClassification.CREATURE).setShouldReceiveVelocityUpdates(true).sized(2.04F, 2.04F)
        .setTrackingRange(10), "blood_slime");

    public static final EntityType<ManaSlimeEntity> MANA_SLIME_ENTITY = addEntity(EntityType.Builder.<ManaSlimeEntity>
                    of(ManaSlimeEntity::new, EntityClassification.MISC).noSave().setShouldReceiveVelocityUpdates(true).sized(2.04F, 2.04F)
            .setTrackingRange(10), "mana_slime");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        for (EntityType<?> entityType : entityTypes){
            registry.register(entityType);
            LOGGER.debug("JUST REGISTERED ENTITY: " + entityType.getRegistryName());
        }
    }

    @SubscribeEvent
    public static void attributeEvent(EntityAttributeCreationEvent event){
        event.put(DEATH_GRIP_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(BLACK_HOLE_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(TIME_ANCHOR_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(SPELL_SHIELD_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(RUPTURE_SWORD_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(COMBO_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(LIFE_SIGIL_ENTITY, LivingEntity.createLivingAttributes().build());
        event.put(BLOOD_SLIME_ENTITY, MonsterEntity.createMonsterAttributes().build());
        event.put(MANA_SLIME_ENTITY, MonsterEntity.createMonsterAttributes().build());
    }

}
