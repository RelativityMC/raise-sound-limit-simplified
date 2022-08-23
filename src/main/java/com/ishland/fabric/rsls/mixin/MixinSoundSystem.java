package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem {

    @Mutable
    @Shadow @Final private Map<SoundInstance, Integer> soundEndTicks;

    @Mutable
    @Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

    @Shadow @Final private List<TickableSoundInstance> soundsToPlayNextTick;

    @Shadow public abstract void play(SoundInstance sound);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.soundEndTicks = Collections.synchronizedMap(this.soundEndTicks);
        this.sources = Collections.synchronizedMap(this.sources);
    }

    @Redirect(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Channel;tick()V"))
    private void dontTickChannel(Channel instance) {
        // no-op
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Ljava/util/List;stream()Ljava/util/stream/Stream;"))
    private Stream<?> optimizeNextTickIteration(List<?> instance) {
        if (instance == this.soundsToPlayNextTick) {
            for (TickableSoundInstance soundInstance : this.soundsToPlayNextTick) {
                this.play(soundInstance); // canPlay check done in play()
            }

            return null;
        }
        return instance.stream();
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;"))
    private <T> Stream<T> tickDisableFilter(Stream<T> instance, Predicate<? super T> predicate) {
        if (instance == null) return null;
        return instance.filter(predicate);
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private <T> void tickDisableForEach(Stream<T> instance, Consumer<? super T> consumer) {
        if (instance == null) return;
        instance.forEach(consumer);
    }

}
