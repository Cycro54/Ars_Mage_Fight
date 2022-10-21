package invoker54.magefight.potion;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.init.EffectInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class FatalBondPotionEffect extends Effect {
    public static final DamageSource FATAL_BOND_DEATH = new DamageSource("spell.fatal_bond").bypassArmor().setMagic();
    public static final int effectColor = new Color(196, 52, 6,255).getRGB();

    private static final Logger LOGGER = LogManager.getLogger();

    private static final ArrayList<UUID> checkedEntities = new ArrayList<>();

    public FatalBondPotionEffect(EffectType effectType) {
        super(effectType, effectColor);
    }

    public static final String fatalBondString = "FATAL_BOND_STRING";
    public static final String master_link = "MASTER_LINK";
    public static final String slave_link = "SLAVE_LINK";
    public static final String slave_linkID = "SLAVE_LINK_ID";

    public static void startBinding(int mobCount, boolean isSensitive, LivingEntity hitEntity, LivingEntity caster){
        if (hitEntity.level.isClientSide){
            LOGGER.warn("FATAL BOND ENTITY IS ON CLIENT SIDE, WHY??");
            return;
        }

        LivingEntity newMaster = hitEntity;

        //This will be for refreshing and finding the last slave to be the newMaster
        ArrayList<LivingEntity> bondedEntities = new ArrayList<>();
        bondedEntities.add(hitEntity);
        if (hitEntity.hasEffect(EffectInit.FATAL_BOND_EFFECT)){

            //Master first
            LivingEntity mainMaster = hitEntity;
            while (getMaster(mainMaster) != null){
                mainMaster = getMaster(mainMaster);
                bondedEntities.add(mainMaster);

                //Refresh their effect while here
                mainMaster.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));
                LOGGER.warn("OUTMASTERTICK");
            }

            //Now for the slaves
            while (getSlave(newMaster) != null){
                newMaster = getSlave(newMaster);
                bondedEntities.add(newMaster);

                //Refresh their effect while here
                newMaster.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));
                LOGGER.warn("OUTSLAVETICK");
            }
        }

        //Give the main entity the bind effect
        hitEntity.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));

        //Now start to search for other entities
        float range = 3 + (mobCount * 3);
        AxisAlignedBB bounds = hitEntity.getBoundingBox().inflate(range);
        for (LivingEntity target : hitEntity.level.getEntitiesOfClass(LivingEntity.class, bounds).stream().sorted((A, B) ->{
            return Float.compare(A.distanceTo(hitEntity), B.distanceTo(hitEntity));
        }).collect(Collectors.toList())) {
            if (bondedEntities.size() >= mobCount + 1) break;
            if (target == hitEntity) continue;
            if (bondedEntities.contains(target)) continue;
            if (target.getUUID().equals(caster.getUUID())) continue;
            if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;
            if (isSensitive && hitEntity.getClass() != target.getClass()) continue;

            //IF the current target has the fatal bond effect
            if (target.hasEffect(EffectInit.FATAL_BOND_EFFECT)){

                /** THIS IS FOR MASTERS OF TARGET */
                LivingEntity oldMaster = target;
                while (getMaster(oldMaster) != null){
                    oldMaster = getMaster(oldMaster);
                    bondedEntities.add(oldMaster);

                    //Refresh their effect while here
                    oldMaster.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));
                    LOGGER.error("INMASTERTICK");
                }

                //On the last master, I have to assign the newMaster as the master
                setMaster(oldMaster, newMaster);

                //Next, on the newMaster's cap I have to assign the last master as the slave
                setSlave(newMaster, oldMaster);

                /** THIS IS FOR SLAVES OF TARGET*/
                //Now for the slaves
                while (getSlave(newMaster) != null){
                    newMaster = getSlave(newMaster);
                    bondedEntities.add(newMaster);

                    //Refresh their effect while here
                    newMaster.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));

                    LOGGER.error("INSLAVETICK");
                }
            }
            else {
                setSlave(newMaster, target);
                setMaster(target, newMaster);
                bondedEntities.add(target);

                //Now make the slave into the new master
                newMaster = target;
            }

            target.addEffect(new EffectInstance(EffectInit.FATAL_BOND_EFFECT, 30 * 20, 0));
        }
    }
    public static LivingEntity getSlave(LivingEntity entity){
        ServerWorld world = (ServerWorld) entity.level;

        CompoundNBT nbt = MagicDataCap.getCap(entity).getTag(fatalBondString);
        return nbt.contains(slave_link) ? (LivingEntity) world.getEntity(nbt.getUUID(slave_link)) : null;
    }
    public static void setSlave(LivingEntity masterEntity, LivingEntity slaveEntity){
        CompoundNBT masterNBT = MagicDataCap.getCap(masterEntity).getTag(fatalBondString);
        if (slaveEntity != null) {
            masterNBT.putUUID(slave_link, slaveEntity.getUUID());
            masterNBT.putInt(slave_linkID, slaveEntity.getId());
        }
        else {
            masterNBT.remove(slave_link);
            masterNBT.remove(slave_linkID);
        }
        MagicDataCap.syncToClient(masterEntity);
    }
    public static LivingEntity getMaster(LivingEntity entity){
        ServerWorld world = (ServerWorld) entity.level;

        CompoundNBT nbt = MagicDataCap.getCap(entity).getTag(fatalBondString);
        return nbt.contains(master_link) ? (LivingEntity) world.getEntity(nbt.getUUID(master_link)) : null;
    }
    public static void setMaster(LivingEntity slaveEntity, LivingEntity masterEntity){
        CompoundNBT slaveNBT = MagicDataCap.getCap(slaveEntity).getTag(fatalBondString);

        if (masterEntity != null) slaveNBT.putUUID(master_link, masterEntity.getUUID());
        else slaveNBT.remove(master_link);

        MagicDataCap.syncToClient(slaveEntity);
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PotionEvents{
        @SubscribeEvent
        public static void onDeath(LivingDeathEvent event){
            if (event.isCanceled()) return;
            resetEffects(event.getEntityLiving());
        }
        @SubscribeEvent
        public static void onExpire(PotionEvent.PotionExpiryEvent event){
            if (event.isCanceled()) return;
            if (event.getPotionEffect() == null) return;
            if (!event.getPotionEffect().getEffect().equals(EffectInit.FATAL_BOND_EFFECT)) return;
            resetEffects(event.getEntityLiving());
        }
        @SubscribeEvent
        public static void onRemove(PotionEvent.PotionRemoveEvent event){
            if (event.isCanceled()) return;
            if (event.getPotion() != EffectInit.FATAL_BOND_EFFECT) return;
            resetEffects(event.getEntityLiving());
        }
        @SubscribeEvent
        public static void onDimensionChange(EntityTravelToDimensionEvent event){
            if (!(event.getEntity() instanceof LivingEntity)) return;
            if (!((LivingEntity) event.getEntity()).hasEffect(EffectInit.FATAL_BOND_EFFECT)) return;
            ((LivingEntity) event.getEntity()).removeEffect(EffectInit.FATAL_BOND_EFFECT);
        }
        protected static void resetEffects(LivingEntity entity){
            if (entity.level.isClientSide) return;

            LivingEntity masterEntity = getMaster(entity);
            LivingEntity slaveEntity = getSlave(entity);

            if (masterEntity != null){
                setSlave(masterEntity, slaveEntity);
            }
            if (slaveEntity != null){
                setMaster(slaveEntity, masterEntity);
            }

            //Remove this entities cap data last
            MagicDataCap.getCap(entity).removeTag(fatalBondString);

            //Sync to clients
            if (entity.isAlive()) MagicDataCap.syncToClient(entity);
        }
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDamage(LivingHurtEvent event){
            if (event.getEntityLiving().level.isClientSide) return;
            if (event.isCanceled()) return;
            if (!event.getEntityLiving().hasEffect(EffectInit.FATAL_BOND_EFFECT)) return;
            if (checkedEntities.contains(event.getEntityLiving().getUUID())) return;

            LivingEntity hurtEntity = event.getEntityLiving();
            checkedEntities.add(hurtEntity.getUUID());

            if (getMaster(hurtEntity) != null) getMaster(hurtEntity).hurt(event.getSource(), event.getAmount());
            if (getSlave(hurtEntity) != null) getSlave(hurtEntity).hurt(event.getSource(), event.getAmount());

            LOGGER.warn("THIS IS THE DAMAGE YOU WILL TAKE: " + (event.getAmount() * 0.5F));
            LOGGER.warn("THIS IS HOW MUCH HEALTH YOU WILL HAVE: " + (event.getEntityLiving().getHealth() - (event.getAmount() * 0.5F)));
            event.setAmount(event.getAmount() * 0.5F);
        }
        @SubscribeEvent
        public static void wipeArray(TickEvent.ServerTickEvent event){
            if (event.phase == TickEvent.Phase.START) return;

            if (!checkedEntities.isEmpty()) checkedEntities.clear();
        }
    }
}
