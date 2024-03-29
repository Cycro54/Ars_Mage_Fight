package invoker54.magefight.client.screen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

public class ModGuiManaHUD extends AbstractGui {
    private static final Minecraft minecraft = Minecraft.getInstance();

    public void drawHUD(MatrixStack ms, float pt) {

        IMana mana = ManaCapability.getMana(minecraft.player).orElse(null);
        if(mana == null)
            return;

        int offsetLeft = 10;
        int manaLength = 96;
        manaLength = (int) ((manaLength) * ((mana.getCurrentMana()) / ((double) mana.getMaxMana() - 0.0)));

        int height = minecraft.getWindow().getGuiScaledHeight() - 5;

        Minecraft.getInstance().textureManager.bind(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms,offsetLeft, height - 18, 0, 0, 108, 18, 256, 256);
        int manaOffset = (int) (((ClientInfo.ticksInGame + pt) / 3 % (33))) * 6;

        // 96
        Minecraft.getInstance().textureManager.bind(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_mana.png"));
        blit(ms,offsetLeft + 9, height - 9, 0, manaOffset, manaLength,6, 256, 256);

        Minecraft.getInstance().textureManager.bind(new ResourceLocation(ArsNouveau.MODID, "textures/gui/manabar_gui_border.png"));
        blit(ms,offsetLeft, height - 17, 0, 18, 108, 20, 256, 256);
    }
}
