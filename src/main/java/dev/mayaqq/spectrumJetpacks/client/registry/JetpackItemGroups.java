package dev.mayaqq.spectrumJetpacks.client.registry;

import de.dafuqs.fractal.api.ItemSubGroup;
import de.dafuqs.spectrum.api.item_group.ItemGroupIDs;
import de.dafuqs.spectrum.registries.SpectrumItemGroups;
import dev.mayaqq.spectrumJetpacks.SpectrumJetpacks;
import dev.mayaqq.spectrumJetpacks.registry.JetpackItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public class JetpackItemGroups {

    public static final ItemGroup JETPACKS = new ItemSubGroup.Builder(
            SpectrumItemGroups.MAIN,
            SpectrumJetpacks.id("jetpacks"),
            Text.translatable("itemGroup.spectrumjetpacks.jetpacks")
    )
            .backgroundTexture(ItemGroupIDs.BACKGROUND_TEXTURE)
            .entries((displayContext, entries) -> {
                entries.add(JetpackItems.GEMSTONE_JETPACK);
                entries.add(JetpackItems.GEMSTONE_JETPACK);
            }
    ).build();

    public static void init() {}
}
