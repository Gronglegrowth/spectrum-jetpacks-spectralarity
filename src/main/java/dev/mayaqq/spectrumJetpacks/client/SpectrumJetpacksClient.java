package dev.mayaqq.spectrumJetpacks.client;

import dev.mayaqq.spectrumJetpacks.client.registry.JetpackClientEvents;
import dev.mayaqq.spectrumJetpacks.client.registry.JetpackKeybinds;
import dev.mayaqq.spectrumJetpacks.client.registry.JetpackTrinketRenderers;
import net.fabricmc.api.ClientModInitializer;

public class SpectrumJetpacksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        JetpackTrinketRenderers.register();
        JetpackKeybinds.register();
        JetpackClientEvents.register();
    }
}