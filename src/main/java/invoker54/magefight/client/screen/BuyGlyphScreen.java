package invoker54.magefight.client.screen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.config.MageFightConfig;
import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.BuyGlyphMsg;
import invoker54.magefight.network.message.SaveChoicesMsg;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BuyGlyphScreen extends BaseCombatScreen{
    private static final Logger LOGGER = LogManager.getLogger();

    //Stone Card
    protected ClientUtil.Image stoneCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            68, 34, 0, 66, 256);
    protected ClientUtil.Image stoneCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            68, 34, 66, 66, 256);

    //Gold Card
    protected ClientUtil.Image goldCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            0, 34, 0, 66, 256);
    protected ClientUtil.Image goldCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            0, 34, 66, 66, 256);

    //Netherite Card
    protected ClientUtil.Image netheriteCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            34, 34, 0, 66, 256);
    protected ClientUtil.Image netheriteCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            34, 34, 64, 64, 256);

    //Obsidian Card
    protected ClientUtil.Image obsidianCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            102, 34, 0, 66, 256);
    protected ClientUtil.Image obsidianCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            102, 34, 66, 66, 256);

    private final int selectedDesign;
    private List<Button> glyphButtons;
    private AbstractSpellPart selectedChoice;
    private final MagicDataCap playerCap;

    public BuyGlyphScreen(){
        //First select a design
        selectedDesign = ClientUtil.mC.player.getRandom().nextInt((3 - 1) + 1) + 1;
        playerCap = MagicDataCap.getCap(ClientUtil.mC.player);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        if (selectedChoice == null){
            ClientUtil.Image[] imgs = new ClientUtil.Image[poolSpells.size()];
            int spaceTaken = 0;

            for (int a = 0; a < poolSpells.size(); a++){
                imgs[a] = getCard(poolSpells.get(a));
                imgs[a].resetScale();
                imgs[a].setActualSize(imgs[a].getWidth() * 2, imgs[a].getHeight() * 2);
                spaceTaken += imgs[a].getWidth();
            }
            //The space inbetween the cards
            int space = (this.width - (spaceTaken))/(poolSpells.size() + 1);

            for (int a = 0; a < poolSpells.size(); a++){
                ClientUtil.Image img = imgs[a];
                //Render the card
                img.moveTo(((a+1) * space) + (img.getWidth() * a), img.y0);
                img.centerImageY(0,this.height);
                img.RenderImage(stack);

                this.buttons.get(a).x = img.x0;
                this.buttons.get(a).y = img.y0;
//                LOGGER.debug("BUTTON BEGINNING X IS: " + this.buttons.get(a).x);
//                LOGGER.debug("BUTTON BEGINNING WIDTH IS: " + this.buttons.get(a).getWidth());
//                LOGGER.debug("BUTTON BEGINNING Y IS: " + this.buttons.get(a).y);
//                LOGGER.debug("BUTTON BEGINNING HEIGHT IS: " + this.buttons.get(a).getHeight());

                boolean showFront = playerCap.checkSeenSpell(poolSpells.get(a)) && MageFightConfig.showSeenGlyphs;
                // LOGGER.debug("WHAT's THE GLYPH? " + poolSpells.get(a).getName());
                // LOGGER.debug("DID I UNLOCK IT ALREADY? " + playerCap.checkSeenSpell(poolSpells.get(a)));
                if (showFront){
                    //Then render the glyph
                    ClientUtil.Image choiceIMG = new ClientUtil.Image(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + poolSpells.get(a).getIcon()), 0, 16, 0, 16, 16);
                    choiceIMG.setActualSize(48,48);
                    choiceIMG.centerImageX(img.x0,img.getWidth());
                    choiceIMG.centerImageY(img.y0,img.getHeight());
                    choiceIMG.RenderImage(stack);
                }
            }

            for (int a = 0; a < poolSpells.size(); a++){
                boolean showFront = playerCap.checkSeenSpell(poolSpells.get(a)) && MageFightConfig.showSeenGlyphs;
                //This is for rendering the toolTip
                if (this.buttons.get(a).isMouseOver(mouseX, mouseY) && showFront) {
                    List<ITextComponent> toolTip = new ArrayList<>();

                    toolTip.add(new TranslationTextComponent(poolSpells.get(a).getLocalizationKey()));
                    if (Screen.hasShiftDown()) {
                        toolTip.add(poolSpells.get(a).getBookDescLang());
                    } else {
                        toolTip.add(new TranslationTextComponent("tooltip.ars_nouveau.hold_shift"));
                    }

                    renderWrappedToolTip(stack, toolTip, mouseX, mouseY, font);
                }
            }
        }
        else {
            ClientUtil.Image img = getCard(selectedChoice);
            img.resetScale();
            img.setActualSize(img.getWidth() * 2, img.getHeight() * 2);

            //First render the card
            img.centerImageX(0, this.width);
            img.centerImageY(0, this.height);
            img.RenderImage(stack);

            //Then render the glyph
            ClientUtil.Image choiceIMG = new ClientUtil.Image(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + selectedChoice.getIcon()), 0, 16, 0, 16, 16);
            choiceIMG.setActualSize(48,48);
            choiceIMG.centerImageX(0,this.width);
            choiceIMG.centerImageY(0,this.height);
            choiceIMG.RenderImage(stack);

            //This is for rendering the toolTip
            if (choiceIMG.isMouseOver(mouseX, mouseY)) {
                List<ITextComponent> toolTip = new ArrayList<>();

                toolTip.add(new TranslationTextComponent(selectedChoice.getLocalizationKey()));
                if (Screen.hasShiftDown()) {
                    toolTip.add(selectedChoice.getBookDescLang());
                } else {
                    toolTip.add(new TranslationTextComponent("tooltip.ars_nouveau.hold_shift"));
                }

                renderWrappedToolTip(stack, toolTip, mouseX, mouseY, font);
            }
        }
    }

    public ClientUtil.Image getCard(AbstractSpellPart spellPart){
        ClientUtil.Image img;

        boolean showFront = playerCap.checkSeenSpell(spellPart) && MageFightConfig.showSeenGlyphs;

        switch (spellPart.getTier().ordinal()){
            default:
                img = (!showFront ? obsidianCardFront : obsidianCardBack);
                break;
            case 0:
                img = (!showFront ? stoneCardFront : stoneCardBack);
                break;
            case 1:
                img = (!showFront ? goldCardFront : goldCardBack);
                break;
            case 2:
                img = (!showFront ? netheriteCardFront : netheriteCardBack);
                break;
        }

        return img;
    }

    public void glyphSelected(AbstractSpellPart choice){
        //Remove temp spells
        playerCap.removeTempSpells();

        //Send data to server
        NetworkHandler.INSTANCE.sendToServer(new BuyGlyphMsg(choice.getTag()));

        //Get rid of all the prev buttons
        this.buttons.clear();
        this.children.clear();

        //Make the selected choice known
        selectedChoice = choice;

        ClientUtil.Image img;
        switch (selectedDesign){
            default:
                img = stoneCardBack;
                break;
            case 2:
                img = goldCardBack;
                break;
            case 3:
                img = netheriteCardBack;
                break;
        }
        img.setActualSize(img.getWidth() * 2, img.getHeight() * 2);
        
        //Make one gigantic button that goes back to the glyph storage screen
        ClientUtil.SimpleButton backButton = new ClientUtil.SimpleButton(0,0,this.width, this.height, ITextComponent.nullToEmpty(""), (button) ->{
            ClientUtil.mC.setScreen(new GlyphStorageScreen());
        });
        addButton(backButton);
        backButton.hidden = true;
    }

    @Override
    protected void init() {
        super.init();
        stoneCardFront.resetScale();
        stoneCardBack.resetScale();
        goldCardFront.resetScale();
        goldCardBack.resetScale();
        netheriteCardFront.resetScale();
        netheriteCardBack.resetScale();

        glyphButtons = new ArrayList<>();

        //Next start to get the choices
        IMana mana = ManaCapability.getMana(ClientUtil.mC.player).resolve().get();
        if (playerCap.getTempSpells().size() == 0) {
            poolSpells.removeIf((spellPart) -> unlockedSpells.contains(spellPart) || spellPart.getTier().ordinal() > mana.getBookTier());

            //Remove all the excess choices
            while (poolSpells.size() > 3) {
                poolSpells.remove(ClientUtil.mC.player.getRandom().nextInt(poolSpells.size()));
            }

            String tempSpells = "";
            for (AbstractSpellPart spellPart: this.poolSpells){
                tempSpells = tempSpells.concat(spellPart.getTag() + ",");
            }
            NetworkHandler.INSTANCE.sendToServer(new SaveChoicesMsg(tempSpells));
        }
        else {
            poolSpells.clear();
            poolSpells.addAll(playerCap.getTempSpells());
        }


        //Now make some buttons for each choice
        for (AbstractSpellPart spellPart : poolSpells){
            ClientUtil.Image img = getCard(spellPart);
            img.resetScale();

            img.setActualSize(img.getWidth() * 2, img.getHeight() * 2);
            ClientUtil.SimpleButton choiceButton =
                    new ClientUtil.SimpleButton(0,0, img.getWidth(), img.getHeight(), ITextComponent.nullToEmpty(""), (button) ->{
                        // LOGGER.debug("YOU HAVE SELECTED: " + spellPart.getName());
                        glyphSelected(spellPart);
                    });

            choiceButton.hidden = true;
            this.addButton(choiceButton);
            this.glyphButtons.add(choiceButton);
        }
    }
}
