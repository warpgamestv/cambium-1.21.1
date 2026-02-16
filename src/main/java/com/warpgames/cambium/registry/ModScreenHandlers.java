package com.warpgames.cambium.registry;

import com.warpgames.cambium.Cambium;
import com.warpgames.cambium.menu.SolarConcentratorMenu;
import com.warpgames.cambium.menu.SolarDigesterMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos; // Import BlockPos
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers {

    public static final MenuType<SolarDigesterMenu> SOLAR_DIGESTER_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_digester_menu"),
            new ExtendedScreenHandlerType<>(
                    SolarDigesterMenu::new,
                    BlockPos.STREAM_CODEC
            )
    );

    public static final MenuType<SolarConcentratorMenu> SOLAR_CONCENTRATOR_SCREEN_HANDLER =
            Registry.register(BuiltInRegistries.MENU,
                    ResourceLocation.fromNamespaceAndPath(Cambium.MOD_ID, "solar_concentrator_menu"),
                    new ExtendedScreenHandlerType<>(
                            SolarConcentratorMenu::new,
                            BlockPos.STREAM_CODEC //
                    ));

    public static void registerScreenHandlers() {
        Cambium.LOGGER.info("Registering Screen Handlers for " + Cambium.MOD_ID);
    }
}