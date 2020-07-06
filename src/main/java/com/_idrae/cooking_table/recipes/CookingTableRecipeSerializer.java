package com._idrae.cooking_table.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;

public class CookingTableRecipeSerializer<T extends CookingTableRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CookingTableRecipe> {

    private static final ResourceLocation NAME = new ResourceLocation("idraes_cooking_table", "cooking");

    @Override
    public CookingTableRecipe read(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "group", "");
        Map<String, Ingredient> map = CookingTableRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
        String[] astring = CookingTableRecipe.shrink(CookingTableRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
        // String[] astring = CookingTableRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern"));
        int i = astring[0].length();
        int j = astring.length;
        NonNullList<Ingredient> nonnulllist = CookingTableRecipe.deserializeIngredients(astring, map, i, j);
        ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        return new CookingTableRecipe(recipeId, s, i, j, nonnulllist, itemstack);
    }

    @Nullable
    @Override
    public CookingTableRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        int i = buffer.readVarInt();
        int j = buffer.readVarInt();
        String s = buffer.readString(32767);
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

        for(int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.read(buffer));
        }

        ItemStack itemstack = buffer.readItemStack();
        return new CookingTableRecipe(recipeId, s, i, j, nonnulllist, itemstack);
    }

    @Override
    public void write(PacketBuffer buffer, CookingTableRecipe recipe) {
        buffer.writeVarInt(recipe.recipeWidth);
        buffer.writeVarInt(recipe.recipeHeight);
        buffer.writeString(recipe.group);

        for(Ingredient ingredient : recipe.recipeItems) {
            ingredient.write(buffer);
        }

        buffer.writeItemStack(recipe.recipeOutput);
    }
}
