package com.ishland.fabric.rsls;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ishland.fabric.rsls.mixin.access.ISoundManager;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RSLSMod implements ClientModInitializer {

    private static final ScheduledThreadPoolExecutor scheduler =
            new ScheduledThreadPoolExecutor(
                    1,
                    new ThreadFactoryBuilder().setNameFormat("RSLS Scheduler").setDaemon(true).build()
            );

    static {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                final MinecraftClient instance = MinecraftClient.getInstance();
                if (instance == null) return;
                final SoundManager soundManager = instance.getSoundManager();
                if (soundManager == null) return;
                final SoundSystem soundSystem = ((ISoundManager) soundManager).getSoundSystem();
                if (soundSystem == null) return;
                final Channel channel = ((ISoundSystem) soundSystem).getChannel();
                channel.tick();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 0, 20L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onInitializeClient() {
//        MixinEnvironment.getCurrentEnvironment().audit();
        RSLSInjectorLWJGL.init();
    }
}
