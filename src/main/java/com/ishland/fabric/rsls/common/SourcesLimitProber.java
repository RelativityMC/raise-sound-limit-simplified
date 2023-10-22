package com.ishland.fabric.rsls.common;

import com.ishland.fabric.rsls.RSLSInjectorLWJGL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class SourcesLimitProber {

    public static int probeSourcesLimit() {
        RSLSInjectorLWJGL.init();
        final long device = ALC10.alcOpenDevice((String) null);
        checkALCError(device, "Open device");
        final long context = ALC10.alcCreateContext(device, (IntBuffer) null);
        ALC10.alcMakeContextCurrent(context);
        final int sourcesCount;
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            int attributes_length = ALC10.alcGetInteger(device, ALC10.ALC_ATTRIBUTES_SIZE);
            checkALCError(device, "Get attributes length");
            IntBuffer attributes = memoryStack.mallocInt(attributes_length);
            ALC10.alcGetIntegerv(device, ALC10.ALC_ALL_ATTRIBUTES, attributes);
            checkALCError(device, "Get attributes");
            sourcesCount = getSourcesCount(attributes);
        }
        ALC10.alcMakeContextCurrent(0L);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
        return sourcesCount;
    }

    private static int getSourcesCount(IntBuffer attributes) {
        attributes.position(0);
        while (attributes.hasRemaining()) {
            int key = attributes.get();
            int value = attributes.get();
            if (key == ALC11.ALC_MONO_SOURCES) {
                return value;
            }
        }
        return 30;
    }

    private static void checkALCError(long device, String message) {
        final int error = ALC10.alcGetError(device);
        if (error != 0) throw new RuntimeException("%s failed: %s".formatted(message, getAlcErrorMessage(error)));
    }

    private static String getAlcErrorMessage(int errorCode) {
        switch(errorCode) {
            case 40961:
                return "Invalid device.";
            case 40962:
                return "Invalid context.";
            case 40963:
                return "Illegal enum.";
            case 40964:
                return "Invalid value.";
            case 40965:
                return "Unable to allocate memory.";
            default:
                return "An unrecognized error occurred.";
        }
    }

}
