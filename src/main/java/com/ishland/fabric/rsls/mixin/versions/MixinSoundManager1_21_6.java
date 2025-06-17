package com.ishland.fabric.rsls.mixin.versions;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager1_21_6 {

    @Shadow @Final
    private SoundSystem soundSystem;

    @Shadow public abstract void pauseAllExcept(SoundCategory... categories);

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
//            if (!this.soundSystem.started) {
//                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
//                return;
//            } else
            if (!sound.canPlay()) {
                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
                return;
            }
            // soundset check skipped
            Sound sound2 = sound.getSound();
            if (sound2 == SoundManager.INTENTIONALLY_EMPTY_SOUND || sound2 == SoundManager.MISSING_SOUND) {
                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
                return;
            }

            cir.setReturnValue(SoundSystem.PlayResult.STARTED); // we play sound asynchronously, STARTED is the only choice for now
            ((SoundSystemDuck) this.soundSystem).rsls$schedulePlay(sound);
            return;
        }
    }

    @Inject(method = "pauseAllExcept", at = @At("HEAD"), cancellable = true)
    private void onPauseAll(SoundCategory[] categories, CallbackInfo ci) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.pauseAllExcept(categories));
        }
    }

}
