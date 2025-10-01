package com.ishland.fabric.rsls.mixin.versions;

import com.ishland.fabric.rsls.common.SoundSystemDuck;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem1_21_5 implements SoundSystemDuck {

    @Dynamic
    @Shadow
    public abstract void play(SoundInstance sound);

    @Override
    public void rsls$playInternal0(SoundInstance instance) {
        this.play(instance);
    }

    @Dynamic
    @Redirect(method = "tickNonPaused()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;play(Lnet/minecraft/client/sound/SoundInstance;)V"))
    private void redirectDelayedPlay(SoundSystem instance, SoundInstance sound) {
        this.rsls$schedulePlay(sound);
    }

}
