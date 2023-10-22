package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.RSLSOptions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(SoundOptionsScreen.class)
public class MixinSoundOptionsScreen {

    @ModifyReturnValue(method = "getOptions", at = @At("RETURN"))
    private static SimpleOption<?>[] appendRSLSOptions(SimpleOption<?>[] original) {
        final SimpleOption<?>[] copy = Arrays.copyOf(original, original.length + 2);
        copy[copy.length - 2] = RSLSOptions.maxSourcesCount;
        copy[copy.length - 1] = RSLSOptions.maxStreamingSources;
        return copy;
    }

    @Inject(method = "method_19855", at = @At("RETURN"))
    private void flushChanges(CallbackInfo ci) {
        if (RSLSOptions.maxSourcesCountChanged.compareAndSet(true, false)) {
            MinecraftClient.getInstance().getSoundManager().reloadSounds();
        }
    }

}
