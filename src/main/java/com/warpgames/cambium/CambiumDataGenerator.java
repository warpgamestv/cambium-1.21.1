package com.warpgames.cambium;

import com.warpgames.cambium.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class CambiumDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModEnLangProvider::new);
		pack.addProvider(ModLootTableProvider::new);
		pack.addProvider(SplicingRecipeProvider::new);
		pack.addProvider(ModItemTagProvider::new);
	}
}
