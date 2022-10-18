package invoker54.magefight.client.screen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.capability.Mana;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.ArsMageFight;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.BuyGlyphMsg;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BuyGlyphScreen extends BaseCombatScreen{
    private static final Logger LOGGER = LogManager.getLogger();

    //Stone Card
    protected ClientUtil.Image stoneCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            66, 34, 0, 66, 256);
    protected ClientUtil.Image stoneCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            66, 34, 66, 66, 256);

    //Gold Card
    protected ClientUtil.Image goldCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            0, 34, 0, 66, 256);
    protected ClientUtil.Image goldCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            0, 34, 66, 66, 256);

    //Netherite Card
    protected ClientUtil.Image netheriteCardFront = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            34, 32, 0, 66, 256);
    protected ClientUtil.Image netheriteCardBack = new ClientUtil.Image(new ResourceLocation(ArsMageFight.MOD_ID,"textures/gui/combat_block_cards.png"),
            34, 32, 64, 64, 256);

    private int selectedDesign;
    private List<Button> glyphButtons;
    private AbstractSpellPart selectedChoice;

    public BuyGlyphScreen(){
        //First select a design
        selectedDesign = ClientUtil.mC.player.getRandom().nextInt((3 - 1) + 1) + 1;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        ClientUtil.Image img;

        switch (selectedDesign){
            default:
                img = (selectedChoice == null ? stoneCardFront : stoneCardBack);
                break;
            case 2:
                img = (selectedChoice == null ? goldCardFront : goldCardBack);
                break;
            case 3:
                img = (selectedChoice == null ? netheriteCardFront : netheriteCardBack);
                break;
        }

        if (selectedChoice == null){
            //The space inbetween the cards
            int space = (this.width - (poolSpells.size() * img.getWidth()))/(poolSpells.size() + 1);

            for (int a = 0; a < poolSpells.size(); a++){
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
            }
        }
        else {
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

    public void glyphSelected(AbstractSpellPart choice){
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
        poolSpells.removeIf((spellPart) -> unlockedSpells.contains(spellPart) || spellPart.getTier().ordinal() > mana.getBookTier());

        //Remove all the excess choices
        while (poolSpells.size() > 3){
            poolSpells.remove(ClientUtil.mC.player.getRandom().nextInt(poolSpells.size()));
        }

        ClientUtil.Image img;
        switch (selectedDesign){
            default:
                img = (selectedChoice == null ? stoneCardFront : stoneCardBack);
                break;
            case 2:
                img = (selectedChoice == null ? goldCardFront : goldCardBack);
                break;
            case 3:
                img = (selectedChoice == null ? netheriteCardFront : netheriteCardBack);
                break;
        }
        img.setActualSize(img.getWidth() * 2, img.getHeight() * 2);

        //Now make some buttons for each choice
        for (AbstractSpellPart spellPart : poolSpells){
            ClientUtil.SimpleButton choiceButton =
                    new ClientUtil.SimpleButton(0,0, img.getWidth(), img.getHeight(), ITextComponent.nullToEmpty(""), (button) ->{
                        LOGGER.debug("YOU HAVE SELECTED: " + spellPart.getName());
                        glyphSelected(spellPart);
                    });

            choiceButton.hidden = true;
            this.addButton(choiceButton);
            this.glyphButtons.add(choiceButton);
        }
    }
}
