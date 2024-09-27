package dev.mayaqq.spectrumJetpacks.networking;

import de.dafuqs.spectrum.energy.InkPowered;
import de.dafuqs.spectrum.energy.color.InkColor;
import dev.mayaqq.spectrumJetpacks.utils.JetpackPlayerExtension;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;

import java.util.HashMap;

import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.CONFIG;
import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.id;
import static dev.mayaqq.spectrumJetpacks.registry.ServerEventRegistry.tick;

public class C2SPackets {

    public static HashMap<PlayerEntity, Boolean> propellingMap = new HashMap<>();

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(id("propel"), (server, player, handler, buf, responseSender) -> {
            int working = buf.readInt();
            server.execute(() -> {
                //particles, sounds, and energy drain
                propellingMap.put(player, true);
                if (tick) {
                    switch (working) {
                        case 0 -> InkPowered.tryDrainEnergy(player, InkColor.of(DyeColor.YELLOW), CONFIG.inkUsagePerTick);
                        case 1 -> InkPowered.tryDrainEnergy(player, InkColor.of(DyeColor.YELLOW), CONFIG.inkUsagePerTickGoingUp);
                        case 2 -> InkPowered.tryDrainEnergy(player, InkColor.of(DyeColor.YELLOW), CONFIG.inkUsagePerTickForward);
                    }
                }
                player.forwardSpeed = 10f;
                ((JetpackPlayerExtension) player).setHasRecentlyUsedJetPack(true);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(id("stoppropel"), (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                propellingMap.put(player, false);
            });
        });
    }
}
