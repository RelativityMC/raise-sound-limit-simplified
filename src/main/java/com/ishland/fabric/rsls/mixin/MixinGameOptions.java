package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.RSLSOptions;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class MixinGameOptions {

    @Inject(method = "accept", at = @At("RETURN"))
    private void appendRSLSOptions(GameOptions.Visitor visitor, CallbackInfo ci) {
        visitor.accept("rsls_maxSourcesCount", RSLSOptions.maxSourcesCount);
        visitor.accept("rsls_maxStreamingSources", RSLSOptions.maxStreamingSources);
    }

}
