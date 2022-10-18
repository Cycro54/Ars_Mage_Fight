package invoker54.magefight.items.curios;

import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.magefight.ArsMageFight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Random;


public class BloodThirstCharm extends Item implements ICurioItem {
    private static final Logger LOGGER = LogManager.getLogger();

    public BloodThirstCharm(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LOGGER.debug("HEY, THIS BE WORKING!");

        //Take away their health, and their food.
        if (!(slotContext.getWearer() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) slotContext.getWearer();

        if (player.getHealth() > 5) player.setHealth(5);

        FoodStats foodStats = player.getFoodData();

        if (foodStats.getFoodLevel() > 4){
            foodStats.setFoodLevel(4);
            foodStats.setSaturation(0);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ActionResult.fail(playerIn.getItemInHand(handIn));
    }

    @Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID)
    public static class BloodThirstEvents{
        @SubscribeEvent
        public static void onFoodEat(LivingEntityUseItemEvent.Finish event){
            //Make sure the item they used was food
            if (!event.getItem().getItem().isEdible()) return;
            //Make sure it was a player
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            //Now look for the blood charm
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the blood charm
                    if (item instanceof BloodThirstCharm){
                        //Take away the food they just gained
                        Food foodData = event.getItem().getItem().getFoodProperties();
                        FoodStats stats = player.getFoodData();
                        stats.eat(-foodData.getNutrition(), -foodData.getSaturationModifier());

                        //Send a message
                        switch (new Random().nextInt(3) + 1){
                            default:
                                PortUtil.sendMessage(player, new TranslationTextComponent("ars_mage_fight.chat.blood_charm_message1"));
                                break;
                            case 2:
                                PortUtil.sendMessage(player, new TranslationTextComponent("ars_mage_fight.chat.blood_charm_message2"));
                                break;
                            case 3:
                                PortUtil.sendMessage(player, new TranslationTextComponent("ars_mage_fight.chat.blood_charm_message3"));
                                break;
                        }
                    }
                }

            });
        }

        @SubscribeEvent
        public static void onKill(LivingDeathEvent event){
            if (event.getSource() == null) return;
            DamageSource source = event.getSource();

            if (!(source.getEntity() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) source.getEntity();

            //Now look for the blood charm
            CuriosUtil.getAllWornItems(player).ifPresent(e ->{
                for(int i = 0; i < e.getSlots(); i++){
                    Item item = e.getStackInSlot(i).getItem();
                    //Find the blood charm
                    if (item instanceof BloodThirstCharm){
                        //Now feed them.
                        FoodStats stats = player.getFoodData();
                        stats.eat(3,2);
                    }
                }
            });
        }
    }
}
