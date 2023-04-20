package invoker54.magefight.init;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.blocks.CombatBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockInit {
    private static final Logger LOGGER = LogManager.getLogger();
    public static ArrayList<Block> blocks = new ArrayList<>();

    public static Block addBlock(Block block, String name){
        block.setRegistryName(ArsMageFight.MOD_ID, name);
        blocks.add(block);
        return block;
    }

    public static final Block COMBAT_BLOCK =
            addBlock(new CombatBlock(AbstractBlock.Properties.of(Material.WOOD).
                    strength(2.5F).
                    sound(SoundType.WOOD).
                    noOcclusion()), "combat_block");

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent){
        IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();
        for (Block block: blocks){
            // LOGGER.debug("REGISTERING: " + block.getRegistryName());
            registry.register(block);
        }
    }
}
