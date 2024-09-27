package dev.mayaqq.spectrumJetpacks.registry;

import dev.mayaqq.spectrumJetpacks.renderer.TextRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class EventRegistry {
    public static void register() {
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            TextRenderer.renderJetpackInfo(matrices);
        });
    }
}
