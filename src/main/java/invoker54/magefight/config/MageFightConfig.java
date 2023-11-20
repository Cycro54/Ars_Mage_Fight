package invoker54.magefight.config;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
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
import java.util.Map;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MageFightConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

//    public static boolean isRandom;
    public static List<String> blacklistGlyphPool;
    public static int baseGlyphPrice;
    public static int pricePerTier;
    public static int maxCost;
    public static int maxAllowedGlyphs;
    public static int deathGlyphLoss;
//    public static boolean autoUnlockGlyph;
    public static boolean showSeenGlyphs;
    public static boolean disableGlyphSystem;

    private static boolean isDirty = false;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static List<AbstractSpellPart> getBatlePoolList(){
        Map<String, AbstractSpellPart> spellPartMap = ArsNouveauAPI.getInstance().getSpell_map();
        List<AbstractSpellPart> pool_list = new ArrayList<>(spellPartMap.values());
        for (String spellTag: MageFightConfig.blacklistGlyphPool){
            if (!spellPartMap.containsKey(spellTag)){
                continue;
            }

            pool_list.remove(spellPartMap.get(spellTag));
        }

        return pool_list;
    }

    public static CompoundNBT serialize(){
        CompoundNBT mainNBT = new CompoundNBT();
        //First isRandom
//        mainNBT.putBoolean("isRandom", isRandom);
        //Glyph list
        String allSpells = "";
        for (String string: blacklistGlyphPool){
            allSpells = allSpells.concat(string+",");
        }
        mainNBT.putString("blacklistGlyphPool", allSpells);
        //Price per Glyphs
        mainNBT.putInt("baseGlyphPrice", baseGlyphPrice);
        //Price per Tier
        mainNBT.putInt("pricePerTier", pricePerTier);
        //Max cost
        mainNBT.putInt("maxCost", maxCost);
        //Max Allowed Glyphs
        mainNBT.putInt("maxAllowedGlyphs", maxAllowedGlyphs);
        //Glyphs lost on death
        mainNBT.putInt("deathGlyphLoss", deathGlyphLoss);
        //Auto obtain glyph when you purchase it
//        mainNBT.putBoolean("autoUnlockGlyph", autoUnlockGlyph);
        //Show glyphs you've already seen
        mainNBT.putBoolean("showSeenGlyphs", showSeenGlyphs);
        //Disables the glyph system
        mainNBT.putBoolean("disableGlyphSystem", disableGlyphSystem);
        return mainNBT;
    }

    public static void deserialize(CompoundNBT mainNBT){
        //First isRandom
//        isRandom = mainNBT.getBoolean("isRandom");
        //Glyph list
        blacklistGlyphPool = Arrays.asList(mainNBT.getString("blacklistGlyphPool").split(","));
//        LOGGER.debug("IN CONFIG, WHAT'S THE NEW SIZE: " + blacklistGlyphPool.size());
        //Price per Glyphs
        baseGlyphPrice = mainNBT.getInt("baseGlyphPrice");
        //Price per Tier
        pricePerTier = mainNBT.getInt("pricePerTier");
        //Max cost
        maxCost = mainNBT.getInt("maxCost");
        //Max Allowed Glyphs
        maxAllowedGlyphs = mainNBT.getInt("maxAllowedGlyphs");
        //Glyphs lost on death
        deathGlyphLoss = mainNBT.getInt("deathGlyphLoss");
        //Auto obtain glyph when you purchase it
//        autoUnlockGlyph = mainNBT.getBoolean("autoUnlockGlyph");
        //Show glyphs you've already seen
        showSeenGlyphs = mainNBT.getBoolean("showSeenGlyphs");
        //Disables the glyph system
        disableGlyphSystem = mainNBT.getBoolean("disableGlyphSystem");
    }
    
    public static void bakeCommonConfig(){
        //System.out.println("SYNCING CONFIG SHTUFF");
//        isRandom = COMMON.isRandom.get();
        blacklistGlyphPool = (List<String>) COMMON.blacklistGlyphPool.get();
        baseGlyphPrice = COMMON.baseGlyphPrice.get();
        pricePerTier = COMMON.pricePerTier.get();
        maxCost = COMMON.maxCost.get();
        maxAllowedGlyphs = COMMON.maxAllowedGlyphs.get();
        deathGlyphLoss = COMMON.deathGlyphLoss.get();
//        autoUnlockGlyph = COMMON.autoUnlockGlyph.get();
        showSeenGlyphs = COMMON.showSeenGlyphs.get();
        disableGlyphSystem = COMMON.disableGlyphSystem.get();
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
//        public final ForgeConfigSpec.ConfigValue<Boolean> isRandom;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistGlyphPool;
        public final ForgeConfigSpec.ConfigValue<Integer> baseGlyphPrice;
        public final ForgeConfigSpec.ConfigValue<Integer> pricePerTier;
        public final ForgeConfigSpec.ConfigValue<Integer> maxCost;
        public final ForgeConfigSpec.ConfigValue<Integer> maxAllowedGlyphs;
        public final ForgeConfigSpec.ConfigValue<Integer> deathGlyphLoss;
//        public final ForgeConfigSpec.ConfigValue<Boolean> autoUnlockGlyph;
        public final ForgeConfigSpec.ConfigValue<Boolean> showSeenGlyphs;
        public final ForgeConfigSpec.ConfigValue<Boolean> disableGlyphSystem;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            //This is what goes on top inside of the config
            builder.push("Ars Gears Config");
            //This is how you place a variable in the config file
            //exampleInt = BUILDER.comment("This is an integer. Default value is 3.").define("Example Integer", 54);
//            isRandom = builder.comment("If it's random mode").define("Random_Mode", true);
            blacklistGlyphPool = builder.comment("All glyphs not added to the glyph pool").defineList("Blacklist_Glyph_Pool",
                    Arrays.asList("aquatic","break","conjure_water","craft","crush","cut","delay","ender_inventory","evaporate","exchange","fell","firework","grow","harvest","interact",
                            "light", "phantom_block", "pickup", "place_block", "redstone_signal", "touch", "sensitive", "smelt", "summon_steed", "toss"), (toTest -> toTest instanceof String));

            baseGlyphPrice = builder.comment("How much the starting cost will be").define("Base_Price", 16);
            pricePerTier = builder.comment("How much each tier will increase cost").define("Price_Per_Tier", 16);
            maxCost = builder.comment("The max cost of glyphs in the combat altar").define("Max_Cost", 315);
            maxAllowedGlyphs = builder.comment("How many battle glyphs you may have in total, setting to 0 disables this.").defineInRange("Max_Allowed_Glyphs", 15, 0, Integer.MAX_VALUE);
            deathGlyphLoss = builder.comment("How many battle glyphs you lose on death").defineInRange("Death_Glyph_Loss", 0, 0, Integer.MAX_VALUE);
//            autoUnlockGlyph = builder.comment("If you should automatically unlock a glyph after purchase").define("Auto_Unlock_Glyph", false);
            showSeenGlyphs = builder.comment("Should you see glyphs you've already seen in the Combat Altar").define("Show_Seen_Glyphs", true);
            disableGlyphSystem = builder.comment("Disables the glyph system and allows you to use any glyph you have").define("Disable_Glyph_System", false);
            builder.pop();
        }
    }
}
