package dev.mayaqq.spectrumJetpacks.registry;

import de.dafuqs.spectrum.registries.SpectrumItems;
import dev.mayaqq.spectrumJetpacks.items.JetpackItem;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.CONFIG;
import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.id;

public class ItemRegistry {
    public static final JetpackItem GEMSTONE_JETPACK = Registry.register(
            Registry.ITEM, id("gemstone_jetpack"), new JetpackItem(
                    SpectrumItems.Tab.EQUIPMENT.settings(1, Rarity.RARE),
                    id("gemstone_jetpack"),
                    CONFIG.gemstoneJetpackHorizontalSpeedMultiplier,
                    CONFIG.gemstoneJetpackVerticalSpeedAdditionPerTick,
                    CONFIG.gemstoneJetpackMaxHorizontalVelocity,
                    CONFIG.gemstoneJetpackMaxVerticalVelocity
            ));
    public static final JetpackItem BEDROCK_JETPACK = Registry.register(
            Registry.ITEM, id("bedrock_jetpack"), new JetpackItem(
                    SpectrumItems.Tab.EQUIPMENT.settings(1, Rarity.RARE),
                    id("bedrock_jetpack"),
                    CONFIG.bedrockJetpackHorizontalSpeedMultiplier,
                    CONFIG.bedrockJetpackVerticalSpeedAdditionPerTick,
                    CONFIG.bedrockJetpackMaxHorizontalVelocity,
                    CONFIG.bedrockJetpackMaxVerticalVelocity
            ));

    public static void register() {}
}