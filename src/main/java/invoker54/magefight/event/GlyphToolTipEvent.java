package invoker54.magefight.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.config.MageFightConfig;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ArsMageFight.MOD_ID, value = Dist.CLIENT)
public class GlyphToolTipEvent {

    @SubscribeEvent
    public static void onGlyphHover(ItemTooltipEvent event){
        if (ClientUtil.mC.player == null) return;

        MagicDataCap cap = MagicDataCap.getCap(ClientUtil.mC.player);
        if (!(event.getItemStack().getItem() instanceof Glyph)) return;
        Glyph glyph = (Glyph) event.getItemStack().getItem();
        List<AbstractSpellPart> pool_list = MageFightConfig.getBatlePoolList();

        if (cap.getUnlockedBattleSpells().contains(glyph.spellPart) || !pool_list.contains(glyph.spellPart) || MageFightConfig.disableGlyphSystem)
            event.getToolTip().add(new TranslationTextComponent("ars_mage_fight.tooltip.unlocked")
                    .withStyle(TextFormatting.BOLD).withStyle(TextFormatting.GREEN));
        else {
            event.getToolTip().add(new TranslationTextComponent("ars_mage_fight.tooltip.locked")
                    .withStyle(TextFormatting.BOLD).withStyle(TextFormatting.RED));
        }

    }
}
