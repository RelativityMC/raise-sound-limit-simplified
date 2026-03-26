package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.WorldRandomHolder;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
public class MixinAbstractSoundInstance {

    @Shadow
    protected Random random;

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false)
    private void onInit(CallbackInfo ci) {
        if (WorldRandomHolder.isWorldRandom(this.random)) {
            this.random = Random.create();
        }
    }

}
