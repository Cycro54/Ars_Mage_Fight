package invoker54.magefight.init;

import com.hollingsworth.arsnouveau.ArsNouveau;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.items.CombatBlockItem;
import invoker54.magefight.items.curios.*;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {
    private static final Logger LOGGER = LogManager.getLogger();
    public static ArrayList<Item> items = new ArrayList<>();

    public static Item addItem(Item item, String name){
        item.setRegistryName(ArsMageFight.MOD_ID, name);
        items.add(item);
        return item;
    }
//    public static final Item WOOD_PAXEL_FAKE = addItem(new Item(getDefault(true)), "utility/wood_paxel_fake");

    public static final Item BLOOD_CHARM = addItem(new BloodThirstCharm(getDefault(true)), "blood_charm");
    public static final Item OWL_TRINKET = addItem(new OwlSightTrinket(getDefault(true)), "owl_trinket");
    public static final Item SOUL_SIPHON_RING = addItem(new SoulSiphonRing(getDefault(true)), "soul_siphon_ring");
    public static final Item RING_OF_PERMANENCE = addItem(new PermanenceRing(getDefault(true)), "ring_of_permanence");
    public static final Item THUNDER_TRINKET = addItem(new ThunderTrinket(getDefault(true)), "thunder_trinket");
    public static final Item CREEPER_TRINKET = addItem(new CreeperTrinket(getDefault(true)), "creeper_trinket");
    public static final Item VITAL_RESERVE_CHARM = addItem(new VitalReserveCharm(getDefault(true)), "vital_reserve_charm");
    public static final Item MANA_STRIKE_RING = addItem(new ManaStrikeRing(getDefault(true)), "mana_strike_ring");

    //This is for custom block items
    public static final Item COMBAT_BLOCK_ITEM = addItem(new CombatBlockItem(BlockInit.COMBAT_BLOCK, getDefault(true)), "combat_block_item");

    public static Item.Properties getDefault(boolean itemGroup) {
        if (!itemGroup) return new Item.Properties();
        
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent){
        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
        for (Item item: items){
            registry.register(item);
        }
    }
}
