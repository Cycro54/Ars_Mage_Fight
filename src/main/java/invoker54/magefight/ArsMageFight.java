package invoker54.magefight;

import invoker54.magefight.config.MageFightConfig;
import invoker54.magefight.init.CapInit;
import invoker54.magefight.init.GlyphInit;
import invoker54.magefight.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import static invoker54.magefight.config.Config.registerGlyphConfigs;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArsMageFight.MOD_ID)
public class ArsMageFight {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "ars_mage_fight";

    public ArsMageFight() {
        GlyphInit.registerGlyphs();
        registerGlyphConfigs();
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //This is for configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MageFightConfig.COMMON_SPEC, "ars_mage_fight-common.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        // LOGGER.info("HELLO FROM PREINIT");
        // LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        CapInit.registerCaps();
        NetworkHandler.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("magefight", "helloworld", () -> {
            // LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CURIO.getMessageBuilder().build());
    }
}
