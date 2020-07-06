package com._idrae.cooking_table.blocks;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.containers.CookingTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.ServerRecipePlacer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CookingTableBlock extends Block {

    private static final ITextComponent crafting_text = new TranslationTextComponent("container.crafting");

    public CookingTableBlock(Block.Properties properties) {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if (!worldIn.isRemote) {
            player.openContainer(state.getContainer(worldIn, pos));
        }
        return ActionResultType.SUCCESS;
    }

    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {

        return new SimpleNamedContainerProvider((p_220270_2_, p_220270_3_, p_220270_4_) -> {
            return new CookingTableContainer(p_220270_2_, p_220270_3_, IWorldPosCallable.of(worldIn, pos));
        }, crafting_text);
    }
}
