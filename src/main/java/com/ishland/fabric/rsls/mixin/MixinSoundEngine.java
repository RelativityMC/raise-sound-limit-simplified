package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.SoundEngine;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SoundEngine.class)
public class MixinSoundEngine {

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
