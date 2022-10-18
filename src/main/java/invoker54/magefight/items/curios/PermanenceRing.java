package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import invoker54.magefight.ArsMageFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;

public class PermanenceRing extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public PermanenceRing(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class PermanenceRingEvents{
        @SubscribeEvent
        public static void onKill(LivingDeathEvent event){
            if (event.getSource() == null) return;
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) source.getEntity();

            //Now look for the Ring of Permanence
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the Ring of Permanence
                    if (item instanceof PermanenceRing){
                        //if it's smaller or equal to 10%
                        LOGGER.debug("I AM CHECKING YOUR CHANCE");
                        if (Math.random() <= 0.25F){
                            ArrayList<ItemStack> repairableItems = new ArrayList<>();
                            for (ItemStack stack: player.inventory.items){
                                if (stack.isDamageableItem() && stack.isDamaged()){
                                    repairableItems.add(stack);
                                }
                            }
                            for (ItemStack stack: player.inventory.armor){
                                if (stack.isDamageableItem() && stack.isDamaged()){
                                    repairableItems.add(stack);
                                }
                            }
                            for (ItemStack stack: player.inventory.offhand){
                                if (stack.isDamageableItem() && stack.isDamaged()){
                                    repairableItems.add(stack);
                                }
                            }

                            LOGGER.debug("YOU PASSED THE CHECK!!");
                            ItemStack repairedItem = repairableItems.get((int) (Math.random() * (repairableItems.size() - 1)));
                            LOGGER.debug("THIS IS WHAT WILL BE REPAIRED: " + repairedItem.getDisplayName().getString());
                            LOGGER.debug("ITS OLD DURABILITY WAS: " + (repairedItem.getMaxDamage() - repairedItem.getDamageValue()));
                            repairedItem.setDamageValue((int) (repairedItem.getDamageValue() - (repairedItem.getMaxDamage() * 0.1f)));
                            LOGGER.debug("ITS NEW DURABILITY IS: " + (repairedItem.getMaxDamage() - repairedItem.getDamageValue()));
                            player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ANVIL_USE, SoundCategory.PLAYERS, 1.0F,
                                    random.nextFloat() * 0.1F + 0.9F);
                        }
                    }
                }
            });
        }
    }
}
