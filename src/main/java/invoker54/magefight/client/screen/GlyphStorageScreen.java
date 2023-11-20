package invoker54.magefight.client.screen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.magefight.capability.player.MagicDataCap;
import invoker54.magefight.client.ClientUtil;
import invoker54.magefight.config.MageFightConfig;
import invoker54.magefight.network.NetworkHandler;
import invoker54.magefight.network.message.SellGlyphMsg;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GlyphStorageScreen extends BaseCombatScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    public boolean isSellMode = false;
    protected GlyphList myList;

    @Override
    protected void init() {
        super.init();
        if (MageFightConfig.disableGlyphSystem == false) {

            //Center the glyph container image
            glyph_container.centerImageX(0, width);
            glyph_container.centerImageY(0, height);

            //Then center the glyph slot and move it
            glyph_slot.centerImageX(glyph_container.x0, glyph_container.getWidth());
            glyph_slot.moveTo(glyph_slot.x0, glyph_container.y0 + 16);

            //Get possible spell tiers and sort them
            List<Integer> spellTiers = new ArrayList<>();
            for (AbstractSpellPart spellPart : poolSpells) {
                if (!spellTiers.contains(spellPart.getTier().ordinal())) {
                    spellTiers.add(spellPart.getTier().ordinal());
                }
            }
            spellTiers.sort(Integer::compare);
            // LOGGER.debug("WHATS SPELL TIERS SIZE: " + spellTiers.size());

            //This will be the visual list
            myList = new GlyphList(glyph_container.x0 + 5, glyph_container.getWidth() - 10,
                    glyph_container.y0 + 5, glyph_container.getHeight() - 10);
            this.addWidget(myList);

            //This is for the Glyph slots, Spell Tier names, and buy/sell button
            for (Integer spellTier : spellTiers) {
                //This is the Spell Tier text at the top of each tier
                this.myList.addEntry(new CategoryEntry("Spell Tier " + (spellTier + 1)));

                //This is a sub list containing only the spells at this tier
                List<AbstractSpellPart> tierSpellList = unlockedSpells.stream().filter(
                        (spellPart) -> spellPart.getTier().ordinal() == spellTier).collect(Collectors.toList());

                //This will be the glyph slots
                do {
                    List<AbstractSpellPart> sixPackList = tierSpellList.subList(0, (Math.min(6, tierSpellList.size())));
                    this.myList.addEntry(new GlyphEntry(sixPackList, this.myList));
                    tierSpellList.removeAll(sixPackList);
                }
                while (!tierSpellList.isEmpty());
            }

            //Then add the buy and sell button right below
            this.myList.addEntry(new buttonEntry(this.myList));
        }
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        super.renderBackground(stack);

        if (MageFightConfig.disableGlyphSystem == false) {
            //Then render the glyph container
            glyph_container.RenderImage(stack);
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        if (MageFightConfig.disableGlyphSystem == false) {
//        //Then finally the glyph slot
//        glyph_slot.RenderImage(stack);
            ClientUtil.beginCrop(glyph_container.x0, glyph_container.getWidth(), glyph_container.y0 + 5, glyph_container.getHeight(), true);
            if (!this.myList.children().isEmpty()) {
                for (net.minecraft.client.gui.widget.Widget button : this.buttons) {
                    button.render(stack, mouseX, mouseY, partialTicks);
                }
                this.myList.render(stack, mouseX, mouseY, partialTicks);
            }
            ClientUtil.endCrop();
        }
        else {
            drawCenteredString(stack, this.font, new TranslationTextComponent("ars_mage_fight.buy_glyph.disabled"),
                    this.width / 2, this.height / 2, TextFormatting.RED.getColor());
        }
    }

    public void setSellMode(){
        isSellMode = !isSellMode;
    }

    public boolean hasLockedSpells(int tier){
        for (AbstractSpellPart spellPart : poolSpells){
         if (spellPart.getTier().ordinal() <= tier && !unlockedSpells.contains(spellPart)) return true;
        }
        return  false;
    }

//    public boolean mouseDragged(double xOrigin, double yOrigin, int mouseButton, double xDistance, double yDistance) {
//        if (mouseButton != 0) {
//            this.isScrolling = false;
//            return false;
//        } else {
//            if (!this.isScrolling) {
//                this.isScrolling = true;
//            } else {
//                this.scroll(xDistance, yDistance);
//            }
//
//            return true;
//        }
//    }

    public class GlyphList extends AbstractList<ListEntry> {
        public final List<ITextComponent> toolTip = new ArrayList<>();
        protected boolean sellMode = false;
        protected int sellIndex = -1;
        protected ListEntry hoverEntry = null;


        //Height is used for how long you want the top and bottom panels to be if you had
        //renderTopAndBottom set to true.
        public GlyphList(int x0, int width, int y0, int height) {
            super(ClientUtil.mC, width, 0, y0, y0 + height, 30);
            this.x0 = x0;
            this.x1 = x0 + width;
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
            this.setRenderHeader(false, 0);

            // LOGGER.debug("WHATS MY X0: " + x0);
            // LOGGER.debug("WHATS MY WIDTH: " + width);
            // LOGGER.debug("WHATS MY Y0: " + y0);
            // LOGGER.debug("WHATS MY HEIGHT: " + height);
        }

        public void recalcWidth(){
            int width = 0;

            for (ListEntry entry : this.children()){
                if (entry.getWidth() > width){
                    width = entry.getWidth();
                }
            }

            //Set width
            this.width = width;
        }

        public void updatePosition(int x0, int y0, int height) {
            this.setLeftPos(x0);
            this.y0 = y0;
            this.y1 = y0 + height;
        }

        @Override
        public void render(MatrixStack stack, int xMouse, int yMouse, float partialTicks) {
            super.render(stack, xMouse, yMouse, partialTicks);

            ClientUtil.endCrop();
            if (!toolTip.isEmpty()){
                renderWrappedToolTip(stack, this.toolTip, xMouse, yMouse, font);
                this.toolTip.clear();
            }
            ClientUtil.beginCrop(glyph_container.x0, glyph_container.getWidth(), glyph_container.y0 + 5, glyph_container.getHeight(), true);
        }

//        protected int getRowTop(int p_230962_1_) {
//            return this.y0 + 4 - (int)this.getScrollAmount() + p_230962_1_ * this.itemHeight + this.headerHeight;
//        }

        @Override
        protected void renderList(MatrixStack stack, int p_238478_2_, int p_238478_3_, int xMouse, int yMouse, float p_238478_6_) {
            int i = this.getItemCount();
            hoverEntry = null;
//            Tessellator tessellator = Tessellator.getInstance();
//            BufferBuilder bufferbuilder = tessellator.getBuilder();

            int currY0 = (int) (this.y0 - this.getScrollAmount());
            for(int index = 0; index < i; ++index) {
                //Top
                int k = currY0;
                //Bottom
//                int l = k + this.getEntry(index).getHeight();
//                  int y0 = p_238478_3_ + index * this.itemHeight + this.headerHeight;
                int height = this.getEntry(index).getHeight();
                ListEntry e = this.getEntry(index);
                int k1 = this.getRowWidth();
//                    if (this.isSelectedItem(index)) {
//                        int l1 = this.x0 + this.width / 2 - k1 / 2;
//                        int i2 = this.x0 + this.width / 2 + k1 / 2;
//                        RenderSystem.disableTexture();
//                        float f = this.isFocused() ? 1.0F : 0.5F;
//                        RenderSystem.color4f(f, f, f, 1.0F);
//                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
//                        bufferbuilder.vertex((double)l1, (double)(y0 + height + 2), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)i2, (double)(y0 + height + 2), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)i2, (double)(y0 - 2), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)l1, (double)(y0 - 2), 0.0D).endVertex();
//                        tessellator.end();
//                        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
//                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
//                        bufferbuilder.vertex((double)(l1 + 1), (double)(y0 + height + 1), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)(i2 - 1), (double)(y0 + height + 1), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)(i2 - 1), (double)(y0 - 1), 0.0D).endVertex();
//                        bufferbuilder.vertex((double)(l1 + 1), (double)(y0 - 1), 0.0D).endVertex();
//                        tessellator.end();
//                        RenderSystem.enableTexture();
//                    }

                int j2 = this.getRowLeft();
                e.render(stack, index, k, j2, k1, height, xMouse, yMouse, this.isMouseOver(xMouse, yMouse) && Objects.equals(this.getEntryAtPosition(xMouse, yMouse), e), p_238478_6_);
                currY0 += e.getHeight();
                if (e.isMouseOver(xMouse, yMouse)){
                    hoverEntry = e;
                }

            }
        }

        @Override
        public int getRowLeft() {
            return x0;
        }

        @Override
        public int addEntry(@Nonnull ListEntry entry) {
            return super.addEntry(entry);
        }

        @Override
        protected int getScrollbarPosition() {
            return x0 + getRowWidth();
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        public boolean mouseClicked(double xMouse, double yMouse, int button) {
            this.updateScrollingState(xMouse, yMouse, button);
            if (!this.isMouseOver(xMouse, yMouse)) {
                return false;
            } else {
                if (hoverEntry != null) {
                    if (hoverEntry.mouseClicked(xMouse, yMouse, button)) {
                        this.setFocused(hoverEntry);
                        this.setDragging(true);
                        return true;
                    }
                } else if (button == 0) {
                    this.clickedHeader((int)(xMouse - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(yMouse - (double)this.y0) + (int)this.getScrollAmount() - 4);
                    return true;
                }

                return false;
            }
        }
    }
    public class ListEntry extends AbstractList.AbstractListEntry<ListEntry> {
        protected GlyphList parent;
        protected int height = 0;
        protected int heightPadding = 1;
        protected boolean isMouseOver = false;
        public ListEntry(GlyphList parent){
            this.parent = parent;
        }
        public int getWidth(){
            return 0;
        }
        public void setHeight(int newHeight){
            this.height = newHeight;
        }
        public int getHeight(){
            return this.height + (heightPadding * 2);
        }
        @Override
        public void render(MatrixStack stack, int index, int y0, int x0, int rowWidth, int rowHeight, int xMouse, int yMouse, boolean isMouseOver, float partialTicks
        ) {
            this.isMouseOver = xMouse >= x0 && xMouse <= (x0 + rowWidth) && yMouse >= y0 && yMouse <= (y0 + rowHeight);
        }

        @Override
        public boolean isMouseOver(double xMouse, double yMouse) {
            return this.isMouseOver;
        }
    }
    //The spell tier name and what glyphs in the spell tier you have unlocked
    public class CategoryEntry extends ListEntry{
        protected String name;
        private final int blackColor = new Color(0,0,0,255).getRGB();

        public CategoryEntry(String name){
            super(null);
            this.name = name;
            this.setHeight(9);
        }
        @Override
        public int getWidth() {
            return font.width(name);
        }
        @Override
        public void render(MatrixStack stack, int index, int y0, int x0, int rowWidth, int rowHeight, int xMouse, int yMouse, boolean isMouseOver, float partialTicks
        ) {
            int x = (rowWidth - font.width(this.name))/2;
            int y = (rowHeight - 9)/2;

            font.draw(stack, this.name, x0 + x, y0 + y, blackColor);
            super.render(stack, index, y0, x0, rowWidth, rowHeight, xMouse, yMouse, isMouseOver, partialTicks);
        }
    }
    //Where the glyphs will sit
    public class GlyphEntry extends ListEntry{
        protected int redColor = new Color(255,0,0, 69).getRGB();
        final List<ClientUtil.Image> glyphImages = new ArrayList<>();
        final List<AbstractSpellPart> collectedGlyphs;
        protected final Map<String, AbstractSpellPart> spellMap;
        public GlyphEntry(List<AbstractSpellPart> spellPartList, GlyphList parent){
            super(parent);
            this.setHeight(20);
            spellMap = ArsNouveauAPI.getInstance().getSpell_map();
            this.collectedGlyphs = new ArrayList<>(spellPartList);

            //Get the image for each glyph
            for (AbstractSpellPart spellPart : collectedGlyphs){
                glyphImages.add(new ClientUtil.Image(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + spellPart.getIcon()),
                        0, 16, 0, 16, 16));
            }
        }

        @Override
        public boolean mouseClicked(double xMouse, double yMouse, int button) {
            // LOGGER.debug("I AM CLICKING ON GLYPH ENTRY STUFF");
            if (!glyphImages.isEmpty()) {
                for (int a = 0; a < glyphImages.size(); a++) {
                    //This is for the glyph tooltip
                    if (glyphImages.get(a).isMouseOver((int) xMouse, (int) yMouse)) {
                        if (isSellMode) {
                            setSellMode();
                            NetworkHandler.INSTANCE.sendToServer(new SellGlyphMsg(collectedGlyphs.get(a).getTag()));
                            MagicDataCap.getCap(ClientUtil.mC.player).removeSpell(collectedGlyphs.get(a));

                            buttons.clear();
                            children().clear();
                            init();
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void render(MatrixStack stack, int index, int y0, int x0, int rowWidth, int rowHeight, int xMouse, int yMouse, boolean isMouseOver, float partialTicks) {
            super.render(stack, index, y0, x0, rowWidth, rowHeight, xMouse, yMouse, isMouseOver, partialTicks);

            //First the Slot Background
            glyph_slot.moveTo(x0, y0);
            glyph_slot.centerImageX(x0, rowWidth);
            glyph_slot.RenderImage(stack);

            if (!glyphImages.isEmpty()) {
//                int posX = (glyph_slot.getWidth() / glyphImages.size());
                int padding = 1;

                //Then the glyphs
                for (int a = 0; a < glyphImages.size(); a++) {
                    ClientUtil.Image glyphImg = glyphImages.get(a);
                    glyphImg.moveTo(glyph_slot.x0 + 2 + ((glyphImg.getWidth() + padding) * a), y0 + padding + 1);
                    glyphImg.RenderImage(stack);
                    if (isSellMode){
                        ClientUtil.blitColor(stack, glyphImg.x0, glyphImg.getWidth(), glyphImg.y0, glyphImg.getHeight(), redColor);
                    }

                    //This is for the glyph tooltip
                    if (glyphImg.isMouseOver(xMouse, yMouse)) {
                        if (spellMap.containsKey(collectedGlyphs.get(a).getTag())) {
                            AbstractSpellPart spellPart = spellMap.get(collectedGlyphs.get(a).getTag());
                            this.parent.toolTip.add(new TranslationTextComponent(spellPart.getLocalizationKey()));
                            if (Screen.hasShiftDown()) {
                                this.parent.toolTip.add(spellPart.getBookDescLang());
                            } else {
                                this.parent.toolTip.add(new TranslationTextComponent("tooltip.ars_nouveau.hold_shift"));
                            }
                        }
                    }
                }
            }
        }
    }

    //For the Refund and Buy button
    public class buttonEntry extends ListEntry{
        int cost;

        private final ClientUtil.SimpleButton buyButton;
        private final ClientUtil.SimpleButton sellButton;

        public buttonEntry(GlyphList parent){
            super(parent);
            this.setHeight(15);

            //add up all the glyphs
            for (AbstractSpellPart spellPart : unlockedSpells){
                cost += ((spellPart.getTier().ordinal() + 1) * MageFightConfig.pricePerTier);
            }
            //Then add the base price at the end
            cost += MageFightConfig.baseGlyphPrice;
            cost = Math.min(MageFightConfig.maxCost, cost);

            ITextComponent buyText = ITextComponent.nullToEmpty(" Purchase: " + (cost) + " ");
            buyButton = new ClientUtil.SimpleButton(0,0, font.width(buyText.getString()), 13, buyText, (button) -> {
                ClientUtil.mC.setScreen(new BuyGlyphScreen());
            });
            addButton(buyButton);

            ITextComponent sellText = ITextComponent.nullToEmpty(" Sell ");
            sellButton = new ClientUtil.SimpleButton(0,0, font.width(sellText.getString()), 13, sellText, (button) -> {
                // LOGGER.debug("I AM CLICKING THIS BUTTON ");
                setSellMode();
                if (isSellMode) {
                    // LOGGER.debug("SETTING TO SELL MODE ");
                    button.setMessage(ITextComponent.nullToEmpty(" Cancel "));
                    button.setWidth(font.width(button.getMessage()));
                }
                else{
                    // LOGGER.debug("Turning OFF MODE ");
                    button.setMessage(sellText);
                    button.setWidth(font.width(button.getMessage()));
                }
            });
            addButton(sellButton);
        }

        public boolean canPurchase(){
            IMana mana = ManaCapability.getMana(ClientUtil.mC.player).resolve().get();
            List<ITextComponent> txtList = new ArrayList<>();

            //Check how many glyphs they have
            if (MageFightConfig.maxAllowedGlyphs != 0 && unlockedSpells.size() >= MageFightConfig.maxAllowedGlyphs){
                txtList.add(new TranslationTextComponent("ars_mage_fight.buy_glyph.size", MageFightConfig.maxAllowedGlyphs));
            }

            //Check if there are any glyphs left for the player tier or below
            if (!hasLockedSpells(mana.getBookTier())){
                txtList.add(new TranslationTextComponent("ars_mage_fight.buy_glyph.no_more"));
            }

            //Then finally check if they can afford it.
            if (ClientUtil.mC.player.totalExperience < cost){
                txtList.add(new TranslationTextComponent("ars_mage_fight.buy_glyph.broke_1").append((String.valueOf(ClientUtil.mC.player.totalExperience)))
                                .append(new TranslationTextComponent("ars_mage_fight.buy_glyph.broke_2")).append(String.valueOf(cost)));
            }

            if (this.isMouseOver) this.parent.toolTip.addAll(txtList);

            return txtList.isEmpty();
        }

        public boolean canSell(){
            //I just have to check if the player has any glyphs
            return !unlockedSpells.isEmpty();
        }

        @Override
        public void render(MatrixStack stack, int index, int y0, int x0, int rowWidth, int rowHeight, int xMouse, int yMouse, boolean isMouseOver, float partialTicks) {
            super.render(stack, index, y0, x0, rowWidth, rowHeight, xMouse, yMouse, isMouseOver, partialTicks);

            //Get the extra space in between the buttons and divide it by 3.
            int extraSpace = (rowWidth - (buyButton.getWidth() + sellButton.getWidth()))/3;

            buyButton.active = canPurchase();
            buyButton.x = x0 + extraSpace;
            buyButton.y = y0 + ((rowHeight - buyButton.getHeight())/2);

            sellButton.active = canSell();
            sellButton.x = buyButton.x + buyButton.getWidth() + extraSpace;
            sellButton.y = y0 + ((rowHeight - sellButton.getHeight())/2);
        }
    }
}
