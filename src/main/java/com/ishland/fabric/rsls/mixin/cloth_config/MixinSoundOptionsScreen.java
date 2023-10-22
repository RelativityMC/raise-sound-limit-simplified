package com.ishland.fabric.rsls.mixin.cloth_config;

import com.ishland.fabric.rsls.common.RSLSConfig;
import com.ishland.fabric.rsls.common.cloth_config.ConfigScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;

@Mixin(SoundOptionsScreen.class)
public abstract class MixinSoundOptionsScreen extends GameOptionsScreen {

    public MixinSoundOptionsScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        final ButtonWidget widget = ConfigScreenUtils.getConfigButton(this);
        if (widget != null)
            this.addDrawableChild(widget);
    }

}
