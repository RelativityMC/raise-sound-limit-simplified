package com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_0;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager1_21_5 {

    @Shadow(aliases = "method_4879")
    public abstract void pauseAll();

    @Shadow @Final private SoundSystem soundSystem;

    @Dynamic
    @Inject(method = "method_4873(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ci.cancel();
            ((SoundSystemDuck) this.soundSystem).rsls$schedulePlay(sound);
        }
    }

    @Dynamic
    @Inject(method = "method_4879", at = @At("HEAD"), cancellable = true)
    private void onPauseAll(CallbackInfo ci) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::pauseAll);
        }
    }

}
