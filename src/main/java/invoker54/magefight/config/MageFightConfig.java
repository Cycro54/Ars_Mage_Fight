package invoker54.magefight.config;

import invoker54.magefight.ArsMageFight;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MageFightConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static boolean isRandom;
    public static List<String> randomGlyphPool;
    public static int baseGlyphPrice;
    public static int pricePerTier;
    public static int maxAllowedGlyphs;
    public static int deathGlyphLoss;
    public static boolean autoUnlockGlyph;

    private static boolean isDirty = false;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static CompoundNBT serialize(){
        CompoundNBT mainNBT = new CompoundNBT();
        //First isRandom
        mainNBT.putBoolean("isRandom", isRandom);
        //Glyph list
        String allSpells = "";
        for (String string: randomGlyphPool){
            allSpells = allSpells.concat(string+",");
        }
        mainNBT.putString("randomGlyphPool", allSpells);
        //Price per Glyphs
        mainNBT.putInt("baseGlyphPrice", baseGlyphPrice);
        //Price per Tier
        mainNBT.putInt("pricePerTier", pricePerTier);
        //Max Allowed Glyphs
        mainNBT.putInt("maxAllowedGlyphs", maxAllowedGlyphs);
        //Glyphs lost on death
        mainNBT.putInt("deathGlyphLoss", deathGlyphLoss);
        //Auto obtain glyph when you purchase it
        mainNBT.putBoolean("autoUnlockGlyph", autoUnlockGlyph);
        return mainNBT;
    }

    public static void deserialize(CompoundNBT mainNBT){
        //First isRandom
        isRandom = mainNBT.getBoolean("isRandom");
        //Glyph list
        randomGlyphPool = Arrays.asList(mainNBT.getString("randomGlyphPool").split(","));
        LOGGER.debug("IN CONFIG, WHAT'S THE NEW SIZE: " + randomGlyphPool.size());
        //Price per Glyphs
        baseGlyphPrice = mainNBT.getInt("baseGlyphPrice");
        //Price per Tier
        pricePerTier = mainNBT.getInt("pricePerTier");
        //Max Allowed Glyphs
        maxAllowedGlyphs = mainNBT.getInt("maxAllowedGlyphs");
        //Glyphs lost on death
        deathGlyphLoss = mainNBT.getInt("deathGlyphLoss");
        //Auto obtain glyph when you purchase it
        autoUnlockGlyph = mainNBT.getBoolean("autoUnlockGlyph");
    }
    
    public static void bakeCommonConfig(){
        //System.out.println("SYNCING CONFIG SHTUFF");
        isRandom = COMMON.isRandom.get();
        randomGlyphPool = (List<String>) COMMON.randomGlyphPool.get();
        baseGlyphPrice = COMMON.baseGlyphPrice.get();
        pricePerTier = COMMON.pricePerTier.get();
        maxAllowedGlyphs = COMMON.maxAllowedGlyphs.get();
        deathGlyphLoss = COMMON.deathGlyphLoss.get();
        autoUnlockGlyph = COMMON.autoUnlockGlyph.get();
    }

    @SubscribeEvent
    public static void onConfigChanged(final ModConfig.ModConfigEvent eventConfig){
        //System.out.println("What's the config type? " + eventConfig.getConfig().getType());
        if(eventConfig.getConfig().getSpec() == MageFightConfig.COMMON_SPEC){
            bakeCommonConfig();
            markDirty(true);
        }
    }

    public static void markDirty(boolean dirty){
        isDirty = dirty;
    }
    public static boolean isDirty(){
        return isDirty;
    }
    
    public static class CommonConfig {

        //This is how to make a config value
        //public static final ForgeConfigSpec.ConfigValue<Integer> exampleInt;
        //public final ForgeConfigSpec.ConfigValue<Integer> timeLeft;
        public final ForgeConfigSpec.ConfigValue<Boolean> isRandom;
        public final ForgeConfigSpec.ConfigValue<List<?>> randomGlyphPool;
        public final ForgeConfigSpec.ConfigValue<Integer> baseGlyphPrice;
        public final ForgeConfigSpec.ConfigValue<Integer> pricePerTier;
        public final ForgeConfigSpec.ConfigValue<Integer> maxAllowedGlyphs;
        public final ForgeConfigSpec.ConfigValue<Integer> deathGlyphLoss;
        public final ForgeConfigSpec.ConfigValue<Boolean> autoUnlockGlyph;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            //This is what goes on top inside of the config
            builder.push("Ars Gears Config");
            //This is how you place a variable in the config file
            //exampleInt = BUILDER.comment("This is an integer. Default value is 3.").define("Example Integer", 54);
            isRandom = builder.comment("If it's random mode").define("Random_Mode", true);
            randomGlyphPool = builder.comment("All glyphs in the glyph pool").defineList("Random_Glyph_Pool",
                    Arrays.asList("black_hole","blood_slime","chilling_touch","combo","death_grip","fatal_bond","life_sigil","life_tap","mana_slime","metabolic_rush","rewind","rupture","stalwart","vengeful_strike",
                            "amplify","aoe","blink","cold_snap","explosion","fangs","flare","freeze","gravity","harm","heal","hex","lightning","orbit","linger","shield","snare","summon_decoy","summon_vex","summon_wolves","self","wither","wind_shear"), (toTest -> toTest instanceof String));

            baseGlyphPrice = builder.comment("How much the starting cost will be").define("Base_Price", 80);
            pricePerTier = builder.comment("How much each tier will increase cost").define("Price_Per_Tier", 54);
            maxAllowedGlyphs = builder.comment("How many battle glyphs you may have in total").defineInRange("Max_Allowed_Glyphs", 9, 0, Integer.MAX_VALUE);
            deathGlyphLoss = builder.comment("How many battle glyphs you lose on death").defineInRange("Death_Glyph_Loss", 0, 0, Integer.MAX_VALUE);
            autoUnlockGlyph = builder.comment("If you should automatically unlock a glyph after purchase").define("Auto_Unlock_Glyph", false);
            builder.pop();
        }
    }
}
