package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.mixin.access.ISoundManager;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicInstance;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Mixin(MusicTracker.class)
public class MixinMusicTracker {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private CompletableFuture<Void> rsls$playFuture;

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void prePlay(MusicInstance instance, CallbackInfo ci) {
        CompletableFuture<Void> rsls$playFuture1 = this.rsls$playFuture;
        if (rsls$playFuture1 == null || rsls$playFuture1.isDone()) {
            ci.cancel();
        }
    }

    @WrapMethod(method = "play")
    private void wrapPlay(MusicInstance instance, Operation<Void> original) {
        SoundManager soundManager = this.client.getSoundManager();
        SoundSystem soundSystem = ((ISoundManager) soundManager).getSoundSystem();
        SoundExecutor taskQueue = ((ISoundSystem) soundSystem).getTaskQueue();
        this.rsls$playFuture = CompletableFuture.runAsync(() -> original.call(instance), taskQueue).orTimeout(15, TimeUnit.SECONDS);
    }

    @WrapOperation(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;onMusicTrackStart()V"))
    private void wrapPlayListener(ToastManager instance, Operation<Void> original) {
        this.client.execute(() -> original.call(instance));
    }

    @WrapMethod(method = "tryShowToast")
    private void wrapTryShowToast(Operation<Void> original) {
        if (this.client.isOnThread()) {
            original.call();
        } else {
            this.client.execute(original::call);
        }
    }

}
