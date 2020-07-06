package com._idrae.cooking_table.network;

import com._idrae.cooking_table.containers.CookingTableContainer;
import com._idrae.cooking_table.containers.CookingTableRecipePlacer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaceNewRecipePacket {

    private final int windowId;
    private final ResourceLocation recipeId;
    private final boolean placeAll;

    public PlaceNewRecipePacket(PacketBuffer buffer) {
        this.windowId = buffer.readInt();
        this.recipeId = buffer.readResourceLocation();
        this.placeAll = buffer.readBoolean();
    }

    public PlaceNewRecipePacket(int windowId, IRecipe<?> recipe, boolean placeAll) {
        this.windowId = windowId;
        this.recipeId = recipe.getId();
        this.placeAll = placeAll;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeInt(windowId);
        buffer.writeResourceLocation(recipeId);
        buffer.writeBoolean(placeAll);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (!player.isSpectator() && player.openContainer.windowId == this.getWindowId() && player.openContainer.getCanCraft(player) && player.openContainer instanceof CookingTableContainer) {
                player.getServer().getRecipeManager().getRecipe(this.getRecipeId()).ifPresent((recipe) -> {
                    CookingTableRecipePlacer recipePlacer = new CookingTableRecipePlacer((CookingTableContainer)player.openContainer);
                    recipePlacer.placeNewRecipe(this.shouldPlaceAll(), recipe, player);
                });
            }
        });
        context.get().setPacketHandled(true);
    }

    public int getWindowId() {
        return this.windowId;
    }

    public ResourceLocation getRecipeId() {
        return this.recipeId;
    }

    public boolean shouldPlaceAll() {
        return this.placeAll;
    }
}
