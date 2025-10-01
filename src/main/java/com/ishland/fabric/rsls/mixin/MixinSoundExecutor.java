package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundExecutor.class)
public abstract class MixinSoundExecutor extends ThreadExecutor<Runnable> {

    @Shadow private volatile boolean stopped;

    @Shadow private Thread thread;

    protected MixinSoundExecutor(String name) {
        super(name);
    }

}
