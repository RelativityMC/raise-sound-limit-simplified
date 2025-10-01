package com.ishland.fabric.rsls.mixin.versions;

import com.ishland.fabric.rsls.common.SoundSystemDuck;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem1_21_6 implements SoundSystemDuck {

    @Shadow public abstract SoundSystem.PlayResult play(SoundInstance sound);

    @Override
    public void rsls$playInternal0(SoundInstance instance) {
        this.play(instance);
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;"))
    private SoundSystem.PlayResult redirectDelayedPlay(SoundSystem instance, SoundInstance sound) {
        this.rsls$schedulePlay(sound);
        return SoundSystem.PlayResult.STARTED; // return value is unused
    }

}
