package com._idrae.cooking_table.client.gui;

import com._idrae.cooking_table.containers.CookingTableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CookingTableGhostRecipePlacer {

    public static void handlePlaceGhostRecipe(Minecraft mc, int windowId, ResourceLocation recipeId) {
        CookingTableContainer container = (CookingTableContainer) mc.player.openContainer;

        if (container.windowId == windowId && container.getCanCraft(mc.player)) {
            RecipeManager manager = mc.world.getRecipeManager();
            manager.getRecipe(recipeId).ifPresent((p_217285_2_) -> {
                if (mc.currentScreen instanceof IRecipeShownListener) {
                    CookingTableRecipeBookGui recipebookgui = ((CookingTableScreen) mc.currentScreen).getRecipeGui();
                    recipebookgui.setupGhostRecipe(p_217285_2_, container.inventorySlots);
                }
            });
        }
    }
}
