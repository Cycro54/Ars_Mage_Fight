package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import invoker54.magefight.ArsMageFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ManaStrikeRing extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public ManaStrikeRing(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class ManaStrikeRingEvents{

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onHit(LivingDamageEvent event){
            if (event.isCanceled()) return;
            if (event.getSource() == null) return;
            //Make sure it was a player
            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            //Now look for the mana strike ring
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the mana strike ring
                    if (item instanceof ManaStrikeRing){
                        //Reduce the damage
                        event.setAmount(event.getAmount() * 0.75F);
                        //Then give the player some mana
                        ManaCapability.getMana(player).ifPresent((manaCap) -> manaCap.addMana(event.getAmount()/2F));
                    }
                }

            });
        }
    }
}
