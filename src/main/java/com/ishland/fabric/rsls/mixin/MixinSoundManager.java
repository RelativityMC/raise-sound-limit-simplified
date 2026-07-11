package com.ishland.fabric.rsls.mixin;

import com.google.common.collect.Sets;
import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundExecutor;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager implements SoundManagerDuck {

    @Unique
    private static final Set<Identifier> rsls$unknownSounds = Sets.newConcurrentHashSet();

    @Shadow @Final private SoundSystem soundSystem;

    @Mutable
    @Shadow @Final private Map<?, WeightedSoundSet> sounds;

    @Shadow public abstract void tick(boolean paused);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void onInit(CallbackInfo ci) {
        this.sounds = Collections.synchronizedMap(this.sounds);
    }

    @WrapMethod(method = "playNextTick")
    private void onPlayNextTick(TickableSoundInstance sound, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(sound));
        } else {
            original.call(sound);
        }
    }

    @WrapMethod(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V")
    private void onPlay(SoundInstance sound, int delay, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(sound, delay));
        } else {
            original.call(sound, delay);
        }
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfoReturnable<SoundSystem.PlayResult> cir) {
        if (this.rsls$shouldRunOffthread()) {
//            if (!this.soundSystem.started) {
//                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
//                return;
//            } else
            if (!sound.canPlay()) {
                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
                return;
            }

            WeightedSoundSet soundSet = sound.getSoundSet((SoundManager) (Object) this);
            Identifier identifier = sound.getId();
            if (soundSet == null) {
                if (rsls$unknownSounds.add(identifier)) {
                    LOGGER.warn("Unable to play unknown soundEvent:  {}", identifier);
                }
                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
                return;
            }

            Sound sound2 = sound.getSound();
            if (sound2 == SoundManager.INTENTIONALLY_EMPTY_SOUND || sound2 == SoundManager.MISSING_SOUND) {
                cir.setReturnValue(SoundSystem.PlayResult.NOT_STARTED);
                return;
            }

            cir.setReturnValue(SoundSystem.PlayResult.STARTED); // we play sound asynchronously, STARTED is the only choice for now
            ((SoundSystemDuck) this.soundSystem).rsls$schedulePlay(sound);
            return;
        }
    }

    // updateListenerPosition not needed

    @WrapMethod(method = "pauseAllExcept")
    private void onPauseAll(SoundCategory[] categories, Operation<Void> original) {
        if (((SoundManagerDuck) this).rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call((Object) categories));
        } else {
            original.call((Object) categories);
        }
    }

    // stopAll not needed

    @WrapMethod(method = "close")
    private void onClose(Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(original::call);
        } else {
            original.call();
        }
    }

    // stopAbruptly debatable

    @WrapMethod(method = "tick")
    private void onTick(boolean paused, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> this.tick(paused));
        } else {
            original.call(paused);
        }
    }

    @WrapMethod(method = "resumeAll")
    private void onResumeAll(Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(original::call);
        } else {
            original.call();
        }
    }

    @WrapMethod(method = "refreshSoundVolumes")
    private void onUpdateSoundVolume(SoundCategory category, Operation<Void> original) {
        if (this.rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(category));
        } else {
            original.call(category);
        }
    }

    @WrapMethod(method = "stop")
    private void onStop(SoundInstance sound, Operation<Void> original) {
        if (this.rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(sound));
        } else {
            original.call(sound);
        }
    }

    @WrapMethod(method = "setVolume")
    private void onSetVolume(SoundCategory category, float volume, Operation<Void> original) {
        if (this.rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(category, volume));
        } else {
            original.call(category, volume);
        }
    }

    // isPlaying not needed

    @WrapMethod(method = "registerListener")
    private void onRegisterListener(SoundInstanceListener listener, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(listener));
        } else {
            original.call(listener);
        }
    }

    @WrapMethod(method = "unregisterListener")
    private void onUnregisterListener(SoundInstanceListener listener, Operation<Void> original) {
        if (rsls$shouldRunOffthread()) {
            ((ISoundSystem) this.soundSystem).getTaskQueue().execute(() -> original.call(listener));
        } else {
            original.call(listener);
        }
    }

    @WrapMethod(method = "stopSounds")
    private void onStopSounds(Identifier id, SoundCategory soundCategory, Operation<Void> original) {
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
