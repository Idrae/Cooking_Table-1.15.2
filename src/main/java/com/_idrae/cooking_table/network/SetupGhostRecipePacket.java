package com._idrae.cooking_table.network;

import com._idrae.cooking_table.client.gui.CookingTableGhostRecipePlacer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetupGhostRecipePacket {
    private final int windowId;
    private final ResourceLocation recipe;

    public SetupGhostRecipePacket(PacketBuffer buffer) {
        this.windowId = buffer.readInt();
        this.recipe = buffer.readResourceLocation();
    }

    public SetupGhostRecipePacket(int windowId, IRecipe<?> recipe) {
        this.windowId = windowId;
        this.recipe = recipe.getId();
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getRecipeId() {
        return this.recipe;
    }

    @OnlyIn(Dist.CLIENT)
    public int getWindowId() {
        return this.windowId;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeInt(windowId);
        buffer.writeResourceLocation(recipe);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.runWhenOn(Dist.CLIENT, () -> this::handleOnClient);

        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleOnClient() {
        CookingTableGhostRecipePlacer.handlePlaceGhostRecipe(Minecraft.getInstance(), getWindowId(), getRecipeId());
    }
}
