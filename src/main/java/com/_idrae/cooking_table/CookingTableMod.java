package com._idrae.cooking_table;

import com._idrae.cooking_table.network.PlaceNewRecipePacket;
import com._idrae.cooking_table.network.SetupGhostRecipePacket;
import com._idrae.cooking_table.util.RegistryHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CookingTableMod.MOD_ID)
@Mod.EventBusSubscriber(modid = CookingTableMod.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class CookingTableMod {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "idraes_cooking_table";
    private static final String NETWORK_PROTOCOL_VERSION = "1";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> NETWORK_PROTOCOL_VERSION,
            NETWORK_PROTOCOL_VERSION::equals,
            NETWORK_PROTOCOL_VERSION::equals
    );

    public CookingTableMod() {

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::setup);
        RegistryHandler.BLOCKS.register(modEventBus);
        RegistryHandler.ITEMS.register(modEventBus);
        RegistryHandler.CONTAINER_TYPES.register(modEventBus);
        RegistryHandler.SERIALIZERS.register(modEventBus);
        LOGGER.info("Blocks, items, containers, serializers registered.");
        MinecraftForge.EVENT_BUS.register(this);
        // MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        HANDLER.registerMessage(0, PlaceNewRecipePacket.class, PlaceNewRecipePacket::encode, PlaceNewRecipePacket::new, PlaceNewRecipePacket::handle);
        HANDLER.registerMessage(1, SetupGhostRecipePacket.class, SetupGhostRecipePacket::encode, SetupGhostRecipePacket::new, SetupGhostRecipePacket::handle);
        LOGGER.info("Handler messages registered.");

        // CapabilityManager.INSTANCE.register(OpenState.class, new OpenStateStorage(), OpenState::new);
        LOGGER.info("Capabilities registered.");
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {

        final IForgeRegistry<Item> registry = event.getRegistry();
        RegistryHandler.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            final Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
            final BlockItem blockItem = new BlockItem(block, properties);
            blockItem.setRegistryName(block.getRegistryName());
            registry.register(blockItem);
        });
        LOGGER.info("BlockItems registered.");
    }
}




