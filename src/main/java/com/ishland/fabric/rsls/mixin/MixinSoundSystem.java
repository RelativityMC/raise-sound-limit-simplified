package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.HashSetList;
import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(SoundSystem.class)
public abstract class MixinSoundSystem implements SoundSystemDuck {

    @Mutable
    @Shadow @Final private Map<SoundInstance, Integer> soundEndTicks;

    @Mutable
    @Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;

    @Shadow @Final private List<TickableSoundInstance> soundsToPlayNextTick;

    @Mutable
    @Shadow @Final private List<TickableSoundInstance> tickingSounds;

    @Shadow public abstract void play(SoundInstance sound, int delay);

    @Shadow @Final private SoundExecutor taskQueue;

    @Shadow private boolean started;

    @Unique
    private AtomicLong rsls$droppedSoundsPerf;

    @Unique
    private Set<SoundInstance> rsls$pendingSounds;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onInit(CallbackInfo ci) {
        this.soundEndTicks = Collections.synchronizedMap(this.soundEndTicks);
        this.sources = Collections.synchronizedMap(this.sources);
        this.tickingSounds = new HashSetList<>(this.tickingSounds);
        this.rsls$droppedSoundsPerf = new AtomicLong();
        this.rsls$pendingSounds = Collections.synchronizedSet(new HashSet<>());
    }

    @Redirect(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/Channel;tick()V"))
    private void dontTickChannel(Channel instance) {
        // no-op
    }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Ljava/util/List;stream()Ljava/util/stream/Stream;"))
    private Stream<?> optimizeNextTickIteration(List<?> instance) {
        if (instance == this.soundsToPlayNextTick) {
            for (TickableSoundInstance soundInstance : this.soundsToPlayNextTick) {
                this.rsls$schedulePlay(soundInstance); // canPlay check done in play()
            }

            return null;
        }
        return instance.stream();
    }

    @Override
    public void rsls$schedulePlay(SoundInstance instance) {
        long scheduleTime = System.nanoTime();
        this.rsls$pendingSounds.add(instance);
        this.taskQueue.execute(() -> {
            if (!this.started) {
                return;
            }
            this.rsls$pendingSounds.remove(instance);
            if (System.nanoTime() - scheduleTime < 1_000_000_000L) { // 1 second
                this.rsls$playInternal0(instance);
            } else {
                this.rsls$droppedSoundsPerf.incrementAndGet();
            }
        });
    }

    @Inject(method = "reloadSounds", at = @At("RETURN"))
    private void onReload(CallbackInfo ci) {
        this.rsls$droppedSoundsPerf.set(0);
    }

    @ModifyReturnValue(method = "getDebugString", at = @At("RETURN"))
    private String appendDebugString(String original) {
        final long dropped = this.rsls$droppedSoundsPerf.get();
        if (dropped != 0) {
            return original + String.format(" (%d dropped)", dropped);
        } else {
            return original;
        }
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

    @ModifyReturnValue(method = "isPlaying", at = @At("RETURN"))
    private boolean modifyIsPlaying(boolean original, SoundInstance sound) {
        return original || this.rsls$pendingSounds.contains(sound);
    }

}
