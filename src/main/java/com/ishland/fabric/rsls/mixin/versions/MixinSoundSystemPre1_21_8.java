package com.ishland.fabric.rsls.mixin.versions;

import com.ishland.fabric.rsls.mixin.access.IThreadExecutor;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public class MixinSoundSystemPre1_21_8 {

    @Shadow
    @Final
    private SoundExecutor taskQueue;

    // named "restart" in older versions
    @Dynamic
    @Redirect(method = "stopAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundEngineExecutor;flush()V", remap = false))
    private void redirectExecutorStop(SoundExecutor instance) {
        ((IThreadExecutor<Runnable>) this.taskQueue).invokeCancelTasks();
    }

}
