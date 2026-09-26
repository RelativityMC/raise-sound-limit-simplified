package com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_2;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager1_18 {

    @Shadow
    public abstract void reloadSounds();

    @Shadow
    @Final
    private SoundSystem soundSystem;

    @Inject(method = "reloadSounds", at = @At("HEAD"), cancellable = true)
    private void wrapReloadSounds(CallbackInfo ci) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::reloadSounds);
        }
    }

}
