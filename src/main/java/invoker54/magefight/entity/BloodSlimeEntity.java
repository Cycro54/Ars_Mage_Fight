package invoker54.magefight.entity;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class BloodSlimeEntity extends SlimeEntity {
    private static final Logger POGGER = LogManager.getLogger();
    public static final String bloodSlimeString = "BLOOD_SLIME_DATA";
    public static final String casterString = "CASTER_ID";
    public static final String hitEntityString = "HIT_ENTITY_ID";
    //This will only be on the entity the blood slime is following
    public static final String bloodSlimeFollower = "BLOOD_SLIME_ID";
    public static final String mainSlimeString = "MAIN_SLIME_BOOL";
    public static final String carryHealthString = "CARRY_HEALTH_FLOAT";

    public CompoundNBT slimeNBT;
    private boolean spawnedSlimes = false;
    private static final Integer maxLife = 10 * 20;

    private float highestHealth = 1;
    private boolean lastHit = false;

    @Override
    public void remove(boolean p_remove_1_) {
//        POGGER.debug("BEGIN REMOVE");
//        POGGER.debug("THIS SLIME IS DYING!!!!");
        spawnSlimes();
        this.removed = true;
        if (!p_remove_1_) {
            this.invalidateCaps();
        }
//        POGGER.warn("END REMOVE");
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        //For main slime
        if (slimeNBT.getBoolean(mainSlimeString) && damageSource != DamageSource.OUT_OF_WORLD){
            damage = 0;

        }
        //For tiny slimes
        if (tickCount < 10 && !slimeNBT.getBoolean(mainSlimeString)){
            return false;
        }

//        POGGER.debug("BEGIN HURT");
        boolean isHurt = super.hurt(damageSource, damage);

        if (!isHurt) return isHurt;

        if (!slimeNBT.getBoolean(mainSlimeString)){
            if (damageSource.getEntity() != null) {
                LivingEntity attacker = (LivingEntity) damageSource.getEntity();
                damage = 0.01F;
                //If the attacker is the caster, instantly kill this mob
//                POGGER.debug("ATTACKERS UUID " + (attacker.getUUID()));
//                POGGER.debug("STORED CASTER UUID " + (slimeNBT.getUUID(casterString)));
                if (attacker.getUUID().equals(slimeNBT.getUUID(casterString))){
                    this.setHealth(0);
                }
                else {
                    this.setHealth(this.getHealth() - (this.getMaxHealth()/2));
                }

                if (this.getHealth() <= 0){
                    attacker.heal(slimeNBT.getFloat(carryHealthString));
                }
            }
        }
//        POGGER.warn("END HURT");

        return isHurt;
    }

    @Override
    public void heal(float healAmount) {
        //Only 75% of the heal amount will count
        super.heal(healAmount * 0.75F);
        if (slimeNBT.getBoolean(mainSlimeString) && !spawnedSlimes) {
            highestHealth = this.getHealth();

            int size = Math.round(MathHelper.lerp((this.getHealth()/slimeNBT.getFloat(carryHealthString)), 1, 3));
//            POGGER.debug("THIS IS A MAIN SLIME, CHANGING SIZE FROM " + (this.getSize()) + " TO " + (size));
            //Set its size
            if (size != this.getSize()) setSize(size, false);

            //If they have full health, spawn the slimes
            if (this.getHealth() == this.getMaxHealth()){
                if (!lastHit){
                    lastHit = true;
                    return;
                }
                POGGER.debug("I AM MAIN SLIME WITH FULL HEALTH, SPAWN SLIMES");
                this.kill();
            }
        }
    }

    public BloodSlimeEntity(World level, LivingEntity hitEntity, UUID caster, boolean mainSlime, float carryHealth, Vector3d startPosition) {
        this(EntityInit.BLOOD_SLIME_ENTITY, level);
        //is this the main slime?
        slimeNBT.putBoolean(mainSlimeString, mainSlime);
        //Who's the caster
        if (caster != null) slimeNBT.putUUID(casterString, caster);

        //Who's the entity hit
        slimeNBT.putInt(hitEntityString, hitEntity == null ? -1 : hitEntity.getId());
        //This is for the main slime, how much health they may take before exploding

        //How much health can ya carry?
        slimeNBT.putFloat(carryHealthString, carryHealth);

        if (mainSlime){
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(carryHealth);
            //Make sure the main slime starts with 1 health.
            this.setHealth(1);

            //Place this entity id into the hit entities slime data compound
            MagicDataCap hitEntityCap = MagicDataCap.getCap(hitEntity);
            hitEntityCap.getTag(bloodSlimeString).putInt(bloodSlimeFollower, this.getId());
        }
        else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2);
            //Make sure the main slime starts with 1 health.
            this.setHealth(2);
        }

        //Where shall I place you
        this.moveTo(startPosition.x(), startPosition.y(), startPosition.z());
    }

    public BloodSlimeEntity(EntityType<BloodSlimeEntity> BloodSlimeEntityType, World world) {
        super(EntityInit.BLOOD_SLIME_ENTITY, world);
        slimeNBT = MagicDataCap.getCap(this).getTag(bloodSlimeString);
    }

    public BloodSlimeEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.BLOOD_SLIME_ENTITY, world);
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        LivingEntity owner = (LivingEntity) this.level.getEntity(slimeNBT.getInt(hitEntityString));
        if (owner == null || (slimeNBT.getInt(hitEntityString) != ClientUtil.mC.player.getId())) return super.shouldRender(x, y, z);

        return owner.isCrouching();
    }

    protected void spawnSlimes(){
        if (this.level.isClientSide) return;
        if (spawnedSlimes || !slimeNBT.getBoolean(mainSlimeString)) return;
        spawnedSlimes = true;
        POGGER.debug("BEGIN SPAWN SLIMES");

        this.setSize(1, false);
        POGGER.debug("Whats the MAX HEALTH " + this.getMaxHealth());
        POGGER.debug("Whats the TOP HEALTH " + this.getHealth());

        //If lost health is greater than or equal to mininum lost health required, begin to spawn tiny slimes
        if (slimeNBT.getFloat(carryHealthString) / 2F <= highestHealth) {
            int maxSlimes = (int) (highestHealth / (slimeNBT.getFloat(carryHealthString) / 4F));
            POGGER.debug("How much health did it lose? " + highestHealth);
            float healthPerSlime = highestHealth / maxSlimes;
            POGGER.debug("How much health does each slime have? " + highestHealth);
            POGGER.debug("HOW MANY SLIMES? " + maxSlimes);
            for (int a = 0; a < maxSlimes; a++) {
                createTinySlime(healthPerSlime);
            }
        }
        POGGER.warn("BEGIN END SLIMES");
    }

    //This will be used to create the tiny slimes
    protected void createTinySlime(float carryHealth){
        LivingEntity hitEntity = (LivingEntity) this.level.getEntity(slimeNBT.getInt(hitEntityString));
        LivingEntity casterEntity = (LivingEntity) ((ServerWorld)this.level).getEntity(slimeNBT.getUUID(casterString));

        Vector3d spawnPos = (hitEntity == null ? this.position() : hitEntity.position());

        BloodSlimeEntity entity = new BloodSlimeEntity(this.level, null, slimeNBT.getUUID(casterString), false, carryHealth, spawnPos);
        entity.setSize(1,false);
        this.level.addFreshEntity(entity);
        if (casterEntity != null) entity.setTarget(casterEntity);
    }

    @Override
    public void tick() {
        if (this.isAlive()) refreshNBT();
        super.tick();

        if (!this.isAlive()) return;

        if (slimeNBT.getBoolean(mainSlimeString)){
            LivingEntity entityHit = (LivingEntity) this.level.getEntity(slimeNBT.getInt(hitEntityString));
//            POGGER.debug("Does entity equal null? " + (entityHit == null));
            if ((entityHit == null || !entityHit.isAlive())){
                if (!this.level.isClientSide) {
                    POGGER.debug("CANT FIND HIT ENTITY, DYING");
                    this.kill();
                }
                return;
            }

            AxisAlignedBB boundBox = entityHit.getBoundingBox();
            Vector3d newPos = boundBox.getCenter();
            newPos = new Vector3d(newPos.x(), boundBox.maxY + 0.5F, newPos.z());

            //Now set the blood slime's position to be above the hit entity
            this.moveTo(newPos.x(), newPos.y(), newPos.z());
            //Also set the rotation
            this.setYBodyRot(entityHit.yBodyRot);
        }

        if (this.level.isClientSide) return;
        if (this.tickCount >= maxLife){
            POGGER.debug("YOUR TOO OLD");
            this.kill();
        }
    }

    public void refreshNBT(){
        slimeNBT = MagicDataCap.getCap(this).getTag(bloodSlimeString);
    }

    @Override
    protected void setSize(int size, boolean brandNew) {
        POGGER.debug("BEGIN SIZE");
        POGGER.debug("ID SIZE");
        this.entityData.set(ID_SIZE, size);
        POGGER.debug("REAPPLY POS");
        this.reapplyPosition();
        POGGER.debug("REFRESH DIMENSION");
        this.refreshDimensions();

        POGGER.warn("END SIZE");
    }

    @Override
    public EntityType<? extends SlimeEntity> getType() {
        return EntityInit.BLOOD_SLIME_ENTITY;
    }

    @Override
    protected void dealDamage(LivingEntity hitEntity) {

//        if (hitEntity.getUUID().equals(slimeNBT.getUUID(casterString))){
//            return;
//        }
//
//        super.dealDamage(hitEntity);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level.isClientSide) return;

        //if it's a main slime, place in the hashmap
        if (slimeNBT.getBoolean(mainSlimeString)) {
            this.setNoAi(true);
            this.setNoGravity(true);
            this.noPhysics = true;
            MagicDataCap.syncToClient(this);
        }
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class MobEvents{

        //This will cause the slime to take damage as well
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onEntityhurt(LivingDamageEvent event){
            LivingEntity hurtEntity = event.getEntityLiving();
            if (hurtEntity instanceof BloodSlimeEntity) return;
            if (hurtEntity.level.isClientSide) return;
//            POGGER.debug("AM I IN THE SLIMED ENTITY MAP? " + slimed_entities.containsKey(hurtEntity.getUUID()));
//            POGGER.debug("WHATS THE MAP LOOK LIKE? " + slimed_entities.keySet());
            MagicDataCap cap = MagicDataCap.getCap(hurtEntity);
            if (!cap.hasTag(bloodSlimeString)) return;

            Entity bloodSlime = hurtEntity.level.getEntity(cap.getTag(bloodSlimeString).getInt(bloodSlimeFollower));
            if (!(bloodSlime instanceof BloodSlimeEntity)){
                cap.removeTag(bloodSlimeFollower);
                return;
            }

            if (bloodSlime.isAlive())
                ((LivingEntity)bloodSlime).heal(event.getAmount());
        }

        @SubscribeEvent
        public static void setTarget(LivingSetAttackTargetEvent event){
            LivingEntity entity = event.getEntityLiving();
            if (event.getEntityLiving().level.isClientSide) return;
            if (!(entity instanceof BloodSlimeEntity)) return;

            CompoundNBT slimeNBT = MagicDataCap.getCap(entity).getTag(bloodSlimeString);
            if (slimeNBT.getBoolean(mainSlimeString)) return;

            ServerWorld world = (ServerWorld) entity.level;
            LivingEntity caster = (LivingEntity) world.getEntity(slimeNBT.getUUID(casterString));
            if (caster == null) return;
            if (caster == event.getTarget()) return;

            ((BloodSlimeEntity) entity).setTarget(caster);
        }

        @SubscribeEvent
        public static void startTrack (PlayerEvent.StartTracking event){
            if (event.getEntity().level.isClientSide) return;
            if (!(event.getTarget() instanceof BloodSlimeEntity)) return;

            MagicDataCap.syncToClient((LivingEntity) event.getTarget());
        }
    }
}
