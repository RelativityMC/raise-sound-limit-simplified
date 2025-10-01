package com.ishland.fabric.rsls.mixin;

import com.ishland.fabric.rsls.common.EarlyMultiVersionUtil;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RSLSMixinPlugin implements IMixinConfigPlugin {

    private static final boolean POST_1_21_5;
    private static final boolean POST_1_21_8;

    static {
        ModInfo minecraft = EarlyMultiVersionUtil.loadingModList.getMods().stream().filter(modInfo -> modInfo.getModId().equals("minecraft")).findFirst().get();
        POST_1_21_5 = minecraft.getVersion().getMajorVersion() >= 1 && minecraft.getVersion().getMinorVersion() >= 21 && minecraft.getVersion().getIncrementalVersion() > 5;
        POST_1_21_8 = minecraft.getVersion().getMajorVersion() >= 1 && minecraft.getVersion().getMinorVersion() >= 21 && minecraft.getVersion().getIncrementalVersion() > 8;
    }

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("com.ishland.fabric.rsls.mixin.cloth_config."))
            return EarlyMultiVersionUtil.loadingModList.getMods().stream().anyMatch(modInfo1 -> modInfo1.getModId().equals("cloth_config"));
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_0.MixinSoundManager1_21_5"))
            return !POST_1_21_5;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_0.MixinSoundManager1_21_6"))
            return POST_1_21_5;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_1.MixinSoundManager1_21_8"))
            return !POST_1_21_8;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.sndmgr.patch_1.MixinSoundManager1_21_9"))
            return POST_1_21_8;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.MixinSoundSystem1_21_5"))
            return !POST_1_21_5;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.MixinSoundSystem1_21_6"))
            return POST_1_21_5;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.MixinMusicTracker"))
            return POST_1_21_5;
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
