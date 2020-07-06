package com._idrae.cooking_table.containers;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.network.SetupGhostRecipePacket;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;

public class CookingTableRecipePlacer {

    private CookingTableContainer container;
    private final PlayerEntity player;

    public CookingTableRecipePlacer(CookingTableContainer container) {
        this.container = container;
        this.player = container.getPlayer();
    }

    public void placeNewRecipe(boolean placeAll, IRecipe<?> recipe, ServerPlayerEntity player) {

        if (recipe != null && player.getRecipeBook().isUnlocked(recipe)) {
            if (this.placeIntoInventory() || player.isCreative()) {
                RecipeItemHelper recipeItemHelper = new RecipeItemHelper();
                recipeItemHelper.clear();
                player.inventory.accountStacks(recipeItemHelper);
                this.container.fillStackedContents(recipeItemHelper);
                if (recipeItemHelper.canCraft(recipe, (IntList) null)) {
                    tryPlaceRecipe((IRecipe<CraftingInventory>) recipe, placeAll, recipeItemHelper);
                } else {
                    this.clear();
                    CookingTableMod.HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SetupGhostRecipePacket(container.windowId, recipe));
                }
                player.inventory.markDirty();
            }
        }
    }

    protected void tryPlaceRecipe(IRecipe<CraftingInventory> recipe, boolean placeAll, RecipeItemHelper helper) {
        boolean flag = container.matches(recipe);
        int i = helper.getBiggestCraftableStack(recipe, (IntList) null);
        if (flag) {
            for(int j = 0; j < this.container.getHeight() * this.container.getWidth() + 1; ++j) {
                if (j != this.container.getOutputSlot()) {
                    ItemStack itemstack = this.container.getSlot(j).getStack();
                    if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
                        return;
                    }
                }
            }
        }

        int j1 = this.getMaxAmount(placeAll, i, flag);
        IntList intlist = new IntArrayList();
        if (helper.canCraft(recipe, intlist, j1)) {
            int k = j1;

            for(int l : intlist) {
                int i1 = RecipeItemHelper.unpack(l).getMaxStackSize();
                if (i1 < k) {
                    k = i1;
                }
            }

            if (helper.canCraft(recipe, intlist, k)) {
                this.clear();
                this.placeRecipe(this.container.getWidth(), this.container.getHeight(), this.container.getOutputSlot(), recipe, intlist.iterator(), k);
            }
        }
    }

    protected void clear() {
        for(int i = 0; i < this.container.getWidth() * this.container.getHeight() + 1; ++i) {
            if (i != this.container.getOutputSlot()) {
                this.giveToPlayer(i);
            }
        }

        this.container.clear();
    }

    protected void giveToPlayer(int slotIn) {
        ItemStack itemstack = this.container.getSlot(slotIn).getStack();
        if (!itemstack.isEmpty()) {
            for(; itemstack.getCount() > 0; this.container.getSlot(slotIn).decrStackSize(1)) {
                int i = this.player.inventory.storeItemStack(itemstack);
                if (i == -1) {
                    i = this.player.inventory.getFirstEmptyStack();
                }

                ItemStack itemstack1 = itemstack.copy();
                itemstack1.setCount(1);
                if (!this.player.inventory.add(i, itemstack1)) {
                    CookingTableMod.LOGGER.error("Can't find any space for item in the inventory");
                }
            }

        }
    }

    protected int getMaxAmount(boolean placeAll, int maxPossible, boolean recipeMatches) {
        int i = 1;
        if (placeAll) {
            i = maxPossible;
        } else if (recipeMatches) {
            i = 64;

            for(int j = 0; j < this.container.getWidth() * this.container.getHeight() + 1; ++j) {
                if (j != this.container.getOutputSlot()) {
                    ItemStack itemstack = this.container.getSlot(j).getStack();
                    if (!itemstack.isEmpty() && i > itemstack.getCount()) {
                        i = itemstack.getCount();
                    }
                }
            }

            if (i < 64) {
                ++i;
            }
        }

        return i;
    }

    private boolean placeIntoInventory() {
        List<ItemStack> list = Lists.newArrayList();
        int i = this.getNumEmptyPlayerSlots();

        for(int j = 0; j < this.container.getWidth() * this.container.getHeight() + 1; ++j) {
            if (j != this.container.getOutputSlot()) {
                ItemStack itemstack = this.container.getSlot(j).getStack().copy();
                if (!itemstack.isEmpty()) {
                    int k = player.inventory.storeItemStack(itemstack);
                    if (k == -1 && list.size() <= i) {
                        for(ItemStack itemstack1 : list) {
                            if (itemstack1.isItemEqual(itemstack) && itemstack1.getCount() != itemstack1.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                                itemstack1.grow(itemstack.getCount());
                                itemstack.setCount(0);
                                break;
                            }
                        }

                        if (!itemstack.isEmpty()) {
                            if (list.size() >= i) {
                                return false;
                            }

                            list.add(itemstack);
                        }
                    } else if (k == -1) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private int getNumEmptyPlayerSlots() {
        int i = 0;

        for(ItemStack itemstack : player.inventory.mainInventory) {
            if (itemstack.isEmpty()) {
                ++i;
            }
        }

        return i;
    }

    void placeRecipe(int width, int height, int outputSlot, IRecipe<?> recipe, Iterator<Integer> ingredients, int maxAmount) {
        int i = width;
        int j = height;
        if (recipe instanceof net.minecraftforge.common.crafting.IShapedRecipe) {
            net.minecraftforge.common.crafting.IShapedRecipe shapedrecipe = (net.minecraftforge.common.crafting.IShapedRecipe)recipe;
            i = shapedrecipe.getRecipeWidth();
            j = shapedrecipe.getRecipeHeight();
        }

        int k1 = 0;

        for(int k = 0; k < height; ++k) {
            if (k1 == outputSlot) {
                ++k1;
            }

            boolean flag = (float)j < (float)height / 2.0F;
            int l = MathHelper.floor((float)height / 2.0F - (float)j / 2.0F);
            if (flag && l > k) {
                k1 += width;
                ++k;
            }

            for(int i1 = 0; i1 < width; ++i1) {
                if (!ingredients.hasNext()) {
                    return;
                }

                flag = (float)i < (float)width / 2.0F;
                l = MathHelper.floor((float)width / 2.0F - (float)i / 2.0F);
                int j1 = i;
                boolean flag1 = i1 < i;
                if (flag) {
                    j1 = l + i;
                    flag1 = l <= i1 && i1 < l + i;
                }

                if (flag1) {
                    this.setSlotContents(ingredients, k1, maxAmount, k, i1);
                } else if (j1 == i1) {
                    k1 += width - i1;
                    break;
                }

                ++k1;
            }
        }

    }

    public void setSlotContents(Iterator<Integer> ingredients, int slotIn, int maxAmount, int y, int x) {
        Slot slot = this.container.getSlot(slotIn);
        ItemStack itemstack = RecipeItemHelper.unpack(ingredients.next());
        if (!itemstack.isEmpty()) {
            for(int i = 0; i < maxAmount; ++i) {
                this.consumeIngredient(slot, itemstack);
            }
        }

    }

    protected void consumeIngredient(Slot slotToFill, ItemStack ingredientIn) {
        int i = player.inventory.findSlotMatchingUnusedItem(ingredientIn);
        if (i != -1) {
            ItemStack itemstack = this.player.inventory.getStackInSlot(i).copy();
            if (!itemstack.isEmpty()) {
                if (itemstack.getCount() > 1) {
                    this.player.inventory.decrStackSize(i, 1);
                } else {
                    this.player.inventory.removeStackFromSlot(i);
                }

                itemstack.setCount(1);
                if (slotToFill.getStack().isEmpty()) {
                    slotToFill.putStack(itemstack);
                } else {
                    slotToFill.getStack().grow(1);
                }
            }
        }
    }
}
