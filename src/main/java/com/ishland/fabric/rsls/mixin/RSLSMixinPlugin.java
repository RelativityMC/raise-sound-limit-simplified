package com.ishland.fabric.rsls.mixin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class RSLSMixinPlugin implements IMixinConfigPlugin {

    private static final boolean PRE_1_19;
    private static final boolean PRE_1_20_3;
    private static final boolean POST_1_20_3;
    private static final boolean POST_1_21_5;

    static {
        try {
            PRE_1_19 = VersionPredicate.parse("<1.19").test(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion());
            PRE_1_20_3 = VersionPredicate.parse("<=1.20.2").test(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion());
            POST_1_20_3 = VersionPredicate.parse(">1.20.2").test(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion());
            POST_1_21_5 = VersionPredicate.parse(">1.21.5").test(FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion());
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
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
            return FabricLoader.getInstance().isModLoaded("cloth-config");
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.MixinSubtitlesHud_1_20_3"))
            return POST_1_20_3;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.MixinSubtitlesHud_1_20_2"))
            return PRE_1_20_3;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.MixinBiomeEffectSoundPlayer"))
            return !PRE_1_19;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.MixinSoundManager1_21_5"))
            return !POST_1_21_5;
        if (mixinClassName.equals("com.ishland.fabric.rsls.mixin.versions.MixinSoundManager1_21_6"))
            return POST_1_21_5;
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
