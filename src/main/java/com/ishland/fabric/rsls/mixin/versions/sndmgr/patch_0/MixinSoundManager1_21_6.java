package com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_0;

import com.google.common.collect.Sets;
import com.ishland.fabric.rsls.common.SoundManagerDuck;
import com.ishland.fabric.rsls.common.SoundSystemDuck;
import com.ishland.fabric.rsls.mixin.access.ISoundSystem;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager1_21_6 {


    @Shadow @Final
    private SoundSystem soundSystem;

    @Shadow public abstract void pauseAllExcept(SoundCategory... categories);

    @Shadow
    @Final
    private static Logger LOGGER;

}
