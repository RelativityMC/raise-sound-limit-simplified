package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.WorldRandomHolder;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class, priority = 10000)
public class MixinWorld {

    @Shadow
    @Final
    public Random random;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void captureWorldRandom(CallbackInfo ci) {
        WorldRandomHolder.putWorldRandom(this.random);
    }

}
