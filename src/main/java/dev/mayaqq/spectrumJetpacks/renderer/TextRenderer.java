package dev.mayaqq.spectrumJetpacks.renderer;

import dev.mayaqq.spectrumJetpacks.items.JetpackItem;
import dev.mayaqq.spectrumJetpacks.utils.EquipUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static dev.mayaqq.spectrumJetpacks.registry.KeybindRegistry.*;

public class TextRenderer {
    public static void renderJetpackInfo(MatrixStack matrices) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (EquipUtils.jetpackNumber(player) > 0) {
            String toggleString = toggleKey.isPressed() ? "§aON" : "§cOFF";
            String hoverString = hoverKey.isPressed() ? "§aON" : "§cOFF";
            Text toggleText = Text.of("Jetpack: " + toggleString);
            Text hoverText = Text.of("Hovering: " + hoverString);
            net.minecraft.client.font.TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int width = textRenderer.getWidth(hoverText);
            textRenderer.drawWithShadow(matrices, toggleText, 45 - width / 2f, 110, 0xFFFFFF);
            textRenderer.drawWithShadow(matrices, hoverText, 45 - width / 2f, 120, 0xFFFFFF);
        }
    }
}