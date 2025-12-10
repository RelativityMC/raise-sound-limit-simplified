package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundExecutor;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager implements SoundManagerDuck {

    @Shadow @Final private SoundSystem soundSystem;

    @Mutable
    @Shadow @Final private Map<?, WeightedSoundSet> sounds;

    @Shadow public abstract void playNextTick(TickableSoundInstance sound);

    @Shadow public abstract void play(SoundInstance sound, int delay);

    @Shadow public abstract void close();

    @Shadow public abstract void tick(boolean paused);

    @Shadow public abstract void resumeAll();

    @Shadow public abstract void registerListener(SoundInstanceListener listener);

    @Shadow public abstract void unregisterListener(SoundInstanceListener listener);

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

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, int delay, CallbackInfo ci) {
        if (rsls$shouldRunOffthread()) {
            ci.cancel();
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> play(sound, delay));
        }
    }

    // updateListenerPosition not needed

    // stopAll not needed

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

    @WrapMethod(method = {"stop(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/sounds/SoundSource;)V", "stopSounds"})
    private void onStopSounds(@Coerce Object id, SoundCategory soundCategory, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(id, soundCategory));
        } else {
            original.call(id, soundCategory);
        }
    }

    @Unique
    public boolean rsls$shouldRunOffthread() {
        final SoundExecutor executor = ((ISoundSystem) this.soundSystem).getTaskQueue();
        final Thread thread = ((ISoundExecutor) executor).getThread();
        return Thread.currentThread() != thread;
    }

}
