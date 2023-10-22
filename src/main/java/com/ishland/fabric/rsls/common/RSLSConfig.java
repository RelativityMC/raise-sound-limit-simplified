package com.ishland.fabric.rsls.common;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class RSLSConfig {

    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("rsls.properties");
    public static final int probedMaxSourcesCount;

    static {
        int count;
        try {
            count = SourcesLimitProber.probeSourcesLimit();
        } catch (Throwable t) {
            System.err.println("Failed to probe max sources count, falling back to default value.");
            t.printStackTrace();
            count = RSLSConstants.expectedMaxSourcesCount;
        }
        probedMaxSourcesCount = count;
    }

    public static int maxSourcesCount = probedMaxSourcesCount;
    public static int maxStreamingSources = 8;

    static {
        loadConfig();
    }

    public static void init() {
        // intentionally empty
    }

    public static void loadConfig() {
        final Properties properties = new Properties();
        if (Files.isRegularFile(CONFIG_FILE)) {
            try (InputStream in = Files.newInputStream(CONFIG_FILE, StandardOpenOption.CREATE)) {
                properties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        maxSourcesCount = getInt(properties, "maxSourcesCount", probedMaxSourcesCount);
        maxStreamingSources = getInt(properties, "maxStreamingSources", 8);
        saveConfig();
    }

    public static void saveConfig() {
        final Properties properties = new Properties();
        properties.setProperty("maxSourcesCount", String.valueOf(maxSourcesCount));
        properties.setProperty("maxStreamingSources", String.valueOf(maxStreamingSources));
        try (OutputStream out = Files.newOutputStream(CONFIG_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            properties.store(out, "Configuration file for Raise Sound Limit Simplified");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getInt(Properties properties, String key, int def) {
        try {
            final int i = Integer.parseInt(properties.getProperty(key));
            properties.setProperty(key, String.valueOf(i));
            return i;
        } catch (NumberFormatException e) {
            properties.setProperty(key, String.valueOf(def));
            return def;
        }
    }

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
                MinecraftClient.getInstance().setScreen(RSLSConfig.makeConfigScreen(screen));
            }).dimensions(screen.width - 90, 8, 80, 20).build();
        } catch (NoSuchMethodError e) {
            try {
                final Constructor<ButtonWidget> constructor = ButtonWidget.class.getConstructor(int.class, int.class, int.class, int.class, Text.class, ButtonWidget.PressAction.class);
                widget = constructor.newInstance(screen.width - 90, 8, 80, 20, Text.of("RSLS Config"), (ButtonWidget.PressAction) button -> {
                    MinecraftClient.getInstance().setScreen(RSLSConfig.makeConfigScreen(screen));
                });
            } catch (Throwable t) {
                LogManager.getLogger("RSLS").error("Failed to inject RSLS config button", t);
            }
        }
        return widget;
    }
}
