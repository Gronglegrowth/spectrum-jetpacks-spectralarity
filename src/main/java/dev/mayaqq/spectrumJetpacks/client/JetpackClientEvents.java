package dev.mayaqq.spectrumJetpacks.client;

import dev.mayaqq.spectrumJetpacks.client.renderer.JetpacksTextRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class JetpackClientEvents {
    public static void register() {
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            JetpacksTextRenderer.renderJetpackInfo(matrices);
        });
    }
}
