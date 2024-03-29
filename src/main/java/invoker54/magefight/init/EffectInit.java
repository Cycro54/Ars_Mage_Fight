package invoker54.magefight.init;


import invoker54.magefight.ArsMageFight;
import invoker54.magefight.potion.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EffectInit {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ArrayList<Effect> effects = new ArrayList<>();

    public static Effect addEffect(Effect effect, String name){
        effect.setRegistryName(ArsMageFight.MOD_ID, name);
        effects.add(effect);
        return effect;
    }
//    public static final Effect WOOD_PAXEL_FAKE = addItem(new Effect(getDefault(true)), "utility/wood_paxel_fake");
    public static final Effect CHILL_EFFECT = addEffect(new ChillPotionEffect(EffectType.HARMFUL), "chilled_effect");
    public static final Effect SHOCK_EFFECT = addEffect(new ShockPotionEffect(EffectType.HARMFUL), "shocked_effect");
    public static final Effect OVERCHARGE_EFFECT = addEffect(new OverChargePotionEffect(EffectType.NEUTRAL), "overcharge_effect");
    public static final Effect COMBO_EFFECT = addEffect(new ComboPotionEffect(EffectType.BENEFICIAL), "combo_effect");
    public static final Effect BLACK_HOLE_EFFECT = addEffect(new BlackHolePotionEffect(EffectType.NEUTRAL), "black_hole_effect");
    public static final Effect FATAL_BOND_EFFECT = addEffect(new FatalBondPotionEffect(EffectType.HARMFUL), "fatal_bond_effect");
    public static final Effect VENGEANCE_EFFECT = addEffect(new VengeancePotionEffect(EffectType.BENEFICIAL), "vengeance_effect");
    public static final Effect STALWART_EFFECT = addEffect(new StalwartPotionEffect(EffectType.BENEFICIAL), "stalwart_effect");
    public static final Effect RUPTURE_EFFECT = addEffect(new RupturePotionEffect(EffectType.HARMFUL), "rupture_effect");
    public static final Effect REWIND_EFFECT = addEffect(new RewindPotionEffect(EffectType.BENEFICIAL), "rewind_effect");
    public static final Effect METABOLIC_RUSH_EFFECT = addEffect(new MetabolicRushPotionEffect(EffectType.NEUTRAL), "metabolic_rush_effect");
    public static final Effect LIFE_TAP_EFFECT = addEffect(new LifeTapPotionEffect(EffectType.NEUTRAL), "life_tap_effect");
    public static final Effect ENRAGED_EFFECT = addEffect(new EnragedPotionEffect(EffectType.HARMFUL), "enraged_effect");
    public static final Effect MANA_DROUGHT_EFFECT = addEffect(new ManaDroughtPotionEffect(EffectType.HARMFUL), "mana_drought_effect");
    public static final Effect BATTLE_HUNGER_EFFECT = addEffect(new BattleHungerPotionEffect(EffectType.HARMFUL), "battle_hunger_effect");
    public static final Effect DEATHS_BANE_EFFECT = addEffect(new DeathsBanePotionEffect(EffectType.NEUTRAL), "deaths_bane_effect");
    public static final Effect LIFE_LEECH_EFFECT = addEffect(new LifeLeechPotionEffect(EffectType.HARMFUL), "life_leech_effect");
    public static final Effect EXPLOSIVE_BLOOD_EFFECT = addEffect(new ExplosiveBloodPotionEffect(EffectType.HARMFUL), "explosive_blood_effect");

    @SubscribeEvent
    public static void registerEffects(final RegistryEvent.Register<Effect> event) {
        IForgeRegistry<Effect> registry = event.getRegistry();
        for (Effect effect: effects){
            registry.register(effect);
            // LOGGER.debug("EFFECT REGISTERED: " + effect.getRegistryName().toString());
        }
    }
}
