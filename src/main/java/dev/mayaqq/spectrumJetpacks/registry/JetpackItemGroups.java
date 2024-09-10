package dev.mayaqq.spectrumJetpacks.registry;

import de.dafuqs.fractal.api.ItemSubGroupEvents;
import de.dafuqs.spectrum.api.item_group.ItemGroupIDs;

public class JetpackItemGroups {

    public static void init() {
        ItemSubGroupEvents.modifyEntriesEvent(ItemGroupIDs.SUBTAB_EQUIPMENT).register(entries -> {
            entries.add(JetpackItems.GEMSTONE_JETPACK);
            entries.add(JetpackItems.GEMSTONE_JETPACK);
        });
    }
}
