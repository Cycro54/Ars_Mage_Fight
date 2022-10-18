package invoker54.magefight.init;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import invoker54.magefight.spell.effect.*;

import java.util.ArrayList;
import java.util.List;

public class GlyphInit {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs() {
        register(DeathGripEffect.INSTANCE);
        register(ChillingTouchEffect.INSTANCE);
        register(ComboEffect.INSTANCE);
        register(BlackHoleEffect.INSTANCE);
        register(FatalBondEffect.INSTANCE);
        register(VengefulStrikeEffect.INSTANCE);
        register(LifeSigilEffect.INSTANCE);
        register(StalwartEffect.INSTANCE);
        register(RuptureEffect.INSTANCE);
        register(BloodSlimeEffect.INSTANCE);
        register(ManaSlimeEffect.INSTANCE);
        register(RewindEffect.INSTANCE);
        register(MetabolicRushEffect.INSTANCE);
        register(LifeTapEffect.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.getTag(), spellPart);
        registeredSpells.add(spellPart);
    }
}
