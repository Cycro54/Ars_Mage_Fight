package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectKnockback;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.init.EffectInit;
import invoker54.magefight.spell.CalcUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Optional;

public class ExplosiveBloodCharm extends Item implements ICurioItem, IManaEquipment {
    private static final Logger LOGGER = LogManager.getLogger();
    public ExplosiveBloodCharm(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class ItemEvents{
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onDamage(LivingHurtEvent event){
            if (event.isCanceled()) return;
            if (event.getSource() == null) return;
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            DamageSource source = event.getSource();
            PlayerEntity DamagedPlayer = (PlayerEntity) event.getEntityLiving();

            //Now look for the Explosive Blood Charm
            CuriosUtil.getAllWornItems(DamagedPlayer).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    ItemStack curioStack = e.getStackInSlot(i);
                    Item item = curioStack.getItem();
                    //Find the Explosive Blood Charm
                    if (item instanceof ExplosiveBloodCharm){
                        if (DamagedPlayer.level.getRandom().nextFloat() > 0.25F) return;

                        Optional<IMana> mana = ManaCapability.getMana(DamagedPlayer).resolve();
                        if (!mana.isPresent()) return;
                        //The time level gametime needs to pass in order for this to work.
                        EffectInstance cooldown = DamagedPlayer.getEffect(EffectInit.EXPLOSIVE_BLOOD_EFFECT);
                        if (cooldown != null) return;
                        //Set the cooldown
                        DamagedPlayer.addEffect(new EffectInstance(EffectInit.EXPLOSIVE_BLOOD_EFFECT, (10 * 20), 0));

                        //Start with 3 mana, and each 100 mana will increase the range by 2
                        float knockback = new CalcUtil(0.25F).manaMultiplier(DamagedPlayer, 1).compile();
                        float range = 1 + new CalcUtil(2).manaMultiplier(DamagedPlayer, 1).compile();
                        AxisAlignedBB bounds = DamagedPlayer.getBoundingBox().inflate(range);

                        //Grab all entities nearby
                        for (LivingEntity target : DamagedPlayer.level.getEntitiesOfClass(LivingEntity.class, bounds)) {
                            if ((target.noPhysics || target.isInvulnerable() || !target.isPushable()))
                                continue;
                            if (target == DamagedPlayer) continue;
                            if (target instanceof ArmorStandEntity && ((ArmorStandEntity) target).isMarker()) continue;

                            //First apply knockback
                            EffectKnockback.INSTANCE.knockback(target, DamagedPlayer, knockback * ((range - DamagedPlayer.distanceTo(target))/range));
                            target.hurtMarked = true;
                            //Then apply damage
                            target.hurt(new EntityDamageSource("ars_mage_fight.item.explosive_blood_charm", target).setExplosion(),event.getAmount()/2F);
                        }

                        Vector3d pos = new Vector3d(DamagedPlayer.getX(), DamagedPlayer.getY(), DamagedPlayer.getZ());
                        ((ServerWorld) DamagedPlayer.level).sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1,1.0D, 0.0D, 0.0D, 1);
                        //Let's get some particle effects to show up and the explosion sound
                        DamagedPlayer.level.playSound(null, DamagedPlayer.blockPosition(), SoundEvents.GENERIC_EXPLODE,
                                DamagedPlayer.getSoundSource(), 4.0F, (1.0F + (DamagedPlayer.level.random.nextFloat() - DamagedPlayer.level.random.nextFloat()) * 0.2F) * 0.7F);
                    }
                }
            });
        }
    }
}
