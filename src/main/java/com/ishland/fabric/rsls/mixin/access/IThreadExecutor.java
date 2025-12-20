package com.ishland.fabric.rsls.mixin.access;

import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Queue;

@Mixin(ThreadExecutor.class)
public interface IThreadExecutor<R> {

    @Accessor
    Queue<R> getTasks();

    @Accessor
    int getExecutionsInProgress();

}
