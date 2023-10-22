package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundExecutor;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {

    @Shadow @Final private SoundSystem soundSystem;

    @Shadow protected abstract void apply(SoundManager.SoundList soundList, ResourceManager resourceManager, Profiler profiler);

    @Mutable
    @Shadow @Final private Map<Identifier, WeightedSoundSet> sounds;

    @Shadow public abstract void playNextTick(TickableSoundInstance sound);

    @Shadow public abstract void play(SoundInstance sound);

    @Shadow public abstract void play(SoundInstance sound, int delay);

    @Shadow public abstract void pauseAll();

    @Shadow public abstract void stopAll();

    @Shadow public abstract void close();

    @Shadow public abstract void tick(boolean paused);

    @Shadow public abstract void resumeAll();

    @Shadow public abstract void updateSoundVolume(SoundCategory category, float volume);

    @Shadow public abstract void registerListener(SoundInstanceListener listener);

    @Shadow public abstract void unregisterListener(SoundInstanceListener listener);

    @Shadow public abstract void stopSounds(@Nullable Identifier id, @Nullable SoundCategory soundCategory);

    @Shadow public abstract void reloadSounds();

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onInit(CallbackInfo ci) {
        this.sounds = Collections.synchronizedMap(this.sounds);
    }

    @Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
    private void onPlayNextTick(TickableSoundInstance sound, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> playNextTick(sound));
        }
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
//            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> play(sound));
            ((SoundSystemDuck) this.soundSystem).rsls$schedulePlay(sound);
        }
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, int delay, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> play(sound, delay));
        }
    }

    // updateListenerPosition not needed

    @Inject(method = "pauseAll", at = @At("HEAD"), cancellable = true)
    private void onPauseAll(CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::pauseAll);
        }
    }

    @Inject(method = "stopAll", at = @At("HEAD"), cancellable = true)
    private void onStopAll(CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::stopAll);
        }
    }

    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    private void onClose(CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::close);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(boolean paused, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.tick(paused));
        }
    }

    @Inject(method = "resumeAll", at = @At("HEAD"), cancellable = true)
    private void onResumeAll(CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(this::resumeAll);
        }
    }

    @Inject(method = "updateSoundVolume", at = @At("HEAD"), cancellable = true)
    private void onUpdateSoundVolume(SoundCategory category, float volume, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.updateSoundVolume(category, volume));
        }
    }

    // isPlaying not needed

    @Inject(method = "registerListener", at = @At("HEAD"), cancellable = true)
    private void onRegisterListener(SoundInstanceListener listener, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.registerListener(listener));
        }
    }

    @Inject(method = "unregisterListener", at = @At("HEAD"), cancellable = true)
    private void onUnregisterListener(SoundInstanceListener listener, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.unregisterListener(listener));
        }
    }

    @Inject(method = "stopSounds", at = @At("HEAD"), cancellable = true)
    private void onStopSounds(Identifier id, SoundCategory soundCategory, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.stopSounds(id, soundCategory));
        }
    }

    @Unique
    private boolean rsls$shouldRunOffthread() {
        final SoundExecutor executor = ((ISoundSystem) this.soundSystem).getTaskQueue();
        final Thread thread = ((ISoundExecutor) executor).getThread();
        return Thread.currentThread() != thread;
    }

}
