package com.ishland.fabric.rsls.mixin.access;

import net.minecraft.client.sound.SoundExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundExecutor.class)
public interface ISoundExecutor {

    @Accessor
    Thread getThread();

}
