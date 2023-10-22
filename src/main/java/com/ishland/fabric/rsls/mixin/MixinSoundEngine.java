package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.RSLSOptions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

    @Dynamic
    @WrapOperation(method = {"init", "method_19661"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundEngine;getMonoSourceCount()I"))
    private int modifyMaxSourceFromConfig(SoundEngine instance, Operation<Integer> operation, @Share("rsls$actualSourcesCount") LocalIntRef actualSourcesCount) {
        final int min = Math.min(operation.call(instance), RSLSOptions.maxSourcesCount.getValue());
        actualSourcesCount.set(min);
        return min;
    }

    @ModifyArg(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundEngine$SourceSetImpl;<init>(I)V", ordinal = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundEngine;getMonoSourceCount()I")
            )
    )
    private int modifyStaticSources(int maxSourceCount, @Share("rsls$actualSourcesCount") LocalIntRef actualSourcesCount, @Share("rsls$actualStaticSourcesCount") LocalIntRef actualStaticSourcesCount) {
        final int min = actualSourcesCount.get() - RSLSOptions.maxStreamingSources.getValue();
        actualStaticSourcesCount.set(min);
        return min;
    }

    @ModifyArg(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundEngine$SourceSetImpl;<init>(I)V", ordinal = 1),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundEngine;getMonoSourceCount()I")
            )
    )
    private int modifyStreamingSources(int maxSourceCount, @Share("rsls$actualSourcesCount") LocalIntRef actualSourcesCount, @Share("rsls$actualStaticSourcesCount") LocalIntRef actualStaticSourcesCount) {
        return Math.min(actualSourcesCount.get(), actualSourcesCount.get() - actualStaticSourcesCount.get());
    }


    @Dynamic
    @ModifyConstant(method = {"init", "method_19661"}, constant = @Constant(intValue = 255))
    private int modifyMaxSource(int constant) {
        if (constant == 255) {
            return Integer.MAX_VALUE;
        } else {
            return constant;
        }
    }

}
