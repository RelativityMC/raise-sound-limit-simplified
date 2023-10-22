package com.ishland.fabric.rsls.common.cloth_config;

import com.ishland.fabric.rsls.common.RSLSConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;

public class ConfigScreenUtils {

    public static Screen makeConfigScreen(Screen parent) {
        final ConfigBuilder builder = ConfigBuilder.create();
        builder.setParentScreen(parent)
                .setTitle(Text.of("RSLS Config"))
                .setSavingRunnable(() -> {
                    MinecraftClient.getInstance().getSoundManager().reloadSounds();
                    RSLSConfig.saveConfig();
                });
        final ConfigCategory category = builder.getOrCreateCategory(Text.of("Settings"));
        category.addEntry(
                builder.entryBuilder().startIntSlider(Text.of("Total sound limit"), RSLSConfig.maxSourcesCount, 32, RSLSConfig.probedMaxSourcesCount)
                        .setDefaultValue(RSLSConfig.probedMaxSourcesCount)
                        .setSaveConsumer(integer -> RSLSConfig.maxSourcesCount = integer)
                        .build()
        );
        category.addEntry(
                builder.entryBuilder().startIntSlider(Text.of("Streaming sound limit"), RSLSConfig.maxStreamingSources, 8, RSLSConfig.probedMaxSourcesCount)
                        .setDefaultValue(8)
                        .setSaveConsumer(integer -> RSLSConfig.maxStreamingSources = integer)
                        .build()
        );
        return builder.build();
    }

    public static ButtonWidget getConfigButton(Screen screen) {
        ButtonWidget widget = null;
        try {
            widget = ButtonWidget.builder(Text.of("RSLS Config"), button -> {
                MinecraftClient.getInstance().setScreen(makeConfigScreen(screen));
            }).dimensions(screen.width - 90, 8, 80, 20).build();
        } catch (NoSuchMethodError e) {
            try {
                final Constructor<ButtonWidget> constructor = ButtonWidget.class.getConstructor(int.class, int.class, int.class, int.class, Text.class, ButtonWidget.PressAction.class);
                widget = constructor.newInstance(screen.width - 90, 8, 80, 20, Text.of("RSLS Config"), (ButtonWidget.PressAction) button -> {
                    MinecraftClient.getInstance().setScreen(makeConfigScreen(screen));
                });
            } catch (Throwable t) {
                LogManager.getLogger("RSLS").error("Failed to inject RSLS config button", t);
            }
        }
        return widget;
    }

}
