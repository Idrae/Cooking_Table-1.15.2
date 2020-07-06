package com._idrae.cooking_table.util;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.blocks.CookingTableBlock;
import com._idrae.cooking_table.containers.CookingTableContainer;
import com._idrae.cooking_table.recipes.CookingTableRecipe;
import com._idrae.cooking_table.recipes.CookingTableRecipeSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CookingTableMod.MOD_ID);
    public static final RegistryObject<Block> COOKING_TABLE_BLOCK = BLOCKS.register("cooking_table", () -> new CookingTableBlock(Block.Properties.from(Blocks.CRAFTING_TABLE)));

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CookingTableMod.MOD_ID);
    public static final RegistryObject<Item> DELICIOUS_COOKIE = ITEMS.register("delicious_cookie", () -> new Item(new Item.Properties().food(new Food.Builder().hunger(2).saturation(0.2F).build()).group(ItemGroup.FOOD)));

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, CookingTableMod.MOD_ID);
    public static final RegistryObject<ContainerType<CookingTableContainer>> COOKING_TABLE_CONTAINER = CONTAINER_TYPES.register("cooking_table", () -> IForgeContainerType.create(CookingTableContainer::new));

    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, CookingTableMod.MOD_ID);
    public static final RegistryObject<IRecipeSerializer<CookingTableRecipe>>  COOKING_RECIPES_SERIALIZER = SERIALIZERS.register("cooking", CookingTableRecipeSerializer::new);
}
