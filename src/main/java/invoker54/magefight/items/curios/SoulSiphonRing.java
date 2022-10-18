package invoker54.magefight.items.curios;

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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SoulSiphonRing extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public SoulSiphonRing(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class SoulSiphonEvents{
        @SubscribeEvent
        public static void onKill(LivingDeathEvent event){
            if (event.getSource() == null) return;
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) source.getEntity();

            //Now look for the Soul Siphon Ring
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the Soul Siphon Ring
                    if (item instanceof SoulSiphonRing){
                        if (Math.random() <= 0.2F){
                            ManaCapability.getMana(player).ifPresent(mana -> {
                                mana.addMana(mana.getMaxMana() * 0.25F);
                            });
                        }
                    }
                }
            });
        }
    }
}
