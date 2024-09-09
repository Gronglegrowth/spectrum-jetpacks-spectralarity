package dev.mayaqq.spectrumJetpacks.mixin;

import dev.mayaqq.spectrumJetpacks.items.JetpackItem;
import dev.mayaqq.spectrumJetpacks.utils.EquipUtils;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow private boolean floating;

    @Inject(method = "onPlayerMove", at = @At("TAIL"))
    private void onPlayerMove(CallbackInfo ci) {
        if (JetpackItem.equippedJetpack != null) {
            this.floating = false;
        }
    }
}