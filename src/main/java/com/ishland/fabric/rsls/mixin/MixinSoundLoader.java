package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.SoundLoader;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Mixin(SoundLoader.class)
public class MixinSoundLoader {

    @Mutable
    @Shadow
    @Final
    private Map<Identifier, CompletableFuture<StaticSound>> loadedSounds;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void syncMap(ResourceFactory resourceFactory, CallbackInfo ci) {
        this.loadedSounds = Collections.synchronizedMap(this.loadedSounds);
    }

}
