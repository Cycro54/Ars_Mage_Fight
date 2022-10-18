package invoker54.magefight.init;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.loot.CharmStructureAdditionModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootModInit {

    @SubscribeEvent
    public static void addLootModifiers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event){
        event.getRegistry().registerAll(
                new CharmStructureAdditionModifier.Serializer().setRegistryName(
                        new ResourceLocation(ArsMageFight.MOD_ID, "charm_structure_items"))
        );
    }
}
