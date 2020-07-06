package com._idrae.cooking_table.client.gui;

import com._idrae.cooking_table.CookingTableMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.recipebook.RecipeTabToggleWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CookingTableRecipeTabToggleWidget extends ToggleWidget {

    private final CookingTableRecipeBookCategories category;
    private CookingTableClientRecipeBook clientRecipeBook;
    private float animationTime;

    public CookingTableRecipeTabToggleWidget(CookingTableRecipeBookCategories category, CookingTableClientRecipeBook clientRecipeBook) {
        super(0, 0, 35, 27, false);
        this.category = category;
        this.clientRecipeBook = clientRecipeBook;
        this.initTextureValues(153, 2, 35, 0, CookingTableRecipeBookGui.RECIPE_BOOK);
    }

    public void startAnimation(Minecraft mc) {

        List<RecipeList> list = clientRecipeBook.getRecipes();
        if (mc.player.openContainer instanceof RecipeBookContainer) {
            label25:
            for(RecipeList recipelist : list) {
                Iterator iterator = recipelist.getRecipes(clientRecipeBook.isFilteringCraftable((RecipeBookContainer)mc.player.openContainer)).iterator();

                while(true) {
                    if (!iterator.hasNext()) {
                        continue label25;
                    }

                    IRecipe<?> irecipe = (IRecipe)iterator.next();
                    if (clientRecipeBook.isNew(irecipe)) {
                        break;
                    }
                }

                this.animationTime = 15.0F;
                return;
            }

        }
    }

    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        if (this.animationTime > 0.0F) {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * (float)Math.PI));
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            RenderSystem.scalef(1.0F, f, 1.0F);
            RenderSystem.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.xTexStart;
        int j = this.yTexStart;
        if (this.stateTriggered) {
            i += this.xDiffTex;
        }

        if (this.isHovered()) {
            j += this.yDiffTex;
        }

        int k = this.x;
        if (this.stateTriggered) {
            k -= 2;
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(k, this.y, i, j, this.width, this.height);
        RenderSystem.enableDepthTest();
        this.renderIcon(minecraft.getItemRenderer());
        if (this.animationTime > 0.0F) {
            RenderSystem.popMatrix();
            this.animationTime -= p_renderButton_3_;
        }

    }

    private void renderIcon(ItemRenderer p_193920_1_) {
        List<ItemStack> list = this.category.getIcons();
        int i = this.stateTriggered ? -2 : 0;
        if (list.size() == 1) {
            p_193920_1_.renderItemAndEffectIntoGUI(list.get(0), this.x + 9 + i, this.y + 5);
        } else if (list.size() == 2) {
            p_193920_1_.renderItemAndEffectIntoGUI(list.get(0), this.x + 3 + i, this.y + 5);
            p_193920_1_.renderItemAndEffectIntoGUI(list.get(1), this.x + 14 + i, this.y + 5);
        }

    }

    public CookingTableRecipeBookCategories getCategory() {
        return this.category;
    }

    public boolean checkTabVisible(CookingTableClientRecipeBook clientRecipeBook) {
        List<RecipeList> list = clientRecipeBook.getRecipes(this.category);
        this.visible = false;
        if (list != null) {
            for(RecipeList recipelist : list) {
                if (recipelist.isNotEmpty()) {
                    if (recipelist.containsValidRecipes()) {
                        this.visible = true;
                        break;
                    }
                }
            }
        }
        return this.visible;
    }
}
