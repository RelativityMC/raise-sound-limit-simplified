package com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_1;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
public abstract class MixinSoundManager1_21_9 {

    @Shadow
    @Final
    private SoundSystem soundSystem;

    @Dynamic
    @WrapMethod(method = {"method_4865(Lnet/minecraft/class_3419;)V", "refreshSoundVolumes"})
    private void onUpdateSoundVolume(SoundCategory category, Operation<Void> original) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(category));
        }
    }

}
