package com.ishland.fabric.rsls.common;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

import java.util.concurrent.atomic.AtomicBoolean;

public class RSLSOptions {

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

    public static final AtomicBoolean maxSourcesCountChanged = new AtomicBoolean(false);

    public static final SimpleOption<Integer> maxSourcesCount = new SimpleOption<>(
            "options.rsls.maxSourcesCount",
            SimpleOption.emptyTooltip(),
            GameOptions::getGenericValueText,
            new SimpleOption.ValidatingIntSliderCallbacks(32, probedMaxSourcesCount),
            probedMaxSourcesCount,
            integer -> maxSourcesCountChanged.set(true)
    );

    public static final SimpleOption<Integer> maxStreamingSources = new SimpleOption<>(
            "options.rsls.maxStreamingSources",
            SimpleOption.emptyTooltip(),
            GameOptions::getGenericValueText,
            new SimpleOption.ValidatingIntSliderCallbacks(8, probedMaxSourcesCount),
            8,
            integer -> maxSourcesCountChanged.set(true)
    );
}
