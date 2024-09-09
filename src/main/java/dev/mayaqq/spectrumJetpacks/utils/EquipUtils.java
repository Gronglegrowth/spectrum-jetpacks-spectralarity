package dev.mayaqq.spectrumJetpacks.utils;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.FixedSingleInkStorage;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.mayaqq.spectrumJetpacks.items.JetpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;

import java.util.Optional;

import static dev.mayaqq.spectrumJetpacks.registry.JetpackItems.BEDROCK_JETPACK;
import static dev.mayaqq.spectrumJetpacks.registry.JetpackItems.GEMSTONE_JETPACK;

public class EquipUtils {
    public static SingleInkStorage getEnergyStorage(ItemStack stack) {
        return ((JetpackItem) stack.getItem()).getEnergyStorage(stack);
    }
    public static long getEnergy(ItemStack stack) {
        try {
            InkStorage inkStorage = ((JetpackItem) stack.getItem()).getEnergyStorage(stack);
            return inkStorage.getEnergy(InkColor.ofDyeColor(DyeColor.PURPLE));
        } catch (Exception e) {
            return 0;
        }
    }
}