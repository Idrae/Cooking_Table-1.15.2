package com._idrae.cooking_table.client.gui;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.containers.CookingTableContainer;
import com._idrae.cooking_table.data.open_state.OpenState;
import com._idrae.cooking_table.network.PlaceNewRecipePacket;
import com._idrae.cooking_table.recipes.CookingTableRecipe;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.recipebook.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class CookingTableRecipeBookGui extends RecipeBookGui implements IRenderable, IGuiEventListener, IRecipeUpdateListener, IRecipePlacer<Ingredient> {
    protected static final ResourceLocation RECIPE_BOOK = new ResourceLocation("idraes_cooking_table", "textures/gui/cooking_recipe_book.png");
    private int xOffset;
    private int width;
    private int height;
    protected final GhostRecipe ghostRecipe = new GhostRecipe();
    private final List<CookingTableRecipeTabToggleWidget> recipeTabs = Lists.newArrayList();
    private CookingTableRecipeTabToggleWidget currentTab;
    protected ToggleWidget toggleRecipesBtn;
    protected CookingTableContainer container;
    protected Minecraft mc;
    private TextFieldWidget searchBar;
    private String lastSearch = "";
    protected CookingTableClientRecipeBook recipeBook;
    protected final RecipeBookPage recipeBookPage = new RecipeBookPage();
    protected final RecipeItemHelper stackedContents = new RecipeItemHelper();
    private int timesInventoryChanged;
    private boolean keyDown;
    private boolean isFliteringCookable = false;

    public void init(int widthIn, int heightIn, Minecraft minecraftIn, boolean widthTooNarrowIn, CookingTableContainer containerIn) {
        this.mc = minecraftIn;
        this.width = widthIn;
        this.height = heightIn;
        this.container = containerIn;
        minecraftIn.player.openContainer = containerIn;
        this.mc.player.getRecipeBook().setFilteringCraftable(false);
        this.recipeBook = new CookingTableClientRecipeBook(minecraftIn.world.getRecipeManager());
        this.recipeBook.rebuildTable();
        this.timesInventoryChanged = minecraftIn.player.inventory.getTimesChanged();
        this.setVisible(OpenState.isOpenState());
        if (this.isVisible()) {
            this.initSearchBar(widthTooNarrowIn);
        }

        minecraftIn.keyboardListener.enableRepeatEvents(true);
    }

    public void initSearchBar(boolean widthTooNarrowIn) {
        this.xOffset = widthTooNarrowIn ? 0 : 86;
        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;
        this.stackedContents.clear();
        this.mc.player.inventory.accountStacks(this.stackedContents);
        this.container.fillStackedContents(this.stackedContents);
        String s = this.searchBar != null ? this.searchBar.getText() : "";
        this.searchBar = new TextFieldWidget(this.mc.fontRenderer, i + 25, j + 14, 80, 9 + 5, I18n.format("itemGroup.search"));
        this.searchBar.setMaxStringLength(50);
        this.searchBar.setEnableBackgroundDrawing(false);
        this.searchBar.setVisible(true);
        this.searchBar.setTextColor(16777215);
        this.searchBar.setText(s);
        this.recipeBookPage.init(this.mc, i, j);
        this.recipeBookPage.addListener(this);
        // this.toggleRecipesBtn = new ToggleWidget(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.container));
        this.toggleRecipesBtn = new ToggleWidget(i + 110, j + 12, 26, 16, false);
        this.initBtnTexture();
        this.recipeTabs.clear();


        for (CookingTableRecipeBookCategories category : this.container.getCookingTableRecipeBookCategories()) {
            this.recipeTabs.add(new CookingTableRecipeTabToggleWidget(category, recipeBook));
        }

        if (this.currentTab != null) {
            this.currentTab = this.recipeTabs.stream().filter((tabToggleWidget) -> {
                return tabToggleWidget.getCategory().equals(this.currentTab.getCategory());
            }).findFirst().orElse((CookingTableRecipeTabToggleWidget) null);
        }

        if (this.currentTab == null) {
            this.currentTab = this.recipeTabs.get(0);
        }

        this.currentTab.setStateTriggered(true);
        this.updateCollections(false);
        this.updateTabs();
    }

    public boolean changeFocus(boolean p_changeFocus_1_) {
        return false;
    }

    protected void initBtnTexture() {
        this.toggleRecipesBtn.initTextureValues(152, 41, 28, 18, RECIPE_BOOK);
    }

    public void removed() {
        this.searchBar = null;
        this.currentTab = null;
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    public int updateScreenPosition(boolean p_193011_1_, int p_193011_2_, int p_193011_3_) {
        int i;
        if (this.isVisible() && !p_193011_1_) {
            i = 177 + (p_193011_2_ - p_193011_3_ - 200) / 2;
        } else {
            i = (p_193011_2_ - p_193011_3_) / 2;
        }

        return i;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.recipeBook.isGuiOpen();
    }

    protected void setVisible(boolean visible) {
        this.recipeBook.setGuiOpen(visible);
        OpenState.setOpenState(visible);
        if (!visible) {
            this.recipeBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }

    public void slotClicked(@Nullable Slot slotIn) {
        if (slotIn != null && slotIn.slotNumber < this.container.getSize()) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }

    }

    private void updateCollections(boolean p_193003_1_) {
        List<RecipeList> list = this.recipeBook.getRecipes(this.currentTab.getCategory());
        notifyCallerIsCookingTable(list);
        list.forEach((recipeList) -> {
            recipeList.canCraft(this.stackedContents, this.container.getWidth(), this.container.getHeight(), this.mc.player.getRecipeBook());
        });

        list.forEach((recipeList) -> {
            recipeList.updateKnownRecipes(this.mc.player.getRecipeBook());
        });

        List<RecipeList> list1 = Lists.newArrayList(list);
        list1.removeIf((recipeList) -> {
            return !recipeList.isNotEmpty();
        });
        list1.removeIf((recipeList) -> {
            return !recipeList.containsValidRecipes();
        });
        String s = this.searchBar.getText();
        if (!s.isEmpty()) {

            ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<>(this.mc.getSearchTree(SearchTreeManager.RECIPES).search(s.toLowerCase(Locale.ROOT)));
            boolean flag;
            ArrayList<RecipeList> deleteList = new ArrayList<RecipeList>();

            for (RecipeList recipeList : list1) {
                flag = false;
                label:
                for (IRecipe<?> recipe : recipeList.getRecipes()) {
                    for (RecipeList oRecipeList : objectset) {
                        for (IRecipe<?> oRecipe : oRecipeList.getRecipes()) {
                            if (recipe.equals(oRecipe)) {
                                flag = true;
                                break label;
                            }
                        }
                    }
                }
                if (!flag) {
                    deleteList.add(recipeList);
                }
            }
            list1.removeAll(deleteList);
        }

        if (this.recipeBook.isFilteringCraftable(this.container)) {
            list1.removeIf((recipeList) -> {
                return !recipeList.containsCraftableRecipes();
            });
        }

        this.recipeBookPage.updateLists(list1, p_193003_1_);
    }

    private void updateTabs() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int k = 27;
        int l = 0;

        for (CookingTableRecipeTabToggleWidget recipeTabToggleWidget : this.recipeTabs) {
            CookingTableRecipeBookCategories category = recipeTabToggleWidget.getCategory();
            if (category != CookingTableRecipeBookCategories.COOKING_TABLE_SEARCH) {
                if (recipeTabToggleWidget.checkTabVisible(this.recipeBook)) {
                    recipeTabToggleWidget.setPosition(i, j + 27 * l++);
                    recipeTabToggleWidget.startAnimation(this.mc);
                }
            } else {
                recipeTabToggleWidget.visible = true;
                recipeTabToggleWidget.setPosition(i, j + 27 * l++);
            }
        }

    }

    public void tick() {
        if (this.isVisible()) {
            if (this.timesInventoryChanged != this.mc.player.inventory.getTimesChanged()) {
                this.updateStackedContents();
                this.timesInventoryChanged = this.mc.player.inventory.getTimesChanged();
            }
        }
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.mc.player.inventory.accountStacks(this.stackedContents);
        this.container.fillStackedContents(this.stackedContents);
        this.updateCollections(false);
    }

    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        if (this.isVisible()) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 100.0F);
            this.mc.getTextureManager().bindTexture(RECIPE_BOOK);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;
            this.blit(i, j, 1, 1, 147, 166);
            this.searchBar.render(p_render_1_, p_render_2_, p_render_3_);

            for (CookingTableRecipeTabToggleWidget recipetabtogglewidget : this.recipeTabs) {
                recipetabtogglewidget.render(p_render_1_, p_render_2_, p_render_3_);
            }

            this.toggleRecipesBtn.render(p_render_1_, p_render_2_, p_render_3_);
            this.recipeBookPage.render(i, j, p_render_1_, p_render_2_, p_render_3_);
            RenderSystem.popMatrix();
        }
    }

    public void renderTooltip(int p_191876_1_, int p_191876_2_, int p_191876_3_, int p_191876_4_) {
        if (this.isVisible()) {
            this.recipeBookPage.renderTooltip(p_191876_3_, p_191876_4_);
            if (this.toggleRecipesBtn.isHovered()) {
                String s = this.recipeButtonHovered();
                if (this.mc.currentScreen != null) {
                    this.mc.currentScreen.renderTooltip(s, p_191876_3_, p_191876_4_);
                }
            }

            this.renderGhostRecipeTooltip(p_191876_1_, p_191876_2_, p_191876_3_, p_191876_4_);
        }
    }

    protected String recipeButtonHovered() {
        return I18n.format(this.toggleRecipesBtn.isStateTriggered() ? new TranslationTextComponent("gui.toggle_button.cooking_table").getUnformattedComponentText(): "gui.recipebook.toggleRecipes.all");
    }

    private void renderGhostRecipeTooltip(int p_193015_1_, int p_193015_2_, int p_193015_3_, int p_193015_4_) {
        ItemStack itemstack = null;

        for (int i = 0; i < this.ghostRecipe.size(); ++i) {
            GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
            int j = ghostrecipe$ghostingredient.getX() + p_193015_1_;
            int k = ghostrecipe$ghostingredient.getY() + p_193015_2_;
            if (p_193015_3_ >= j && p_193015_4_ >= k && p_193015_3_ < j + 16 && p_193015_4_ < k + 16) {
                itemstack = ghostrecipe$ghostingredient.getItem();
            }
        }

        if (itemstack != null && this.mc.currentScreen != null) {
            this.mc.currentScreen.renderTooltip(this.mc.currentScreen.getTooltipFromItem(itemstack), p_193015_3_, p_193015_4_);
        }

    }

    public void renderGhostRecipe(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
        this.ghostRecipe.render(this.mc, p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
    }

    public boolean mouseClicked(double posX, double posY, int p_mouseClicked_5_) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.recipeBookPage.func_198955_a(posX, posY, p_mouseClicked_5_, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
                IRecipe<?> irecipe = this.recipeBookPage.getLastClickedRecipe();
                RecipeList recipelist = this.recipeBookPage.getLastClickedRecipeList();

                if (irecipe != null && recipelist != null) {
                    if (!recipelist.isCraftable(irecipe) && this.ghostRecipe.getRecipe() == irecipe) {
                        return false;
                    }

                    this.ghostRecipe.clear();
                    CookingTableMod.HANDLER.sendToServer(new PlaceNewRecipePacket(this.mc.player.openContainer.windowId, irecipe, Screen.hasShiftDown()));
                    if (!this.isOffsetNextToMainGUI()) {
                        this.setVisible(false);
                    }
                }

                return true;
            } else if (this.searchBar.mouseClicked(posX, posY, p_mouseClicked_5_)) {
                return true;
            } else if (this.toggleRecipesBtn.mouseClicked(posX, posY, p_mouseClicked_5_)) {
                boolean flag = this.toggleCraftableFilter();
                this.toggleRecipesBtn.setStateTriggered(flag);
                this.sendUpdateSettings();
                this.updateCollections(false);
                return true;
            } else {
                for (CookingTableRecipeTabToggleWidget recipetabtogglewidget : this.recipeTabs) {
                    if (recipetabtogglewidget.mouseClicked(posX, posY, p_mouseClicked_5_)) {
                        if (this.currentTab != recipetabtogglewidget) {
                            this.currentTab.setStateTriggered(false);
                            this.currentTab = recipetabtogglewidget;
                            this.currentTab.setStateTriggered(true);
                            this.updateCollections(true);
                        }

                        return true;
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    protected boolean toggleCraftableFilter() {
        boolean flag = !isFliteringCookable;
        isFliteringCookable = !isFliteringCookable;
        this.recipeBook.setFilteringCraftable(flag);
        return flag;
    }

    public boolean isMouseInsideGUI(double mouseX, double mouseY, int guiLeft, int guiTop, int xSize, int ySize, int mouseButton) {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean flag = mouseX < (double) guiLeft || mouseY < (double) guiTop || mouseX >= (double) (guiLeft + xSize) || mouseY >= (double) (guiTop + ySize);
            boolean flag1 = (double) (guiLeft - 147) < mouseX && mouseX < (double) guiLeft && (double) guiTop < mouseY && mouseY < (double) (guiTop + ySize);
            return flag && !flag1 && !this.currentTab.isHovered();
        }
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        this.keyDown = false;
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (p_keyPressed_1_ == 256 && !this.isOffsetNextToMainGUI()) {
                this.setVisible(false);
                return true;
            } else if (this.searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
                this.updateSearch();
                return true;
            } else if (this.searchBar.isFocused() && this.searchBar.getVisible() && p_keyPressed_1_ != 256) {
                return true;
            } else if (this.mc.gameSettings.keyBindChat.matchesKey(p_keyPressed_1_, p_keyPressed_2_) && !this.searchBar.isFocused()) {
                this.keyDown = true;
                this.searchBar.setFocused2(true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.keyDown = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (this.keyDown) {
            return false;
        } else if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.searchBar.charTyped(p_charTyped_1_, p_charTyped_2_)) {
                this.updateSearch();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
        return false;
    }

    private void updateSearch() {
        String s = this.searchBar.getText().toLowerCase(Locale.ROOT);
        this.pirateRecipe(s);
        if (!s.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = s;
        }

    }

    /**
     * "Check if we should activate the pirate speak easter egg"
     */
    private void pirateRecipe(String text) {
        if ("excitedze".equals(text)) {
            LanguageManager languagemanager = this.mc.getLanguageManager();
            Language language = languagemanager.getLanguage("en_pt");
            if (languagemanager.getCurrentLanguage().compareTo(language) == 0) {
                return;
            }

            languagemanager.setCurrentLanguage(language);
            this.mc.gameSettings.language = language.getCode();
            net.minecraftforge.client.ForgeHooksClient.refreshResources(this.mc, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
            this.mc.fontRenderer.setBidiFlag(languagemanager.isCurrentLanguageBidirectional());
            this.mc.gameSettings.saveOptions();
        }

    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.updateTabs();
        if (this.isVisible()) {
            this.updateCollections(false);
        }

    }

    public void recipesShown(List<IRecipe<?>> recipes) {
        for (IRecipe<?> irecipe : recipes) {
            this.mc.player.removeRecipeHighlight(irecipe);
        }

    }

    public void setupGhostRecipe(IRecipe<?> recipe, List<Slot> slotList) {
        ItemStack itemstack = recipe.getRecipeOutput();
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (slotList.get(0)).xPos, (slotList.get(0)).yPos);
        this.placeRecipe(this.container.getWidth(), this.container.getHeight(), this.container.getOutputSlot(), recipe, recipe.getIngredients().iterator(), 0);
    }

    public void setSlotContents(Iterator<Ingredient> ingredients, int slotIn, int maxAmount, int y, int x) {
        Ingredient ingredient = ingredients.next();
        if (!ingredient.hasNoMatchingItems()) {
            Slot slot = this.container.inventorySlots.get(slotIn);
            this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
        }
    }

    protected void sendUpdateSettings() {
        if (this.mc.getConnection() != null) {
            this.mc.getConnection().sendPacket(new CRecipeInfoPacket(this.recipeBook.isGuiOpen(), this.recipeBook.isFilteringCraftable(), this.recipeBook.isFurnaceGuiOpen(), this.recipeBook.isFurnaceFilteringCraftable(), this.recipeBook.func_216758_e(), this.recipeBook.func_216761_f()));
        }
    }

    private void notifyCallerIsCookingTable(List<RecipeList> list) {
        for (RecipeList recipeList : list) {
            for (IRecipe<?> recipe : recipeList.getRecipes()) {
                if (recipe instanceof CookingTableRecipe) {
                    ((CookingTableRecipe) recipe).isCalledByCookingTable = true;
                    // remove the break when adding more special recipes
                    break;
                }
            }
        }
    }
}
