package invoker54.magefight.entity;

import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManaSlimeEntity extends SlimeEntity {
    private static final Logger POGGER = LogManager.getLogger();
    public static final String manaSlimeString = "MANA_SLIME_DATA";
    public static final String ownerString = "OWNER_ID";
    public static final String manaIncreaseString = "MANA_INC_FLOAT";
    public CompoundNBT slimeNBT;

    @Override
    public void remove(boolean p_remove_1_) {
//        POGGER.debug("BEGIN REMOVE");
        this.removed = true;
        if (!p_remove_1_) {
            this.invalidateCaps();
        }
//        POGGER.warn("END REMOVE");
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damage) {
        if (damageSource == DamageSource.OUT_OF_WORLD) return super.hurt(damageSource, damage);

        if (damageSource == null || damageSource.getEntity() == null){
            return false;
        }

        LivingEntity attacker = (LivingEntity) damageSource.getEntity();
        if (!(attacker.getId() == slimeNBT.getInt(ownerString)) || !attacker.isCrouching()){
            return false;
        }

        this.setHealth(0);
        return super.hurt(damageSource, damage);
    }

    @Override
    public boolean removeWhenFarAway(double p_213397_1_) {
        return true;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        LivingEntity owner = (LivingEntity) this.level.getEntity(slimeNBT.getInt(ownerString));
        if (owner == null || (slimeNBT.getInt(ownerString) != ClientUtil.mC.player.getId())) return super.shouldRender(x, y, z);

        return owner.isCrouching();
    }

    @Override
    public boolean isPickable() {
        LivingEntity owner = (LivingEntity) this.level.getEntity(slimeNBT.getInt(ownerString));
        if (owner == null || owner.isCrouching()) return super.isPickable();

        return false;
    }

    public ManaSlimeEntity(World level, int hitEntity, int size, float manaIncreasePercentage, Vector3d startPosition) {
        this(EntityInit.MANA_SLIME_ENTITY, level);
        //Assign who they should be following
        slimeNBT.putInt(ownerString, hitEntity);

        //This is for mana AND how much the player should be slowed
        slimeNBT.putFloat(manaIncreaseString, manaIncreasePercentage);

        //Set there size
        this.setSize(size, false);


        //Where shall I place you
        this.moveTo(startPosition.x(), startPosition.y(), startPosition.z());
    }

    public ManaSlimeEntity(EntityType<ManaSlimeEntity> BloodSlimeEntityType, World world) {
        super(EntityInit.MANA_SLIME_ENTITY, world);
        slimeNBT = MagicDataCap.getCap(this).getTag(manaSlimeString);
    }

    public ManaSlimeEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        this(EntityInit.MANA_SLIME_ENTITY, world);
    }

    @Override
    public void tick() {
        if (this.isAlive()) refreshNBT();
        super.tick();
        if (!this.isAlive()) return;

        LivingEntity owner = (LivingEntity) this.level.getEntity(slimeNBT.getInt(ownerString));
        if (owner == null){
            if (!this.level.isClientSide) this.kill();
            return;
        }

        //This will be for slowing down its owner
        Vector3d delta = owner.getDeltaMovement();
        if (delta.x() != 0 || delta.z() != 0){
            float percent = (1 - slimeNBT.getFloat(manaIncreaseString));
            owner.setDeltaMovement(delta.x() * percent, delta.y(), delta.z() * percent);
        }

        //region This will be for moving towards owner
        AxisAlignedBB boundBox = owner.getBoundingBox();
        Vector3d newPos = boundBox.getCenter();
        newPos = new Vector3d(newPos.x(), boundBox.maxY + 0.1F, newPos.z());

        //Now set the mana slime's position to be above the hit entity
        this.moveTo(newPos.x(), newPos.y(), newPos.z());
        //Also set the rotation
        this.setYBodyRot(owner.yBodyRot);
        this.setYHeadRot(owner.yHeadRot);
        //endregion

        if (this.level.isClientSide) {
        }
    }

    public void refreshNBT(){
        slimeNBT = MagicDataCap.getCap(this).getTag(manaSlimeString);
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
        return EntityInit.MANA_SLIME_ENTITY;
    }

    @Override
    protected void dealDamage(LivingEntity hitEntity) {
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (this.level.isClientSide) return;

        //This is for physics.
        this.setNoAi(true);
        this.setNoGravity(true);
        this.noPhysics = true;

        //This is for mana calculation
        PlayerEntity player = (PlayerEntity) this.level.getEntity(slimeNBT.getInt(ownerString));

        IMana mana = ManaCapability.getMana(player).resolve().get();
        mana.setMaxMana(ManaUtil.getMaxMana(player));
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.level.isClientSide) return;

        PlayerEntity player = (PlayerEntity) this.level.getEntity(slimeNBT.getInt(ownerString));

        IMana mana = ManaCapability.getMana(player).resolve().get();
        mana.removeMana(mana.getMaxMana() * slimeNBT.getFloat(manaIncreaseString));
        mana.setMaxMana(ManaUtil.getMaxMana(player));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class MobEvents{
        @SubscribeEvent
        public static void calcMana(MaxManaCalcEvent event){
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (player.level.isClientSide) return;
            ServerWorld world = (ServerWorld) player.level;

            for (Entity entity : world.getAllEntities()){
                if (!(entity instanceof ManaSlimeEntity)) continue;

                ManaSlimeEntity slime = (ManaSlimeEntity) entity;
                event.setMax((int) (event.getMax() + (event.getMax() * slime.slimeNBT.getFloat(manaIncreaseString))));
            }
        }

        @SubscribeEvent
        public static void onTrack(PlayerEvent.StartTracking event){
            if (!(event.getTarget() instanceof ManaSlimeEntity)) return;

            MagicDataCap.syncToClient((LivingEntity) event.getTarget());
        }
    }
}
