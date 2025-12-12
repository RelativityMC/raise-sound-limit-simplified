package com.ishland.fabric.rsls.mixin;

import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SoundExecutor.class)
public abstract class MixinSoundExecutor extends ThreadExecutor<Runnable> {

    protected MixinSoundExecutor(String name) {
        super(name);
    }

    @Override
    public boolean runTask() {
        synchronized (this) {
            return super.runTask();
        }
    }

    @Override
    protected void cancelTasks() {
        synchronized (this) {
            super.cancelTasks();
        }
    }

}
