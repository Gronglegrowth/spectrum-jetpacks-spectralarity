package dev.mayaqq.spectrumJetpacks.items;

import de.dafuqs.spectrum.api.energy.InkPowered;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;
import de.dafuqs.spectrum.items.trinkets.SpectrumTrinketItem;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import dev.emi.trinkets.api.SlotReference;
import dev.mayaqq.spectrumJetpacks.utils.EquipUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.CONFIG;
import static dev.mayaqq.spectrumJetpacks.SpectrumJetpacks.id;
import static dev.mayaqq.spectrumJetpacks.client.registry.JetpackKeybinds.hoverKey;
import static dev.mayaqq.spectrumJetpacks.client.registry.JetpackKeybinds.toggleKey;

public class JetpackItem extends SpectrumTrinketItem implements InkStorageItem<SingleInkStorage>, InkPowered {

    public static ItemStack equippedJetpack = null;

    public final InkColor inkColor;
    public final long maxInk;
    public final float maxVerticalVelocity;
    public final float maxHorizontalVelocity;
    public final float horizontalSpeed;
    public final float verticalSpeed;

    public JetpackItem(Settings settings, Identifier identifier ,InkColor inkColor, long maxInk, float horizontalSpeed, float verticalSpeed, float maxHorizontalVelocity, float maxVerticalVelocity) {
        super(settings.maxCount(1), identifier);
        this.inkColor = inkColor;
        this.maxInk = maxInk;
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
        this.maxVerticalVelocity = maxVerticalVelocity;
        this.maxHorizontalVelocity = maxHorizontalVelocity;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            if (entity.getWorld().isClient) {

                SingleInkStorage inkStorage = EquipUtils.getEnergyStorage(stack);
                long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());

                PacketByteBuf buf = PacketByteBufs.create();
                Vec3d v = player.getVelocity();
                MinecraftClient mc = MinecraftClient.getInstance();

                if (toggleKey.isPressed()) {
                    // vertical motion
                    if (player.isOnGround() || (storedInk <= 0 && !player.isCreative())) {
                        return;
                    } else if (mc.options.jumpKey.isPressed()) {
                        v = new Vec3d(v.x, Math.min(maxVerticalVelocity, v.y + verticalSpeed), v.z);
                        buf.writeInt(0);
                        ClientPlayNetworking.send(id("propel"), buf);
                    } else if (player.isSneaking()) {
                        v = new Vec3d(v.x, Math.max(-maxVerticalVelocity, v.y - verticalSpeed), v.z);
                        buf.writeInt(0);
                        ClientPlayNetworking.send(id("propel"), buf);
                    } else if (hoverKey.isPressed()) {
                        v = new Vec3d(v.x, Math.max(-0.01f, v.y), v.z);
                        buf.writeInt(1);
                        ClientPlayNetworking.send(id("propel"), buf);
                    }
                    // horizontal motion
                    if (player.forwardSpeed > 0.0F && player.isSprinting()) {
                        float yaw = player.getYaw(1.0f);
                        float pitch = player.getPitch(1.0f);
                        double xDir = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                        double zDir = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                        Vec3d direction = new Vec3d(xDir, v.y, zDir).normalize();

                        double speedMultiplier = horizontalSpeed;
                        Vec3d boost = direction.multiply(speedMultiplier, 0.0, speedMultiplier);
                        Vec3d newVelocity = player.getVelocity().add(boost);
                        double maxSpeed = Math.max(maxHorizontalVelocity, newVelocity.length());
                        double keepY = v.y;
                        v = new Vec3d(newVelocity.x, keepY, newVelocity.z).normalize().multiply(maxSpeed);
                        v = new Vec3d(v.x, keepY, v.z);
                        PacketByteBuf sprintBuf = PacketByteBufs.create();
                        sprintBuf.writeInt(2);
                        ClientPlayNetworking.send(id("propel"), sprintBuf);
                    }
                    sound(player);
                    double xOffset = 0.2; // adjust this value as needed
                    double yOffset = 0.8; // adjust this value as needed
                    double zOffset = 0.2; // adjust this value as needed

                    double playerX = player.getX();
                    double playerY = player.getY();
                    double playerZ = player.getZ();

                    float yaw = player.bodyYaw;

                    double cosYaw = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
                    double sinYaw = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);

                    double centerX = playerX + xOffset * sinYaw;
                    double centerY = playerY + yOffset;
                    double centerZ = playerZ + xOffset * cosYaw;

                    double leftX = centerX - xOffset * cosYaw;
                    double leftZ = centerZ + xOffset * sinYaw;

                    double rightX = centerX + xOffset * cosYaw;
                    double rightZ = centerZ - xOffset * sinYaw;

                    // Delta y -0.1, speed 0.5
                    player.getWorld().addParticle(SpectrumParticleTypes.SHOOTING_STAR, leftX, centerY, leftZ, 0, -0.5, 0);
                    player.getWorld().addParticle(SpectrumParticleTypes.SHOOTING_STAR, rightX, centerY, rightZ, 0, -0.5, 0);
                    player.setVelocity(v);
                } else if (MinecraftClient.getInstance().player != null){
                    ClientPlayNetworking.send(id("stoppropel"), PacketByteBufs.empty());
                }
            }
        }
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        equippedJetpack = stack;
    }

    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        equippedJetpack = null;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        SingleInkStorage inkStorage = getEnergyStorage(stack);
        long storedInk = inkStorage.getEnergy(inkStorage.getStoredColor());
        tooltip.add(Text.literal("Stored Purple Ink: " + storedInk).formatted(Formatting.GRAY));
        tooltip.add(Text.of(" "));
        tooltip.add(Text.translatable("item.spectrumjetpacks.jetpack.desc.toggle", toggleKey.getBoundKeyLocalizedText().getString().toUpperCase()).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.spectrumjetpacks.jetpack.desc.hover", hoverKey.getBoundKeyLocalizedText().getString().toUpperCase()).formatted(Formatting.GRAY));
        tooltip.add(Text.of(" "));
    }

    @Override
    public SingleInkStorage getEnergyStorage(ItemStack itemStack) {
        NbtCompound compound = itemStack.getNbt();
        if (compound != null && compound.contains("EnergyStore")) {
            return SingleInkStorage.fromNbt(compound.getCompound("EnergyStore"));
        }
        return new SingleInkStorage(maxInk, inkColor, 0);
    }

    @Override
    public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
        if (storage instanceof SingleInkStorage singleInkStorage) {
            NbtCompound compound = itemStack.getOrCreateNbt();
            compound.put("EnergyStore", singleInkStorage.toNbt());
        }
    }

    @Override
    public Drainability getDrainability() {
        return Drainability.NEVER;
    }

    @Override
    public List<InkColor> getUsedColors() {
        return List.of(InkColor.ofDyeColor(DyeColor.PURPLE));
    }

    private static void sound(PlayerEntity player) {
        if (CONFIG.soundsEnabled) {
            player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.2f, 1.0f);
        }
    }
}