package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {

    @Mutable
    @Shadow @Final private Map<SoundInstance, Integer> soundEndTicks;

    @Mutable
    @Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.soundEndTicks = Collections.synchronizedMap(this.soundEndTicks);
        this.sources = Collections.synchronizedMap(this.sources);
    }

    @Redirect(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Channel;tick()V"))
    private void dontTickChannel(Channel instance) {
        // no-op
    }

}
