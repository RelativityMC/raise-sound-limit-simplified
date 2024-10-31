package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.BiomeEffectSoundPlayer;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeEffectSoundPlayer.class)
public class MixinBiomeEffectSoundPlayer {

    @Mutable
    @Shadow @Final private Random random;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void replaceRandom(CallbackInfo ci) {
        this.random = Random.create();
    }

}
