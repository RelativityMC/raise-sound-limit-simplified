package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.mixin.access.ISoundManager;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(MusicTracker.class)
public class MixinMusicTracker {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private CompletableFuture<Void> rsls$playFuture;

    @WrapMethod(method = {"play"})
    private void wrapPlay(@Coerce Object instance, Operation<Void> original) {
        CompletableFuture<Void> rsls$playFuture1 = this.rsls$playFuture;
        if (rsls$playFuture1 != null && !rsls$playFuture1.isDone()) {
            return;
        }
        SoundManager soundManager = this.client.getSoundManager();
        SoundSystem soundSystem = ((ISoundManager) soundManager).getSoundSystem();
        SoundExecutor taskQueue = ((ISoundSystem) soundSystem).getTaskQueue();
        this.rsls$playFuture = CompletableFuture.runAsync(() -> original.call(instance), taskQueue).orTimeout(15, TimeUnit.SECONDS);
    }

}
