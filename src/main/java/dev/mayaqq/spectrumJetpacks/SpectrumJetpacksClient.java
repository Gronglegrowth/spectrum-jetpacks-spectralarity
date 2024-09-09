package dev.mayaqq.spectrumJetpacks;

import dev.mayaqq.spectrumJetpacks.registry.JetpackClientEvents;
import dev.mayaqq.spectrumJetpacks.registry.JetpackKeybinds;
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