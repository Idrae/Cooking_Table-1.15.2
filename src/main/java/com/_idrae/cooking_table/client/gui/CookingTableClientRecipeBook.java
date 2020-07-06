package com._idrae.cooking_table.client.gui;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.recipes.CookingTableRecipe;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CookingTableClientRecipeBook extends RecipeBook {

    @Override
    public boolean isUnlocked(@Nullable IRecipe<?> recipe) {
        return true;
    }

    private final RecipeManager recipeManager;
    private final Map<CookingTableRecipeBookCategories, List<RecipeList>> recipesByCategory = Maps.newHashMap();
    private final List<RecipeList> allRecipes = Lists.newArrayList();

    public CookingTableClientRecipeBook(RecipeManager p_i48186_1_) {
        this.recipeManager = p_i48186_1_;
    }

    public void rebuildTable() {
        this.allRecipes.clear();
        this.recipesByCategory.clear();
        Table<CookingTableRecipeBookCategories, String, RecipeList> table = HashBasedTable.create();

        for(IRecipe<?> irecipe : this.recipeManager.getRecipes()) {
            if (!irecipe.isDynamic()) {
                CookingTableRecipeBookCategories category = getCategory(irecipe);
                String s = irecipe.getGroup();
                RecipeList recipeList;
                if (s.isEmpty()) {
                    recipeList = this.newRecipeList(category);
                } else {
                    recipeList = table.get(category, s);
                    if (recipeList == null) {
                        recipeList = this.newRecipeList(category);
                        table.put(category, s, recipeList);
                    }
                }

                recipeList.add(irecipe);
            }
        }
    }

    private RecipeList newRecipeList(CookingTableRecipeBookCategories category) {
        RecipeList recipeList = new RecipeList();
        this.allRecipes.add(recipeList);
        this.recipesByCategory.computeIfAbsent(category, (category1) -> {
            return Lists.newArrayList();
        }).add(recipeList);

        if (category == CookingTableRecipeBookCategories.COOKING_TABLE_FOOD) {
            this.addRecipeListToCategory(CookingTableRecipeBookCategories.COOKING_TABLE_SEARCH, recipeList);
        }

        return recipeList;
    }

    private void addRecipeListToCategory(CookingTableRecipeBookCategories categories, RecipeList recipeList) {
        this.recipesByCategory.computeIfAbsent(categories, (categories1) -> {
            return Lists.newArrayList();
        }).add(recipeList);
    }

    private static CookingTableRecipeBookCategories getCategory(IRecipe<?> recipe) {
        IRecipeType<?> irecipetype = recipe.getType();


        if ((irecipetype == CookingTableRecipe.COOKING) ||
                (irecipetype == IRecipeType.CRAFTING && (recipe.getRecipeOutput().isFood() || recipe.getRecipeOutput().getItem() == Items.CAKE))) {
            return CookingTableRecipeBookCategories.COOKING_TABLE_FOOD;
        } else {
            return CookingTableRecipeBookCategories.COOKING_TABLE_OTHER;
        }


    }

    public static List<CookingTableRecipeBookCategories> getCategories() {
        return Lists.newArrayList(CookingTableRecipeBookCategories.COOKING_TABLE_SEARCH, CookingTableRecipeBookCategories.COOKING_TABLE_FOOD);

    }

    public List<RecipeList> getRecipes() {
        return this.allRecipes;

    }

    public List<RecipeList> getRecipes(CookingTableRecipeBookCategories categories) {
        return this.recipesByCategory.getOrDefault(categories, Collections.emptyList());
    }
}





