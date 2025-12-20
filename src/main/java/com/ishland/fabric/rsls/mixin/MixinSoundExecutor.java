package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.mixin.access.IThreadExecutor;
import net.minecraft.client.sound.SoundExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Queue;

@Mixin(SoundExecutor.class)
public abstract class MixinSoundExecutor extends ThreadExecutor<Runnable> {

    @Shadow
    private volatile boolean stopped;

    protected MixinSoundExecutor(String name) {
        super(name);
    }

    @Override
    public boolean runTask() {
        if (!this.stopped && !(((IThreadExecutor<Runnable>) this).getExecutionsInProgress() > 0)) {
            return false;
        }
        Queue<Runnable> tasks = ((IThreadExecutor<Runnable>) this).getTasks();
        Runnable runnable = tasks.poll();
        if (runnable != null) {
            this.executeTask(runnable);
            return true;
        } else {
            return false;
        }
    }

}
