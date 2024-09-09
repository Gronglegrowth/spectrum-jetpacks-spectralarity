package dev.mayaqq.spectrumJetpacks.client.renderer;

import dev.mayaqq.spectrumJetpacks.items.JetpackItem;
import dev.mayaqq.spectrumJetpacks.utils.EquipUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static dev.mayaqq.spectrumJetpacks.client.registry.JetpackKeybinds.*;

public class JetpacksTextRenderer {
    public static void renderJetpackInfo(DrawContext drawContext) {
        ItemStack jetpackStack = JetpackItem.equippedJetpack;
        if (jetpackStack != null) {
            long energyNum = EquipUtils.getEnergy(jetpackStack);
            long energyPercent = energyNum / (((JetpackItem) jetpackStack.getItem()).maxInk / 100);
            String energyString = energyPercent + "%";
            if (energyPercent >= 50) {
                energyString = "§a" + energyString;
            } else if (energyPercent >= 25) {
                energyString = "§e" + energyString;
            } else {
                energyString = "§c" + energyString;
            }
            Text energyText = Text.of("Stored Ink: " + energyString);
            String toggleString = toggleKey.isPressed() ? "§aON" : "§cOFF";
            String hoverString = hoverKey.isPressed() ? "§aON" : "§cOFF";
            Text toggleText = Text.of("Toggled: " + toggleString);
            Text hoverText = Text.of("Hovering: " + hoverString);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int width = textRenderer.getWidth(energyText);
            drawContext.drawTextWithShadow(textRenderer, energyText.asOrderedText(), (int) (45 - width / 2f), 100, 0xFFFFFF);
            drawContext.drawTextWithShadow(textRenderer, toggleText.asOrderedText(), (int) (45 - width / 2f), 110, 0xFFFFFF);
            drawContext.drawTextWithShadow(textRenderer, hoverText.asOrderedText(), (int) (45 - width / 2f), 120, 0xFFFFFF);
        }
    }
}