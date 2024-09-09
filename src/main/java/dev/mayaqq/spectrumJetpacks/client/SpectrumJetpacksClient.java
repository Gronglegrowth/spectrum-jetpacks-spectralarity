package dev.mayaqq.spectrumJetpacks.client;

import dev.mayaqq.spectrumJetpacks.registry.JetpackTrinketRenderers;
import net.fabricmc.api.ClientModInitializer;

public class SpectrumJetpacksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JetpackTrinketRenderers.register();
        JetpackKeybinds.register();
        JetpackClientEvents.register();
    }
}