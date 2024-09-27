package dev.mayaqq.spectrumJetpacks.blockEntites;

import de.dafuqs.spectrum.SpectrumCommon;
import de.dafuqs.spectrum.energy.InkStorage;
import de.dafuqs.spectrum.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.energy.InkStorageItem;
import de.dafuqs.spectrum.energy.color.InkColor;
import de.dafuqs.spectrum.energy.storage.TotalCappedInkStorage;
import de.dafuqs.spectrum.interfaces.PlayerOwned;
import de.dafuqs.spectrum.inventories.ColorPickerScreenHandler;
import de.dafuqs.spectrum.networking.SpectrumS2CPacketSender;
import de.dafuqs.spectrum.particle.SpectrumParticleTypes;
import de.dafuqs.spectrum.progression.SpectrumAdvancementCriteria;
import de.dafuqs.spectrum.recipe.SpectrumRecipeTypes;
import de.dafuqs.spectrum.recipe.ink_converting.InkConvertingRecipe;
import de.dafuqs.spectrum.registries.SpectrumSoundEvents;
import dev.mayaqq.spectrumJetpacks.registry.BlockEntityRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class InkChargerBlockEntity extends LootableContainerBlockEntity implements ExtendedScreenHandlerFactory, PlayerOwned, InkStorageBlockEntity<TotalCappedInkStorage> {
    public static final int INVENTORY_SIZE = 1;
    public static final int INPUT_SLOT_ID = 0;
    public static final long TICKS_PER_CONVERSION = 5L;
    public static final long STORAGE_AMOUNT = 26214400L;
    public DefaultedList<ItemStack> inventory;
    protected TotalCappedInkStorage inkStorage;
    protected boolean paused;
    protected boolean inkDirty;
    protected @Nullable InkConvertingRecipe cachedRecipe;
    protected @Nullable InkColor selectedColor;
    private UUID ownerUUID;

    public InkChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.INK_CHARGER_ENTITY, blockPos, blockState);
    }

    public static void tick(World world, BlockPos pos, BlockState state, InkChargerBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.inkDirty = false;
            if (!blockEntity.paused) {
                boolean convertedPigment = false;
                boolean shouldPause = true;
                if (world.getTime() % 5L == 0L) {
                    convertedPigment = blockEntity.tryConvertPigmentToEnergy((ServerWorld)world);
                } else {
                    shouldPause = false;
                }

                boolean filledContainer = blockEntity.tryFillInkContainer();
                if (!convertedPigment && !filledContainer) {
                    if (shouldPause) {
                        blockEntity.paused = true;
                    }
                } else {
                    blockEntity.updateInClientWorld();
                    blockEntity.setInkDirty();
                    blockEntity.markDirty();
                }
            }
        }
    }

    public void setSelectedColor(InkColor inkColor) {
        this.selectedColor = inkColor;
        this.paused = false;
        this.markDirty();
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }

        if (nbt.contains("InkStorage", 10)) {
            this.inkStorage = TotalCappedInkStorage.fromNbt(nbt.getCompound("InkStorage"));
        }

        if (nbt.contains("OwnerUUID")) {
            this.ownerUUID = nbt.getUuid("OwnerUUID");
        } else {
            this.ownerUUID = null;
        }

        if (nbt.contains("SelectedColor", 8)) {
            this.selectedColor = InkColor.of(nbt.getString("SelectedColor"));
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }

        nbt.put("InkStorage", this.inkStorage.toNbt());
        if (this.ownerUUID != null) {
            nbt.putUuid("OwnerUUID", this.ownerUUID);
        }

        if (this.selectedColor != null) {
            nbt.putString("SelectedColor", this.selectedColor.toString());
        }

    }

    protected Text getContainerName() {
        return Text.translatable("block.spectrumjetpacks.ink_charger");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ColorPickerScreenHandler(syncId, playerInventory, this.pos, this.selectedColor);
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwner(PlayerEntity playerEntity) {
        this.ownerUUID = playerEntity.getUuid();
    }

    public TotalCappedInkStorage getEnergyStorage() {
        return this.inkStorage;
    }

    public void setInkDirty() {
        this.inkDirty = true;
    }

    public boolean getInkDirty() {
        return this.inkDirty;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
        this.paused = false;
        this.updateInClientWorld();
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = super.removeStack(slot, amount);
        this.paused = false;
        this.updateInClientWorld();
        return itemStack;
    }

    public ItemStack removeStack(int slot) {
        ItemStack itemStack = super.removeStack(slot);
        this.paused = false;
        this.updateInClientWorld();
        return itemStack;
    }

    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        this.paused = false;
        this.updateInClientWorld();
    }

    public int size() {
        return INVENTORY_SIZE;
    }

    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        if (this.selectedColor == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeString(this.selectedColor.toString());
        }

    }

    protected boolean tryConvertPigmentToEnergy(ServerWorld world) {
        InkConvertingRecipe recipe = this.getInkConvertingRecipe(world);
        if (recipe != null) {
            InkColor color = recipe.getInkColor();
            long amount = recipe.getInkAmount();
            if (this.inkStorage.getEnergy(color) + amount <= this.inkStorage.getMaxPerColor()) {
                (this.inventory.get(0)).decrement(1);
                this.inkStorage.addEnergy(color, amount);
                if (SpectrumCommon.CONFIG.BlockSoundVolume > 0.0F) {
                    world.playSound(null, this.pos, SpectrumSoundEvents.ENCHANTER_DING, SoundCategory.BLOCKS, SpectrumCommon.CONFIG.BlockSoundVolume / 2.0F, 1.0F);
                }

                SpectrumS2CPacketSender.playParticleWithRandomOffsetAndVelocity(world, new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.7, (double)this.pos.getZ() + 0.5), SpectrumParticleTypes.getFluidRisingParticle(color.getDyeColor()), 5, new Vec3d(0.22, 0.0, 0.22), new Vec3d(0.0, 0.1, 0.0));
                return true;
            }
        }

        return false;
    }

    protected @Nullable InkConvertingRecipe getInkConvertingRecipe(World world) {
        ItemStack inputStack = this.inventory.get(0);
        if (inputStack.isEmpty()) {
            this.cachedRecipe = null;
            return null;
        } else if (this.cachedRecipe != null && (this.cachedRecipe.getIngredients().get(0)).test(inputStack)) {
            return this.cachedRecipe;
        } else {
            Optional<InkConvertingRecipe> recipe = world.getRecipeManager().getFirstMatch(SpectrumRecipeTypes.INK_CONVERTING, this, world);
            if (recipe.isPresent()) {
                this.cachedRecipe = recipe.get();
                return this.cachedRecipe;
            } else {
                this.cachedRecipe = null;
                return null;
            }
        }
    }

    protected boolean tryFillInkContainer() {
        long transferredAmount = 0L;
        ItemStack stack = this.inventory.get(1);
        Item var5 = stack.getItem();
        if (var5 instanceof InkStorageItem inkStorageItem) {
            InkStorage itemStorage = inkStorageItem.getEnergyStorage(stack);
            ServerPlayerEntity owner;
            if (this.selectedColor == null) {
                boolean searchedOwner = false;
                owner = null;

                long amount;
                for(Iterator<InkColor> var8 = InkColor.all().iterator(); var8.hasNext(); transferredAmount += amount) {
                    InkColor color = var8.next();
                    amount = InkStorage.transferInk(this.inkStorage, itemStorage, color);
                    if (amount > 0L) {
                        if (!searchedOwner) {
                            owner = (ServerPlayerEntity)this.getOwnerIfOnline();
                        }

                        if (owner != null) {
                            SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(owner, stack, itemStorage, color, amount);
                        }
                    }
                }
            } else {
                transferredAmount = InkStorage.transferInk(this.inkStorage, itemStorage, this.selectedColor);
                if (transferredAmount > 0L) {
                    owner = (ServerPlayerEntity) this.getOwnerIfOnline();
                    if (owner != null) {
                        SpectrumAdvancementCriteria.INK_CONTAINER_INTERACTION.trigger(owner, stack, itemStorage, this.selectedColor, transferredAmount);
                    }
                }
            }

            if (transferredAmount > 0L) {
                inkStorageItem.setEnergyStorage(stack, itemStorage);
            }
        }

        return transferredAmount > 0L;
    }

    public @Nullable InkColor getSelectedColor() {
        return this.selectedColor;
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.writeNbt(nbtCompound);
        return nbtCompound;
    }

    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void updateInClientWorld() {
        this.world.updateListeners(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 4);
    }
}