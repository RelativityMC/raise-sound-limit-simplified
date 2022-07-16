package com.ishland.fabric.rsls.mixin.access;

import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundSystem.class)
public interface ISoundSystem {

    @Accessor
    Channel getChannel();

    @Accessor
    SoundExecutor getTaskQueue();

}
