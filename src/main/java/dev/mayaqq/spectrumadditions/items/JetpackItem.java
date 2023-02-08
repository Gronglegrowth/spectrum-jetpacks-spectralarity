package dev.mayaqq.spectrumadditions.items;

import de.dafuqs.spectrum.energy.InkStorageItem;
import de.dafuqs.spectrum.energy.color.InkColor;
import de.dafuqs.spectrum.energy.storage.FixedSingleInkStorage;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import dev.emi.trinkets.api.SlotReference;
import dev.mayaqq.spectrumadditions.utils.PlayerExtensionsForTheJetPackMod;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.mayaqq.spectrumadditions.SpectrumAdditionsClient.hoverKey;
import static dev.mayaqq.spectrumadditions.SpectrumAdditionsClient.toggleKey;

public class JetpackItem extends SpectrumTrinketItem implements InkStorageItem<FixedSingleInkStorage> {

    public final InkColor inkColor;
    public final long maxInk;

    public JetpackItem(Settings settings, Identifier identifier ,InkColor inkColor, long maxInk) {
        super(settings.maxCount(1), identifier);
        this.inkColor = inkColor;
        this.maxInk = maxInk;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        FixedSingleInkStorage inkStorage = getEnergyStorage(stack);
        long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());
        tooltip.add(Text.literal("Stored Ink: " + storedInk).formatted(Formatting.GRAY));
        tooltip.add(Text.of(" "));
        tooltip.add(Text.translatable("item.spectrumadditions.jetpack.desc.toggle", toggleKey.getBoundKeyLocalizedText().getString().toUpperCase()).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.spectrumadditions.jetpack.desc.hover", hoverKey.getBoundKeyLocalizedText().getString().toUpperCase()).formatted(Formatting.GRAY));
        tooltip.add(Text.of(" "));
    }
    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        return true;
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        ((PlayerExtensionsForTheJetPackMod) entity).setHasRecentlyUsedJetPack(false);
    }

    @Override
    public FixedSingleInkStorage getEnergyStorage(ItemStack itemStack) {
        NbtCompound compound = itemStack.getNbt();
        return compound != null && compound.contains("EnergyStore") ? FixedSingleInkStorage.fromNbt(compound.getCompound("EnergyStore")) : new FixedSingleInkStorage(this.maxInk, this.inkColor);
    }

    @Override
    public void setEnergyStorage(ItemStack itemStack, FixedSingleInkStorage storage) {
        NbtCompound compound = itemStack.getOrCreateNbt();
        compound.put("EnergyStore", storage.toNbt());
    }

    @Override
    public Drainability getDrainability() {
        return Drainability.PLAYER_ONLY;
    }
}