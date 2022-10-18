package invoker54.magefight.client.screen;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.util.SpellRecipeUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.config.MageFightConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BaseCombatScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    //Where all the glyphs shall sit
    protected ClientUtil.Image glyph_container = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/glyph_storage.png"),
            0, 137, 0, 166, 256);
    //The actual slot for the glyphs
    protected ClientUtil.Image glyph_slot = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/glyph_storage.png"),
            5, 105, 167, 20, 256);
    //The background.
    protected ClientUtil.Image background = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_background.png"),
            0, 128, 0, 128, 128);

    protected List<AbstractSpellPart> unlockedSpells;
    protected List<AbstractSpellPart> poolSpells;

    public BaseCombatScreen(){
        super(ITextComponent.nullToEmpty(""));
    }

    @Override
    protected void init() {
        //Make sure the background fits the entire screen
        background.setActualSize(width, height);
        background.setImageSize(background.getWidth()/2F, background.getHeight()/2F);
        background.moveTo(0,0);

        //Now start to gather data
        //First grab all the glyphs from the glyph pool
        poolSpells = SpellRecipeUtil.getSpellsFromString(MageFightConfig.serialize().getString("randomGlyphPool"));
        LOGGER.debug("WHATS THE PLAYER ID: " + (ClientUtil.mC.player.getId()));
        LOGGER.debug("WHATS THE POOL SIZE: " + poolSpells.size());
        //Now get all the spells the player has
        unlockedSpells = MagicDataCap.getCap(ClientUtil.mC.player).getUnlockedSpells();
        LOGGER.debug("WHATS THE UNLOCKED GLYPH SIZE: " + unlockedSpells.size());

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        background.RenderImage(stack);

        //This is the gradient, do that after drawing the background tiles
        super.renderBackground(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        //Render the background
        this.renderBackground(stack);
    }
}
