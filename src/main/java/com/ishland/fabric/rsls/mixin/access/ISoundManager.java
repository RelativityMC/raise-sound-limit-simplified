package com.ishland.fabric.rsls.mixin.access;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundManager.class)
public interface ISoundManager {

    @Accessor
    SoundSystem getSoundSystem();

}
