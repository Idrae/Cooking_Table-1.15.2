package com._idrae.cooking_table.data;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.data.open_state.OpenStateProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityHandler {

    public static final ResourceLocation IS_COOKABLE = new ResourceLocation(CookingTableMod.MOD_ID,  "is_cookable");

   // @SubscribeEvent
    public static void attachCapabilityEntity(final AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof PlayerEntity))
            return;
        CookingTableMod.LOGGER.info("attach cap called");
        event.addCapability(IS_COOKABLE, new OpenStateProvider());

    }
}
