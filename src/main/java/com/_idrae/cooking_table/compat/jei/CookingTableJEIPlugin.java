package com._idrae.cooking_table.compat.jei;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.client.gui.CookingTableScreen;
import com._idrae.cooking_table.containers.CookingTableContainer;
import com._idrae.cooking_table.recipes.CookingTableRecipe;
import com._idrae.cooking_table.util.RegistryHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;

@JeiPlugin
public class CookingTableJEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(CookingTableMod.MOD_ID, "main");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CookingTableRecipeJEICategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Collection<IRecipe<?>> recipes = Minecraft.getInstance().world.getRecipeManager().getRecipes();
        recipes.removeIf(recipe -> (recipe.getType() != CookingTableRecipe.COOKING));
        registration.addRecipes(recipes, CookingTableRecipeJEICategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryHandler.COOKING_TABLE_BLOCK.get()), CookingTableRecipeJEICategory.UID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CookingTableContainer.class, CookingTableRecipeJEICategory.UID, 1, 9, 10, 36);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CookingTableScreen.class, 88, 32, 28, 23, CookingTableRecipeJEICategory.UID);

    }
}
