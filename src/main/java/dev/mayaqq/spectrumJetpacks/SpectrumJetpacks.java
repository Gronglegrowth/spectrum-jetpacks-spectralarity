package dev.mayaqq.spectrumJetpacks;

import dev.mayaqq.spectrumJetpacks.config.SpectrumJetpacksConfig;
import dev.mayaqq.spectrumJetpacks.networking.C2SPackets;
import dev.mayaqq.spectrumJetpacks.registry.JetpackItems;
import dev.mayaqq.spectrumJetpacks.registry.JetpackServerEvents;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectrumJetpacks implements ModInitializer {
	public static final String MOD_ID = "spectrumjetpacks";
	public static final String MOD_NAME = "Spectrum Jetpacks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	// Config
	public static SpectrumJetpacksConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("To the sky!");
		// Config
		AutoConfig.register(SpectrumJetpacksConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(SpectrumJetpacksConfig.class).getConfig();
		// Registries
		JetpackItems.register();
		C2SPackets.register();
		JetpackServerEvents.register();
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}