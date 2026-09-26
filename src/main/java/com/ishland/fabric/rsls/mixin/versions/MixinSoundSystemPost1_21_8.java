package com.ishland.fabric.rsls.mixin.versions;

import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public class MixinSoundSystemPost1_21_8 {

    @Redirect(method = "stopAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundExecutor;restart()V"))
    private void redirectExecutorStart(SoundExecutor instance) {
        // no-op
    }

}
