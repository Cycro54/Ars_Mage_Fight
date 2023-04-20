package invoker54.magefight.init;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.blocks.tile.CombatBlockTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TileInit {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ArrayList<TileEntityType<?>> tileEntityTypes = new ArrayList<>();
    public static <T extends TileEntity> TileEntityType<T> addTileEntity (TileEntityType.Builder<T> builder, String resourceLocation){
        ResourceLocation location = new ResourceLocation(ArsMageFight.MOD_ID, resourceLocation);
        TileEntityType<T> entityType = builder.build(null);
        entityType.setRegistryName(location);
        tileEntityTypes.add(entityType);
        return entityType;
    }

    public static final TileEntityType<CombatBlockTile> COMBAT_BLOCK_TILE = addTileEntity(TileEntityType.Builder.of(CombatBlockTile::new, BlockInit.COMBAT_BLOCK), "combat_block_tile");

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event){
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

        for (TileEntityType<?> tileEntityType : tileEntityTypes){
            registry.register(tileEntityType);
            // LOGGER.debug("JUST REGISTERED TILE ENTITY: " + tileEntityType.getRegistryName());
        }
    }
}
