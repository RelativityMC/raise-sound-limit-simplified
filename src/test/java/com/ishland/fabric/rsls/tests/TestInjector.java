package com.ishland.fabric.rsls.tests;

import com.ishland.fabric.rsls.RSLSInjectorLWJGL;
import com.ishland.fabric.rsls.common.RSLSConstants;
import com.ishland.fabric.rsls.common.SourcesLimitProber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.lwjgl.openal.ALC10;

public class TestInjector {

    @Test
    @EnabledIf("hasDevice")
    public void testInjectedResult() {
        RSLSInjectorLWJGL.init();
        Assertions.assertEquals(RSLSConstants.expectedMaxSourcesCount, SourcesLimitProber.probeSourcesLimit());
    }


    public boolean hasDevice() {
        RSLSInjectorLWJGL.init();
        final long device = ALC10.alcOpenDevice((String) null);
        final int error = ALC10.alcGetError(device);
        if (error != 0) return false;
        ALC10.alcCloseDevice(device);
        return true;
    }

}
