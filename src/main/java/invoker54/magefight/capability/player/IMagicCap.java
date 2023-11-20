package invoker54.magefight.capability.player;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IMagicCap extends INBTSerializable<CompoundNBT> {
    boolean hasTag(String tag);

    CompoundNBT getTag(String tag);

    void removeTag(String tag);

    List<AbstractSpellPart> getUnlockedBattleSpells();

    void addSpell(AbstractSpellPart spellPart);

    void removeSpell(AbstractSpellPart spellPart);
}
