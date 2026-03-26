package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.WorldRandomHolder;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSoundInstance.class)
public class MixinAbstractSoundInstance {

    @ModifyVariable(method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/util/math/random/Random;)V", at = @At("HEAD"), argsOnly = true)
    private static Random onInit(Random value) {
        if (WorldRandomHolder.isWorldRandom(value)) {
            return Random.create();
        }
        return value;
    }

}
