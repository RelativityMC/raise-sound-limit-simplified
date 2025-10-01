package com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_1;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager1_21_8 {

    @Dynamic
    @Shadow
    public abstract void updateSourceVolume(SoundCategory category, float volume); // updateSoundVolume

    @Shadow
    @Final
    private SoundSystem soundSystem;

    @Dynamic
    @Inject(method = "updateSourceVolume(Lnet/minecraft/sounds/SoundSource;F)V", at = @At("HEAD"), cancellable = true)
    private void onUpdateSoundVolume(SoundCategory category, float volume, CallbackInfo ci) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.updateSourceVolume(category, volume));
        }
    }

}
