package invoker54.magefight.event;

import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.client.screen.ModGuiManaHUD;
import invoker54.magefight.potion.ComboPotionEffect;
import invoker54.magefight.potion.StalwartPotionEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsMageFight.MOD_ID)
public class DrawGuiEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ModGuiManaHUD manaHUD = new ModGuiManaHUD();

    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        MagicDataCap cap = MagicDataCap.getCap(ClientUtil.mC.player);
        if (cap == null) return;

        if (cap.hasTag(ComboPotionEffect.comboString)) manaHUD.drawHUD(event.getMatrixStack(), event.getPartialTicks());
        if (cap.hasTag(StalwartPotionEffect.stalwartString)) manaHUD.drawHUD(event.getMatrixStack(), event.getPartialTicks());
    }
}
