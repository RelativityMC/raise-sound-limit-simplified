package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubtitlesHud.class)
public abstract class MixinSubtitlesHud_1_20_2 {

    @Shadow @Final private MinecraftClient client;

    @Dynamic
    @Shadow(aliases = "method_4884") public abstract void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet);

    @Dynamic
    @Inject(method = "method_4884", at = @At("HEAD"), cancellable = true)
    private void onSoundPlayedHandler(SoundInstance sound, WeightedSoundSet soundSet, CallbackInfo ci) {
        if (!this.client.isOnThread()) {
            ci.cancel();
            this.client.execute(() -> this.onSoundPlayed(sound, soundSet));
        }
    }

}
