package com._idrae.cooking_table.util;

import com._idrae.cooking_table.CookingTableMod;
import com._idrae.cooking_table.client.gui.CookingTableScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CookingTableMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(RegistryHandler.COOKING_TABLE_CONTAINER.get(), CookingTableScreen::new);
        CookingTableMod.LOGGER.info("Screen subscribed.");
    }
}
