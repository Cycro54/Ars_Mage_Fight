package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import invoker54.magefight.ArsMageFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.HashMap;
import java.util.UUID;

public class VitalReserveCharm extends Item implements ICurioItem, IManaEquipment {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DamageSource VITAL_RESERVE = new DamageSource("spell.item.vital_reserve").setMagic();

    public static final int extraMana = 300;

    public VitalReserveCharm(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxManaBoost(ItemStack i) {
        return extraMana;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class VitalReserveEvent{
        private static final HashMap<UUID, Integer> nonHealers = new HashMap<>();

        //If they try to heal while they are dipping into their reserves, stop them. No healing allowed.
        @SubscribeEvent
        public static void onHeal(LivingHealEvent event){
            if (event.isCanceled()) return;

            if (nonHealers.containsKey(event.getEntityLiving().getUUID())){
                event.setCanceled(true);
                event.setAmount(0);
            }

        }

        @SubscribeEvent
        public static void preSpell(SpellResolveEvent.Pre event){
            if (!(event.shooter instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.shooter;
            if (player.level.isClientSide) return;

            //Now look for the vital reserve charm
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    ItemStack charmStack = e.getStackInSlot(i);
                    Item charmItem = charmStack.getItem();
                    //Find the vital reserve charm

                    //Calculate how far the player dipped into their vital reserves.
                    if (charmItem instanceof VitalReserveCharm){
                        SpellResolver resolver = new SpellResolver(event.context);
                        //This is how much the spell will cost in total
                        int totalCost = resolver.getCastingCost(event.spell, player);
                        //This is the player mana capability
                        IMana manaCap = ManaCapability.getMana(player).resolve().get();
                        //This is how much mana the player has currently
                        double currentMana = manaCap.getCurrentMana();
                        //I want to remove the mana above the vital reserve mana first
                        if (currentMana > extraMana){
                            totalCost -= (currentMana - extraMana);
                            //Reflect the reduction to the currentMana variable too
                            currentMana = extraMana;
                        }
                        //If the totalCost is 0 or less, don't go any further
                        if (totalCost <= 0) return;


                        if (currentMana < extraMana){
                            //This will be how far the player dipped into their reserves.
                            nonHealers.put(player.getUUID(), totalCost);
                        }
                    }
                }
            });
        }

        @SubscribeEvent
        public static void postSpell(SpellResolveEvent.Post event){
            if (!(event.shooter instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.shooter;
            if (player.level.isClientSide) return;

            if (!nonHealers.containsKey(player.getUUID())) return;

            //Take their health
            // LOGGER.debug("HOW MUCH MANA DID YOU USE " + nonHealers.get(player.getUUID()));
            // LOGGER.debug("HOW MUCH HEALTH WILL YOU LOSE " + (nonHealers.get(player.getUUID())/(extraMana + 0F)) * (player.getMaxHealth() - 4));
            float healthLoss = (nonHealers.get(player.getUUID())/(extraMana + 0F)) * (player.getMaxHealth() - 4);
            player.hurt(VITAL_RESERVE, healthLoss);

            //Then remove them from the hashMap
            nonHealers.remove(player.getUUID());
        }

        @SubscribeEvent
        public static void onDeath(LivingDeathEvent event){
            if (event.isCanceled()) return;
            if (nonHealers.containsKey(event.getEntityLiving().getUUID())){
                event.setCanceled(true);
                event.getEntityLiving().setHealth(1);
            }
        }
    }

}
