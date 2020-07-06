package com._idrae.cooking_table.client.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public enum CookingTableRecipeBookCategories {
    COOKING_TABLE_SEARCH(new ItemStack(Items.COMPASS)),
    COOKING_TABLE_FOOD(new ItemStack(Items.BREAD)),
    COOKING_TABLE_OTHER(new ItemStack(Items.CLOCK));

    private final List<ItemStack> icons;

    private CookingTableRecipeBookCategories(ItemStack... items) {
        this.icons = ImmutableList.copyOf(items);
    }

    public List<ItemStack> getIcons() {
        return this.icons;
    }
}
