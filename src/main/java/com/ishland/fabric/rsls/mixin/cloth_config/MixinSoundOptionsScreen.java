package com.ishland.fabric.rsls.mixin.cloth_config;

import com.ishland.fabric.rsls.common.cloth_config.ConfigScreenUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public abstract class MixinSoundOptionsScreen extends GameOptionsScreen {

    public MixinSoundOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Dynamic
    @Inject(method = {"method_25426()V", "addOptions"}, at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        final ButtonWidget widget = ConfigScreenUtils.getConfigButton(this);
        if (widget != null)
            this.addDrawableChild(widget);
    }

}
