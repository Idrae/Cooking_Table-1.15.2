package com._idrae.cooking_table.compat.jei;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.recipes.CookingTableRecipe;
import com._idrae.cooking_table.util.RegistryHandler;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CookingTableRecipeJEICategory implements IRecipeCategory<CookingTableRecipe> {

    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot = 1;

    public static final ResourceLocation UID = new ResourceLocation(CookingTableMod.MOD_ID, "textures/gui/jei/gui_vanilla.png");
    private static final String TITLE = "Cooking";
    private final IDrawable background;
    private final IDrawable icon;
    private final ICraftingGridHelper craftingGridHelper;

    public static final int width = 116;
    public static final int height = 54;


    public CookingTableRecipeJEICategory(IGuiHelper helper) {
        background = helper.createDrawable(UID, 0, 60, width, height);
        icon = helper.createDrawableIngredient(new ItemStack(RegistryHandler.COOKING_TABLE_BLOCK.get()));
        craftingGridHelper = helper.createCraftingGridHelper(craftInputSlot);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends CookingTableRecipe> getRecipeClass() {
        return CookingTableRecipe.class;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }


    @Override
    public void setIngredients(CookingTableRecipe recipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(recipe.getIngredients());
        iIngredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CookingTableRecipe recipe, IIngredients ingredients) {

        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
                guiItemStacks.set(index, ingredients.getInputs(VanillaTypes.ITEM).get(index-1));
            }
        }

        guiItemStacks.init(craftOutputSlot, false, 94, 18);
        guiItemStacks.set(craftOutputSlot, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }
}
